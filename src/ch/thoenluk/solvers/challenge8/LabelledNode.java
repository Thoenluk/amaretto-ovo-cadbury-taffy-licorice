package ch.thoenluk.solvers.challenge8;

import java.util.HashMap;
import java.util.Map;

public class LabelledNode {
    private static final Map<String, LabelledNode> network = new HashMap<>();

    public static LabelledNode integrate(String description) {
        final LabelledNode node = new LabelledNode(description);
        network.put(node.label, node);
        return node;
    }

    public static LabelledNode getFromNetwork(String label) {
        return network.get(label);
    }

    private LabelledNode left = null;
    private LabelledNode right = null;

    private final String label;
    private final String leftLabel;
    private final String rightLabel;

    private LabelledNode(String line) {
        final String[] pieces = line.replaceAll("\\W+", " ").split(" ");
        label = pieces[0];
        leftLabel = pieces[1];
        rightLabel = pieces[2];
    }

    public LabelledNode getLeft() {
        if (left == null) {
            left = getFromNetwork(leftLabel);
        }
        return left;
    }

    public LabelledNode getRight() {
        if (right == null) {
            right = getFromNetwork(rightLabel);
        }
        return right;
    }

    public boolean isStart() {
        return label.endsWith("A");
    }

    public boolean isEnd() {
        return label.endsWith("Z");
    }
}
