package ch.thoenluk.ut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtParsing {
    private static final Map<String, Integer> STRING_INTEGER_CACHE = new HashMap<>();
    private static final Map<String, Long> STRING_LONG_CACHE = new HashMap<>();
    private static final Map<Character, Integer> CHAR_INTEGER_CACHE = new HashMap<>();

    public static int cachedParseInt(String stringRepresentation) {
        return STRING_INTEGER_CACHE.computeIfAbsent(stringRepresentation, Integer::parseInt);
    }

    public static int cachedParseInt(String stringRepresentation, int radix) {
        if (!STRING_INTEGER_CACHE.containsKey(stringRepresentation)) {
            STRING_INTEGER_CACHE.put(stringRepresentation, Integer.parseInt(stringRepresentation, radix));
        }

        return STRING_INTEGER_CACHE.get(stringRepresentation);
    }

    public static long cachedParseLong(String stringRepresentation) {
        return STRING_LONG_CACHE.computeIfAbsent(stringRepresentation, Long::parseLong);
    }

    public static long cachedParseLong(String stringRepresentation, int radix) {
        if (!STRING_LONG_CACHE.containsKey(stringRepresentation)) {
            STRING_LONG_CACHE.put(stringRepresentation, Long.parseLong(stringRepresentation, radix));
        }

        return STRING_LONG_CACHE.get(stringRepresentation);
    }

    public static int cachedGetNumericValue(char charRepresentation) {
        return CHAR_INTEGER_CACHE.computeIfAbsent(charRepresentation, Character::getNumericValue);
    }

    public static List<Integer> multilineStringToIntegerList(String stringRepresentation) {
        String[] lines = UtStrings.splitMultilineString(stringRepresentation);
        List<Integer> parsedList = new ArrayList<>(lines.length);
        for (String line : lines) {
            parsedList.add(cachedParseInt(line));
        }
        return parsedList;
    }

    public static Map<Position, Character> multilineStringToPositionCharacterMap(String stringRepresentation) {
        Map<Position, Character> map = new HashMap<>();
        int y, x;

        String[] lines = UtStrings.splitMultilineString(stringRepresentation);

        for (y = 0; y < lines.length; y++) {
            for (x = 0; x < lines[y].length(); x++) {
                map.put(new Position(y, x), lines[y].charAt(x));
            }
        }

        return map;
    }

    public static Map<Position, Integer> multilineStringToPositionIntegerMap(String stringRepresentation) {
        Map<Position, Integer> map = new HashMap<>();
        int y, x;

        String[] lines = UtStrings.splitMultilineString(stringRepresentation);

        for (y = 0; y < lines.length; y++) {
            for (x = 0; x < lines[y].length(); x++) {
                map.put(new Position(y, x), cachedGetNumericValue(lines[y].charAt(x)));
            }
        }

        return map;
    }

    public static List<Integer> commaSeparatedStringToIntegerList(String csv) {
        String[] tokens = UtStrings.splitCommaSeparatedString(csv);
        List<Integer> parsedList = new ArrayList<>();
        for (String token : tokens) {
            parsedList.add(cachedParseInt(token));
        }
        return parsedList;
    }
}
