package ch.thoenluk.solvers.challenge20;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.ArrayList;
import java.util.List;

public class FileMixer implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final List<Node> encrypted = new ArrayList<>();

        for (String number : UtStrings.splitMultilineString(input)) {
            final Node node = new Node(UtParsing.cachedParseLong(number));
            encrypted.add(node);
        }

        for (int i = 1; i < encrypted.size(); i++) {
            final Node left = encrypted.get(i - 1);
            final Node right = encrypted.get(i);

            left.setNext(right);
            right.setPrevious(left);
        }

        final Node first = encrypted.get(0);
        final Node last = encrypted.get(encrypted.size() - 1);

        first.setPrevious(last);
        last.setNext(first);

        Node location = first;

        while (true) {
            if (location.getValue() == 0) {
                break;
            }
            else {
                location = location.getNext();
            }
        }

        mix(encrypted);

        int sum = 0;

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 1000; k++) {
                location = location.getNext();
            }
            sum += location.getValue();
        }

        return Integer.toString(sum);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final List<Node> encrypted = new ArrayList<>();

        for (String number : UtStrings.splitMultilineString(input)) {
            final long value = UtParsing.cachedParseLong(number);
            final Node node = new Node(value * 811589153);
            encrypted.add(node);
        }

        for (int i = 1; i < encrypted.size(); i++) {
            final Node left = encrypted.get(i - 1);
            final Node right = encrypted.get(i);

            left.setNext(right);
            right.setPrevious(left);
        }

        final Node first = encrypted.get(0);
        final Node last = encrypted.get(encrypted.size() - 1);

        first.setPrevious(last);
        last.setNext(first);

        Node location = first;

        while (true) {
            if (location.getValue() == 0) {
                break;
            }
            else {
                location = location.getNext();
            }
        }

        for (int i = 0; i < 10; i++) {
            mix(encrypted);
        }

        long sum = 0;

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 1000; k++) {
                location = location.getNext();
            }
            sum += location.getValue();
        }

        return Long.toString(sum);
    }

    private void mix(List<Node> encrypted) {
        for (Node node : encrypted) {
            if (node.getValue() == 0) {
                continue;
            }
            final long actualMovement = node.getValue() % (encrypted.size() - 1);

            final Node left = node.getPrevious();
            final Node right = node.getNext();
            left.setNext(right);
            right.setPrevious(left);

            Node location = node;
            if (actualMovement < 0) {
                for (int i = 0; i > actualMovement; i--) {
                    location = location.getPrevious();
                }
                final Node newLeft = location.getPrevious();

                location.setPrevious(node);
                node.setNext(location);
                node.setPrevious(newLeft);
                newLeft.setNext(node);
            }
            else {
                for (int i = 0; i < actualMovement; i++) {
                    location = location.getNext();
                }
                final Node newRight = location.getNext();

                location.setNext(node);
                node.setPrevious(location);
                node.setNext(newRight);
                newRight.setPrevious(node);
            }
        }
    }

    private static class Node {
        private Node previous;
        private Node next;
        private final long value;

        public Node(long value) {
            this.value = value;
        }

        public Node getPrevious() {
            return previous;
        }

        public void setPrevious(Node previous) {
            this.previous = previous;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public long getValue() {
            return value;
        }
    }
}
