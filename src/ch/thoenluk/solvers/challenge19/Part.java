package ch.thoenluk.solvers.challenge19;

import ch.thoenluk.ut.UtParsing;

import java.util.Arrays;
import java.util.List;

public record Part(int x, int m, int a, int s) {
    public static Part fromString(final String description) {
        return fromList(Arrays.stream(description.replaceAll("[^\\d,]", "").split(",")).map(UtParsing::cachedParseInt).toList());
    }

    private static Part fromList(final List<Integer> fields) { // Don't touch my...
        return new Part(fields.get(0), fields.get(1), fields.get(2), fields.get(3));
    }

    public int calculateTotalRating() {
        return x() + m() + a() + s();
    }
}
