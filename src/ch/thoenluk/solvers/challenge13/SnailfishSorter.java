package ch.thoenluk.solvers.challenge13;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.solvers.challenge13.comparator.ListListComparator;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SnailfishSorter implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final String[] pairs = UtStrings.splitStringWithEmptyLines(input);
        final ListListComparator listListComparator = new ListListComparator();
        int index = 1;
        int indicesSum = 0;

        for (String pair : pairs) {
            final String[] packets = UtStrings.splitMultilineString(pair);
            final List<Object> firstPacket = parsePacket(packets[0]);
            final List<Object> secondPacket = parsePacket(packets[1]);

            if (listListComparator.compare(firstPacket, secondPacket) < 0) {
                indicesSum += index;
            }
            index++;
        }

        return Integer.toString(indicesSum);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] lines = UtStrings.splitMultilineString(input);
        final List<List<Object>> packets = new ArrayList<>();

        for (String line : lines) {
            if (!line.isEmpty()) {
                packets.add(parsePacket(line));
            }
        }

        final List<Object> decoderTwo = parsePacket("[[2]]");
        final List<Object> decoderSix = parsePacket("[[6]]");

        packets.add(decoderTwo);
        packets.add(decoderSix);

        packets.sort(new ListListComparator());

        final int decoderTwoIndex = packets.indexOf(decoderTwo) + 1;
        final int decoderSixIndex = packets.indexOf(decoderSix) + 1;

        return Integer.toString(decoderTwoIndex * decoderSixIndex);
    }

    private List<Object> parsePacket(String packetDescription) {
        final List<Character> characters = new LinkedList<>();

        final String hexed = packetDescription.replaceAll("10", "A");
        final String commaless = hexed.replaceAll(",", "");
        for (Character current : commaless.toCharArray()) {
            characters.add(current);
        }

        characters.remove(0);

        return parseList(characters);
    }

    private List<Object> parseList(List<Character> characters) {
        final List<Object> packet = new LinkedList<>();

        while(!characters.isEmpty()) {
            final Character next = characters.remove(0);
            switch(next) {
                case '[' -> packet.add(parseList(characters));
                case ']' -> {
                    return packet;
                }
                case 'A' -> packet.add(10);
                default -> packet.add(UtParsing.cachedParseInt(next.toString()));
            }
        }

        throw new IllegalStateException("List was not terminated at any point by a ] character!");
    }
}
