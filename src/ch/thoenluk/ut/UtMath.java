package ch.thoenluk.ut;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UtMath {
    public static int overflowSafeSum(int... summands) {
        int result = 0;
        for (int summand : summands) {
            if (Integer.MAX_VALUE - result < summand) {
                throw new AssertionError("Sum would overflow! Use a long instead.");
            }
            result += summand;
        }
        return result;
    }

    public static long superOverflowSafeSum(long... summands) {
        long result = 0;
        for (long summand : summands) {
            if (Long.MAX_VALUE - result < summand) {
                throw new AssertionError();
            }
            result += summand;
        }
        return result;
    }

    public static int overflowSafeProduct(int... multiplicands) {
        int result = 1;
        for (int multiplicand : multiplicands) {
            if (Integer.MAX_VALUE / result <= multiplicand) {
                throw new AssertionError("Product would overflow! Use a long instead.");
            }
            result *= multiplicand;
        }
        return result;
    }

    public static long superOverflowSafeProduct(long... multiplicands) {
        long result = 1;
        for (long multiplicand : multiplicands) {
            if (Long.MAX_VALUE / result <= multiplicand) {
                throw new AssertionError("Product would overflow! Use a long instead.");
            }
            result *= multiplicand;
        }
        return result;
    }

    public static int wrap(int number, int borderToWrapOn) {
        while (number < 1) {
            number += borderToWrapOn;
        }

        number -= 1;
        number %= borderToWrapOn;
        number += 1;
        return number;
    }

    public static String restOfTheOwl(Stream<Integer> stream) {
        return stream.reduce(UtMath::overflowSafeSum)
                .orElseThrow()
                .toString();
    }

    public static String restOfTheOwl(IntStream stream) {
        return restOfTheOwl(stream.boxed());
    }
}
