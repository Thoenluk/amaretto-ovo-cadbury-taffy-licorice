package ch.thoenluk.ut;

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

    public static int wrap(int number, int borderToWrapOn) {
        while (number < 1) {
            number += borderToWrapOn;
        }

        number -= 1;
        number %= borderToWrapOn;
        number += 1;
        return number;
    }
}
