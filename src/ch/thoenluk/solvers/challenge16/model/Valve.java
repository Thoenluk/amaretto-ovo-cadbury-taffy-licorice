package ch.thoenluk.solvers.challenge16.model;

import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

public class Valve {

    //---- Static methods

    public static Valve fromDescription(String description) {
        final String[] words = description.split(UtStrings.WHITE_SPACE_REGEX);

        final String name = words[1];

        final int flowRate = UtParsing.cachedParseInt(words[4].replaceAll("\\D", ""));

        final Valve valve = new Valve(name, flowRate);

        for (int otherValveIndex = 9; otherValveIndex < words.length; otherValveIndex++) {
            final String cleanedName = words[otherValveIndex].replaceAll(",", "");
            valve.addNeighbour(cleanedName);
            valve.setDistanceToOtherValve(cleanedName, 1);
        }

        return valve;
    }

    public static void plotDistances(Map<String, Valve> valves) {
        for (Valve valve : valves.values()) {
            final List<String> valveNamesToExplore = new LinkedList<>(valve.getNeighbours());

            while (!valveNamesToExplore.isEmpty()) {
                final Valve valveToExplore = valves.get(valveNamesToExplore.remove(0));
                final int distance = valve.getDistanceToOtherValve(valveToExplore.getName());

                for (String neighbourName : valveToExplore.getNeighbours()) {
                    if (!neighbourName.equals(valve.getName())
                        && valve.getDistanceToOtherValve(neighbourName) == null) {
                        valve.setDistanceToOtherValve(neighbourName, distance + 1);
                        valveNamesToExplore.add(neighbourName);
                    }
                }
            }
        }
    }


    //---- Fields

    private final String name;

    private final List<String> neighbours = new LinkedList<>();

    private final Map<String, Integer> distancesToOtherValves = new HashMap<>();

    private final int flowRate;

    private boolean isOpen = false;


    //---- Constructor

    public Valve(String name, int flowRate) {
        this.name = name;
        this.flowRate = flowRate;
    }


    //---- Methods

    public String getName() {
        return name;
    }

    public List<String> getNeighbours() {
        return neighbours;
    }

    public void addNeighbour(String neighbour) {
        neighbours.add(neighbour);
    }

    public Integer getDistanceToOtherValve(String name) {
        return distancesToOtherValves.get(name);
    }

    public void setDistanceToOtherValve(String name, int distance) {
        distancesToOtherValves.put(name, distance);
    }

    public int getFlowRate() {
        return flowRate;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
