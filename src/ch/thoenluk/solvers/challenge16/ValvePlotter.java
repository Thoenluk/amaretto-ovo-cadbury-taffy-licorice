package ch.thoenluk.solvers.challenge16;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.solvers.challenge16.model.Valve;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

public class ValvePlotter implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final Map<String, Valve> valves = new HashMap<>();
        final String[] lines = UtStrings.splitMultilineString(input);

        for (String line : lines) {
            final Valve valve = Valve.fromDescription(line);
            valves.put(valve.getName(), valve);
        }

        Valve.plotDistances(valves);

        Valve start = valves.get("AA");

        valves.entrySet().removeIf(entry -> entry.getValue().getFlowRate() == 0);

        final List<Valve> bestPath = getBestPath(0, new ArrayList<>(valves.values()), start, 30);
        return Integer.toString(getPressureReleasedForPath(bestPath, start, 30));
    }

    @Override
    public String saveChristmasAgain(String input) {
        final Map<String, Valve> valves = new HashMap<>();
        final String[] lines = UtStrings.splitMultilineString(input);

        for (String line : lines) {
            final Valve valve = Valve.fromDescription(line);
            valves.put(valve.getName(), valve);
        }

        Valve.plotDistances(valves);

        Valve start = valves.get("AA");

        valves.entrySet().removeIf(entry -> entry.getValue().getFlowRate() == 0);

        final List<Valve> valveList = new ArrayList<>(valves.values());

        final List<Integer> indices = new ArrayList<>();
        final int half = valveList.size() / 2;

        for (int i = 0; i < half; i++) {
            indices.add(i);
        }

        int bestPressureReleased = Integer.MIN_VALUE;

        while (!indices.isEmpty()) {
            int last = indices.get(indices.size() - 1);

            if (last < valveList.size()) {
                final List<Valve> valvesIOpen = new LinkedList<>();

                for (Integer index : indices) {
                    valvesIOpen.add(valveList.get(index));
                }

                final List<Valve> valvesElephantOpens = new LinkedList<>(valveList);
                valvesElephantOpens.removeAll(valvesIOpen);

                final List<Valve> bestPathForMe = getBestPath(0, valvesIOpen, start, 26);
                final List<Valve> bestPathForElephant = getBestPath(0, valvesElephantOpens, start, 26);

                final int pressureReleased = getPressureReleasedForPath(bestPathForMe, start, 26)
                        + getPressureReleasedForPath(bestPathForElephant, start, 26);

                if (bestPressureReleased < pressureReleased) {
                    bestPressureReleased = pressureReleased;
                }
            }

            last++;

            if (last < valveList.size()) {
                indices.set(indices.size() - 1,  last);
                if (indices.size() < half) {
                    indices.add(last + 1);
                }
            }
            else {
                indices.remove(indices.size() - 1);
                if (indices.size() == 1) {
                    System.out.print(".");
                }
            }
        }

        return Integer.toString(bestPressureReleased);
    }

    private List<Valve> getBestPath(int n, List<Valve> path, Valve start, int time) {
        int bestPressureReleased = Integer.MIN_VALUE;
        List<Valve> bestPath = new ArrayList<>();
        if (getTimeTakenForSubpath(n, path, start) < time) {
            for (int i = n; i < path.size(); i++) {
                Collections.swap(path, i, n);

                final List<Valve> permutation = getBestPath(n + 1, path, start, time);
                final int pressureReleased = getPressureReleasedForPath(permutation, start, time);

                if (bestPressureReleased < pressureReleased) {
                    bestPressureReleased = pressureReleased;
                    bestPath = new ArrayList<>(permutation);
                }

                Collections.swap(path, n, i);
            }
        }

        final int pressureReleased = getPressureReleasedForPath(path, start, time);

        if (bestPressureReleased < pressureReleased) {
            bestPath = new ArrayList<>(path);
        }

        return bestPath;
    }

    private int getTimeTakenForSubpath(int n, List<Valve> path, Valve start) {
        int timeTaken = 0;
        Valve location = start;
        for (int i = 0; i < n; i++) {
            final Valve target = path.get(i);
            timeTaken += location.getDistanceToOtherValve(target.getName());
            location = target;
        }
        return timeTaken;
    }

    private int getPressureReleasedForPath(List<Valve> path, Valve start, int time) {
        int timeRemaining = time;
        int pressureReleased = 0;
        Valve location = start;

        for (Valve valve : path) {
            final int timeToOpen = location.getDistanceToOtherValve(valve.getName()) + 1;

            if (timeToOpen > timeRemaining) {
                break;
            }

            timeRemaining -= timeToOpen;
            pressureReleased += valve.getFlowRate() * timeRemaining;
            location = valve;
        }

        return pressureReleased;
    }
}
