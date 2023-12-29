package ch.thoenluk.solvers.challenge20.module;

import java.math.BigInteger;
import java.util.*;

class ConjunctionModule extends Module {
    private final List<Module> inputs = new LinkedList<>();
    private final Map<Module, Boolean> inputStates = new HashMap<>();
    private final Set<Module> inputsThatWereHigh = new HashSet<>();

    protected ConjunctionModule(final String label, final List<String> outputLabels) {
        super(label, outputLabels);
    }

    @Override
    protected void processPulse(final Pulse pulse) {
        inputStates.put(pulse.source(), pulse.level());
        if (pulse.level() == HIGH) {
            inputsThatWereHigh.add(pulse.source());
        }
        sendToAllOutputs(!inputStates.values().stream().allMatch(Boolean::booleanValue));
    }

    @Override
    protected BigInteger encodeInternalState(final BigInteger existingState) {
        BigInteger output = existingState;
        for (final Module input : inputs) {
            if (inputStates.get(input)) {
                output = output.add(BigInteger.ONE);
            }
            output = output.multiply(BigInteger.TWO);
        }
        return output;
    }

    @Override
    public List<? extends Module> initialise(final List<Module> network) {
        collectInputs(network);
        return super.initialise(network);
    }

    public void collectInputs(final List<Module> network) {
        inputs.addAll(network.stream().filter(this::outputsIntoThis).toList());
        inputs.forEach(module -> inputStates.put(module, LOW));
    }

    public List<Module> getInputs() {
        return inputs;
    }

    public Set<Module> getInputsThatWereHigh() {
        final Set<Module> result = new HashSet<>(inputsThatWereHigh);
        inputsThatWereHigh.clear();
        return result;
    }

    private boolean outputsIntoThis(final Module module) {
        return module.outputsInto(getLabel());
    }
}
