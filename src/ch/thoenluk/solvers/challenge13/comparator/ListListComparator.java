package ch.thoenluk.solvers.challenge13.comparator;

import java.util.Comparator;
import java.util.List;

public class ListListComparator implements Comparator<List<?>> {
    @Override
    public int compare(List<?> o1, List<?> o2) {
        for (int i = 0; i < o1.size() && i < o2.size(); i++) {
            final Object left = o1.get(i);
            final Object right = o2.get(i);

            if (left instanceof final Integer leftInt) {
                if (right instanceof final Integer rightInt) {
                    final int order = leftInt - rightInt;
                    if (order != 0) {
                        return order;
                    }

                }
                else if (right instanceof final List rightList) {
                    final int order = compare(List.of(leftInt), rightList);
                    if (order != 0) {
                        return order;
                    }
                }
            }
            else if (left instanceof final List leftList) {
                if (right instanceof final Integer rightInt) {
                    final int order = compare(leftList, List.of(rightInt));
                    if (order != 0) {
                        return order;
                    }
                }
                else if (right instanceof final List rightList) {
                    final int order = compare(leftList, rightList);
                    if (order != 0) {
                        return order;
                    }
                }
            }
        }

        return o1.size() - o2.size();
    }
}
