package ch.thoenluk.solvers.challenge5;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Almanacinator implements ChristmasSaver {
    @Override
    public String saveChristmas(final String input) {
        final String[] almanac = UtStrings.splitStringWithEmptyLines(input);

        final List<List<MapLine>> maps = Arrays.stream(almanac)
                .skip(1)
                .map(this::parseMap)
                .toList();

        return Arrays.stream(almanac[0].split(UtStrings.WHITE_SPACE_REGEX))
                .skip(1)
                .map(UtParsing::cachedParseLong)
                .map(seed -> getLocation(seed, maps))
                .reduce(Long::min)
                .orElseThrow()
                .toString();
    }

    private List<MapLine> parseMap(final String description) {
        return Arrays.stream(UtStrings.splitMultilineString(description))
                .skip(1)
                .map(MapLine::fromString)
                .sorted(Comparator.comparingLong(m -> m.sourceRange().start()))
                .collect(Collectors.toList());
    }

    private long getLocation(final long seed, final List<List<MapLine>> maps) {
        long result = seed;
        for (final List<MapLine> map : maps) {
            result = lookup(result, map);
        }
        return result;
    }

    private long lookup(final long source, List<MapLine> map) {
        final Optional<MapLine> mapLineOptional = map.stream()
                .filter(m -> m.sourceRange().contains(source))
                .findFirst();

        if (mapLineOptional.isPresent()) {
            final MapLine mapLine = mapLineOptional.get();
            final long offset = source - mapLine.sourceRange().start();
            return mapLine.destinationRange().start() + offset;
        }

        return source;
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] almanac = UtStrings.splitStringWithEmptyLines(input);

        final List<Range> relevantRanges = new LinkedList<>(Pattern.compile("\\d+ \\d+").matcher(almanac[0]).results()
                .map(MatchResult::group)
                .map(Range::fromString)
                .sorted(Comparator.comparingLong(Range::start))
                .toList());

        Arrays.stream(almanac)
                .skip(1)
                .map(this::parseMap)
                .forEach(map -> mapRelevantRanges(map, relevantRanges));

        return Long.toString(relevantRanges.stream()
                .min(Comparator.comparingLong(Range::start))
                .orElseThrow()
                .start());
    }

    private void mapRelevantRanges(final List<MapLine> map, final List<Range> relevantRanges) {
        final List<Range> mappedRanges = new LinkedList<>();
        for (final MapLine mapLine : map) {
            final List<Range> leftoverRanges = new LinkedList<>();
            relevantRanges.stream()
                    .filter(mapLine.sourceRange()::overlaps)
                    .map(mapLine::map)
                    .forEach(mappingResult -> {
                        mappedRanges.add(mappingResult.mappedRange());
                        leftoverRanges.addAll(mappingResult.leftovers());
                    });
            relevantRanges.removeIf(mapLine.sourceRange()::overlaps);
            relevantRanges.addAll(leftoverRanges);
        }
        relevantRanges.addAll(mappedRanges);
    }

    private record MapLine(Range destinationRange, Range sourceRange) {
        public static MapLine fromString(final String description) {
            return fromList(Arrays.stream(description.split(UtStrings.WHITE_SPACE_REGEX))
                    .map(UtParsing::cachedParseLong)
                    .toList());
        }

        private static MapLine fromList(final List<Long> values) {
            return new MapLine(new Range(values.get(0), values.get(0) + values.get(2) - 1), new Range(values.get(1), values.get(1) + values.get(2) - 1));
        }

        public MappingResult map(final Range range) {
            if (!range.overlaps(sourceRange())) {
                throw new IllegalArgumentException();
            }
            final Range sourceUnion = sourceRange().union(range);
            final long startOffset = sourceUnion.start() - sourceRange().start();
            final long mappedStart = destinationRange().start() + startOffset;
            final Range mappedRange = new Range(mappedStart, mappedStart + sourceUnion.length() - 1);
            return new MappingResult(mappedRange, range.remove(sourceUnion));
        }
    }

    private record Range(long start, long end) {
        public static Range fromString(final String description) {
            return fromList(Arrays.stream(description.split(UtStrings.WHITE_SPACE_REGEX))
                    .map(UtParsing::cachedParseLong)
                    .toList());
        }

        private static Range fromList(final List<Long> values) {
            return new Range(values.get(0), values.get(0) + values.get(1) - 1);
        }

        public long length() {
            return end() - start();
        }

        public boolean contains(final long value) {
            return start() <= value && end() >= value;
        }

        public boolean overlaps(final Range other) {
            return start() <= other.end() && end() >= other.start();
        }

        public Range union(final Range other) {
            return new Range(Math.max(start(), other.start()), Math.min(end(), other.end()));
        }

        public List<Range> remove(final Range other) {
            final List<Range> result = new LinkedList<>();
            if (start() < other.start()) {
                result.add(new Range(start(), other.start() - 1));
            }
            if (end() > other.end()) {
                result.add(new Range(other.end() + 1, end()));
            }
            return result;
        }
    }

    private record MappingResult(Range mappedRange, List<Range> leftovers) {}
}
