package ch.thoenluk.solvers.challenge15;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeaconGateKeeper implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);

        final int rowToCheck;

        try {
            rowToCheck = UtParsing.cachedParseInt(lines[0]);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("The first line of input must be a plain number specifying which row to check!");
        }

        final Map<Position, Integer> scannerRanges = new HashMap<>();
        final Set<Position> beacons = new HashSet<>();
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;

        for (int i = 1; i < lines.length; i++) {
            final LineParseResults results = parseLine(lines[i]);

            scannerRanges.put(results.scanner(), results.scannerRange());

            beacons.add(results.beacon());

            if (minX > results.minX()) {
                minX = results.minX();
            }

            if (maxX < results.maxX()) {
                maxX = results.maxX();
            }
        }

        int positionsThatCantBeBeacons = 0;

        for (int x = minX; x <= maxX; x++) {
            if (!canBeBeacon(new Position(rowToCheck, x), scannerRanges, beacons)) {
                positionsThatCantBeBeacons++;
            }
        }

        return Integer.toString(positionsThatCantBeBeacons);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);

        final int center;

        try {
            center = UtParsing.cachedParseInt(lines[0]);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("The first line of input must be a plain number specifying which row to check!");
        }

        final Map<Position, Integer> scannerRanges = new HashMap<>();

        for (int i = 1; i < lines.length; i++) {
            final LineParseResults results = parseLine(lines[i]);

            scannerRanges.put(results.scanner(), results.scannerRange());
        }

        for (Map.Entry<Position, Integer> scannerRange : scannerRanges.entrySet()) {
            final Position distressBeacon = findDistressBeacon(scannerRange.getKey(), scannerRange.getValue(), scannerRanges, center);

            if (distressBeacon != null) {
                final long tuningFrequency = ((long) distressBeacon.x()) * 4000000 + distressBeacon.y();
                return Long.toString(tuningFrequency);
            }
        }

        return null;
    }

    private Position findDistressBeacon(Position scanner, int range, Map<Position, Integer> scannerRanges, int center) {
        final int rimDistance = range + 1;
        final Position[] rimPoints = new Position[4];

        for (int i = 0; i < rimDistance; i++) {
            rimPoints[0] = new Position(scanner.y() - rimDistance + i, scanner.x() + i);
            rimPoints[1] = new Position(scanner.y() + i, scanner.x() + rimDistance - i);
            rimPoints[2] = new Position(scanner.y() + rimDistance - i, scanner.x() - i);
            rimPoints[3] = new Position(scanner.y() - i, scanner.x() - rimDistance + i);

            for (Position rimPoint : rimPoints) {
                if (rimPoint.y() < 0
                    || rimPoint.x() < 0
                    || rimPoint.y() > center * 2
                    || rimPoint.x() > center * 2) {
                    continue;
                }

                if (isOutOfRangeOfAllScanners(rimPoint, scannerRanges)) {
                    return rimPoint;
                }
            }
        }

        return null;
    }

    private boolean canBeBeacon(Position position, Map<Position, Integer> scannerRanges, Set<Position> beacons) {
        if (beacons.contains(position)) {
            return true;
        }

        return isOutOfRangeOfAllScanners(position, scannerRanges);
    }

    private boolean isOutOfRangeOfAllScanners(Position position, Map<Position, Integer> scannerRanges) {
        for (Map.Entry<Position, Integer> scannerRange : scannerRanges.entrySet()) {
            if (scannerRange.getKey().getDistanceFrom(position) <= scannerRange.getValue()) {
                return false;
            }
        }

        return true;
    }

    private LineParseResults parseLine(String line) {
        final String commaified = line.replaceAll(":", ",");
        final String simplified = commaified.replaceAll("[^\\d,-]", "");
        final String[] digits = UtStrings.splitCommaSeparatedString(simplified);

        final Position scanner = new Position(UtParsing.cachedParseInt(digits[1]), UtParsing.cachedParseInt(digits[0]));
        final Position beacon = new Position(UtParsing.cachedParseInt(digits[3]), UtParsing.cachedParseInt(digits[2]));

        final int scannerRange = scanner.getDistanceFrom(beacon);

        final int minX = scanner.x() - scannerRange;
        final int maxX = scanner.x() + scannerRange;

        return new LineParseResults(scanner, beacon, scannerRange, minX, maxX);
    }

    private record LineParseResults(Position scanner, Position beacon, int scannerRange, int minX, int maxX) {}
}
