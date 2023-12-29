package ch.thoenluk.solvers.challenge20.module;

import ch.thoenluk.ut.UtStrings;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class Module {

    //---- Static fields

    public static final boolean LOW = false;
    public static final boolean HIGH = true;


    //---- Static methods

    public static Module fromString(final String description) {
        final String[] labelAndOutputs = description.split(" -> ");
        final List<String> outputLabels = Arrays.stream(labelAndOutputs[1].split(", ")).toList();

        final String label = labelAndOutputs[0];
        if (label.startsWith("%")) {
            return new FlipFlopModule(label.substring(1), outputLabels);
        }
        else if (label.startsWith("&")) {
            return new ConjunctionModule(label.substring(1), outputLabels);
        }
        else {
            return new BroadcastModule(label, outputLabels);
        }
    }

    public static PowerModule createPowerModule() {
        return new PowerModule("rx", List.of());
    }


    //---- Fields

    private final String label;
    private int lowPulsesSent = 0;
    private int highPulsesSent = 0;
    protected List<Pulse> incomingPulses = new LinkedList<>();
    private final List<String> outputLabels;
    protected final List<Module> outputs = new LinkedList<>();


    //---- Constructor

    protected Module(final String label, final List<String> outputLabels) {
        this.label = label;
        this.outputLabels = outputLabels;
    }


    //---- Methods

    public List<? extends Module> initialise(final List<Module> network) {
        network.stream()
                .filter(module -> outputsInto(module.getLabel()))
                .forEach(outputs::add);
        if (outputs.size() < outputLabels.size()) {
            UtStrings.println("The bastards! Adding dummy nodes to network...");
            final List<? extends Module> addedModules = outputLabels.stream()
                    .filter(label -> outputs.stream().map(Module::getLabel).noneMatch(label::equals))
                    .map(label -> new DummyModule(label, List.of()))
                    .toList();
            outputs.addAll(addedModules);
            return addedModules;
        }
        return List.of();
    }

    public int getLowPulsesSent() {
        return lowPulsesSent;
    }

    public int getHighPulsesSent() {
        return highPulsesSent;
    }

    public String getLabel() {
        return label;
    }

    public void receivePulse(final Pulse pulse) {
        incomingPulses.add(pulse);
    }

    protected void sendToAllOutputs(final boolean level) {
        for (final Module output : outputs) {
            if (level == LOW) {
//                UtStrings.println(String.format("%s -low-> %s", getLabel(), output.getLabel()));
                lowPulsesSent++;
            }
            else {
//                UtStrings.println(String.format("%s -high-> %s", getLabel(), output.getLabel()));
                highPulsesSent++;
            }
            output.receivePulse(new Pulse(this, level));
        }
        for (final Module output : outputs) {
            output.processPulses();
        }
    }

    public void processPulses() {
        while (!incomingPulses.isEmpty()) {
            processPulse(incomingPulses.remove(0));
        }
    }

    protected boolean outputsInto(final String label) {
        return outputLabels.contains(label);
    }


    //---- Abstract methods

    protected abstract void processPulse(final Pulse pulse);

    protected abstract BigInteger encodeInternalState(final BigInteger existingState);


    //---- Internal record

    public record Pulse(Module source, boolean level) {}
}
