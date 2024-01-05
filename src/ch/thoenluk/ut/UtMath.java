package ch.thoenluk.ut;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class UtMath {
    public static int overflowSafeSum(final int... summands) {
        int result = 0;
        for (final int summand : summands) {
            if (Integer.MAX_VALUE - result < summand) {
                throw new AssertionError("Sum would overflow! Use a long instead.");
            }
            result += summand;
        }
        return result;
    }

    public static long superOverflowSafeSum(final long... summands) {
        long result = 0;
        for (final long summand : summands) {
            if (summand > 0 && result > 0 && Long.MAX_VALUE - result < summand) {
                throw new AssertionError(String.format("Would overflow for result %d and summand %d!", result, summand));
            }
            result += summand;
        }
        return result;
    }

    public static int overflowSafeProduct(final int... multiplicands) {
        int result = 1;
        for (final int multiplicand : multiplicands) {
            if (Integer.MAX_VALUE / result <= multiplicand) {
                throw new AssertionError("Product would overflow! Use a long instead.");
            }
            result *= multiplicand;
        }
        return result;
    }

    public static long superOverflowSafeProduct(final long... multiplicands) {
        long result = 1;
        for (final long multiplicand : multiplicands) {
            if (Long.MAX_VALUE / result <= multiplicand) {
                throw new AssertionError("Product would overflow! Panic.");
            }
            result *= multiplicand;
        }
        return result;
    }

    public static int wrap(int number, final int borderToWrapOn) {
        while (number < 1) {
            number += borderToWrapOn;
        }

        number -= 1;
        number %= borderToWrapOn;
        number += 1;
        return number;
    }

    public static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        if (a < b) {
            final long holder = a;
            a = b;
            b = holder;
        }
        return gcd(b, a % b);
    }

    public static long gcd(final List<Long> numbers) {
        return numbers.stream().reduce(numbers.get(0), UtMath::gcd);
    }

    public static long lcm(final long a, final long b) {
        return a * (b / gcd(a, b));
    }

    public static long lcm(final List<Long> numbers) {
        return numbers.stream().reduce(numbers.get(0), UtMath::lcm);
    }

    public static String restOfTheOwl(final Stream<Integer> stream) {
        return stream.reduce(UtMath::overflowSafeSum)
                .orElseThrow()
                .toString();
    }

    public static String restOfTheOwl(final IntStream stream) {
        return restOfTheOwl(stream.boxed());
    }

    public static String restOfTheLongOwl(final Stream<Long> stream) {
        return stream.reduce(UtMath::superOverflowSafeSum)
                .orElseThrow()
                .toString();
    }

    public static String restOfTheLongOwl(final LongStream stream) {
        return restOfTheLongOwl(stream.boxed());
    }
}
