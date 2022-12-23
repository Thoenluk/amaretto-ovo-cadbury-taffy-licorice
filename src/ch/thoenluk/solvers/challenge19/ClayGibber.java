package ch.thoenluk.solvers.challenge19;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClayGibber implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        int sum = 0;
        final String[] blueprints = UtStrings.splitMultilineString(input);
        for (int i = 0; i < blueprints.length; i++) {
            sum += (i + 1) * getPossibleGeodes(blueprints[i], 24);
        }
        return Integer.toString(sum);
    }

    @Override
    public String saveChristmasAgain(String input) {
        int product = 1;
        final String[] blueprints = UtStrings.splitMultilineString(input);
        for (int i = 0; i < 3; i++) {
            if (i < blueprints.length) {
                product *= getPossibleGeodes(blueprints[i], 32);
            }
        }
        return Integer.toString(product);
    }

    private int getPossibleGeodes(String blueprint, int time) {
        final String digitsOnly = blueprint.replaceAll("\\D+", " ").trim();
        final String[] digits = digitsOnly.split(" ");

        final int oreRobotCost = UtParsing.cachedParseInt(digits[1]);
        final int clayRobotCost = UtParsing.cachedParseInt(digits[2]);
        final int obsidianRobotOreCost = UtParsing.cachedParseInt(digits[3]);
        final int obsidianRobotClayCost = UtParsing.cachedParseInt(digits[4]);
        final int geodeRobotOreCost = UtParsing.cachedParseInt(digits[5]);
        final int geodeRobotObsidianCost = UtParsing.cachedParseInt(digits[6]);

        final Costs costs = new Costs(oreRobotCost, clayRobotCost, obsidianRobotOreCost, obsidianRobotClayCost, geodeRobotOreCost, geodeRobotObsidianCost);
        final Robots robots = new Robots(1, 0, 0);
        final Resources resources = new Resources(0, 0, 0);

        return getPossibleGeodes(time, costs, robots, resources);
    }

    private int getPossibleGeodes(int time, Costs costs, Robots robots, Resources resources) {
        final List<Integer> possibleGeodes = new ArrayList<>();

        possibleGeodes.add(getPossibleGeodesWhenBuildingOreBot(time, costs, robots, resources));
        possibleGeodes.add(getPossibleGeodesWhenBuildingClayBot(time, costs, robots, resources));
        possibleGeodes.add(getPossibleGeodesWhenBuildingObsidianBot(time, costs, robots, resources));
        possibleGeodes.add(getPossibleGeodesWhenBuildingGeodeBot(time, costs, robots, resources));

        possibleGeodes.sort(Comparator.reverseOrder());

        return possibleGeodes.get(0);
    }

    private int getPossibleGeodesWhenBuildingOreBot(int time, Costs costs, Robots robots, Resources resources) {
        final int maxUsableOre = costs.maxOreCost() * time;

        if (robots.oreRobots() * time + resources.ore() < maxUsableOre) {
            final int minutesToAffordOreBot = (int) Math.ceil(((double) costs.oreRobotOreCost() - resources.ore()) / robots.oreRobots());
            final int minutesUntilOreBotMining = minutesToAffordOreBot < 0 ? 1 : minutesToAffordOreBot + 1;

            if (minutesUntilOreBotMining < time) {
                final Resources resourcesAfterBuilding = resources.afterTime(minutesUntilOreBotMining, robots).deductOre(costs.oreRobotOreCost());
                return getPossibleGeodes(time - minutesUntilOreBotMining, costs, robots.addOreRobot(), resourcesAfterBuilding);
            }
        }

        return 0;
    }

    private int getPossibleGeodesWhenBuildingClayBot(int time, Costs costs, Robots robots, Resources resources) {
        final int maxUsableClay = costs.obsidianRobotClayCost() * time;

        if (robots.clayRobots() * time + resources.clay() < maxUsableClay) {
            final int minutesToAffordClayBot = (int) Math.ceil(((double) costs.clayRobotOreCost() - resources.ore()) / robots.oreRobots());
            final int minutesUntilClayBotMining = minutesToAffordClayBot < 0 ? 1 : minutesToAffordClayBot + 1;

            if (minutesUntilClayBotMining < time) {
                final Resources resourcesAfterBuilding = resources.afterTime(minutesUntilClayBotMining, robots).deductOre(costs.clayRobotOreCost());
                return getPossibleGeodes(time - minutesUntilClayBotMining, costs, robots.addClayRobot(), resourcesAfterBuilding);
            }
        }

        return 0;
    }

    private int getPossibleGeodesWhenBuildingObsidianBot(int time, Costs costs, Robots robots, Resources resources) {
        final int maxUsableObsidian = costs.geodeRobotObsidianCost() * time;

        if (robots.obsidianRobots() * time + resources.obsidian() < maxUsableObsidian
                && robots.clayRobots() > 0) {
            final int minutesToAffordObsidianBotOre = (int) Math.ceil(((double) costs.obsidianRobotOreCost() - resources.ore()) / robots.oreRobots());
            final int minutesUntilObsidianBotMiningFromOre = minutesToAffordObsidianBotOre < 0 ? 1 : minutesToAffordObsidianBotOre + 1;

            final int minutesToAffordObsidianBotClay = (int) Math.ceil(((double) costs.obsidianRobotClayCost() - resources.clay()) / robots.clayRobots());
            final int minutesUntilObsidianBotMiningFromClay = minutesToAffordObsidianBotClay < 0 ? 1 : minutesToAffordObsidianBotClay + 1;

            final int minutesUntilObsidianBotMining = Math.max(minutesUntilObsidianBotMiningFromOre, minutesUntilObsidianBotMiningFromClay);

            if (minutesUntilObsidianBotMining < time) {
                final Resources resourcesAfterBuilding = resources.afterTime(minutesUntilObsidianBotMining, robots)
                        .deductOre(costs.obsidianRobotOreCost())
                        .deductClay(costs.obsidianRobotClayCost());
                return getPossibleGeodes(time - minutesUntilObsidianBotMining, costs, robots.addObsidianRobot(), resourcesAfterBuilding);
            }
        }

        return 0;
    }

    private int getPossibleGeodesWhenBuildingGeodeBot(int time, Costs costs, Robots robots, Resources resources) {
        if (robots.obsidianRobots() > 0) {
            final int minutesToAffordGeodeBotOre = (int) Math.ceil(((double) costs.geodeRobotOreCost() - resources.ore()) / robots.oreRobots());
            final int minutesUntilGeodeBotMiningFromOre = minutesToAffordGeodeBotOre < 0 ? 1 : minutesToAffordGeodeBotOre + 1;

            final int minutesToAffordGeodeBotObsidian = (int) Math.ceil(((double) costs.geodeRobotObsidianCost() - resources.obsidian()) / robots.obsidianRobots());
            final int minutesUntilGeodeBotMiningFromObsidian = minutesToAffordGeodeBotObsidian < 0 ? 1 : minutesToAffordGeodeBotObsidian + 1;

            final int minutesUntilGeodeBotMining = Math.max(minutesUntilGeodeBotMiningFromOre, minutesUntilGeodeBotMiningFromObsidian);

            if (minutesUntilGeodeBotMining < time) {
                final Resources resourcesAfterBuilding = resources.afterTime(minutesUntilGeodeBotMining, robots)
                        .deductOre(costs.geodeRobotOreCost())
                        .deductObsidian(costs.geodeRobotObsidianCost());
                return time - minutesUntilGeodeBotMining + getPossibleGeodes(time - minutesUntilGeodeBotMining, costs, robots, resourcesAfterBuilding);
            }
        }

        return 0;
    }

    private record Costs(int oreRobotOreCost, int clayRobotOreCost, int obsidianRobotOreCost, int obsidianRobotClayCost, int geodeRobotOreCost, int geodeRobotObsidianCost, int maxOreCost) {
        public Costs(int oreRobotCost, int clayRobotCost, int obsidianRobotOreCost, int obsidianRobotClayCost, int geodeRobotOreCost, int geodeRobotObsidianCost) {
            this(oreRobotCost, clayRobotCost, obsidianRobotOreCost, obsidianRobotClayCost, geodeRobotOreCost, geodeRobotObsidianCost,
                    Math.max(clayRobotCost, Math.max(obsidianRobotOreCost, geodeRobotOreCost)));
        }
    }

    private record Robots(int oreRobots, int clayRobots, int obsidianRobots) {
        public Robots addOreRobot() {
            return new Robots(oreRobots() + 1, clayRobots(), obsidianRobots());
        }

        public Robots addClayRobot() {
            return new Robots(oreRobots(), clayRobots() + 1, obsidianRobots());
        }

        public Robots addObsidianRobot() {
            return new Robots(oreRobots(), clayRobots(), obsidianRobots() + 1);
        }
    }

    private record Resources(int ore, int clay, int obsidian) {
        public Resources afterTime(int time, Robots robots) {
            return new Resources(
                    ore() + time * robots.oreRobots(),
                    clay() + time * robots.clayRobots(),
                    obsidian() + time * robots.obsidianRobots()
            );
        }

        public Resources deductOre(int deduction) {
            return new Resources(ore() - deduction, clay(), obsidian());
        }

        public Resources deductClay(int deduction) {
            return new Resources(ore(), clay()- deduction, obsidian());
        }

        public Resources deductObsidian(int deduction) {
            return new Resources(ore(), clay(), obsidian() - deduction);
        }
    }
}
