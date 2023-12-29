package ch.thoenluk.solvers.challenge20.module;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class PowerModule extends Module {
    private boolean isOn = false;
    private ConjunctionModule input;

    protected PowerModule(final String label, final List<String> outputLabels) {
        super(label, outputLabels);
    }

    @Override
    public List<? extends Module> initialise(final List<Module> network) {
        final Module input = network.stream()
                .filter(m -> m.outputsInto(getLabel()))
                .findFirst()
                .orElseThrow();
        if (input instanceof final ConjunctionModule conjunctionModule) {
            this.input = conjunctionModule;
        }
        return super.initialise(network);
    }

    @Override
    protected void processPulse(final Pulse pulse) {
        if (pulse.level() == LOW) {
            isOn = true;
        }
    }

    @Override
    protected BigInteger encodeInternalState(final BigInteger existingState) {
        return existingState;
    }

    public Set<Module> getHighInputStates() {
        return input.getInputsThatWereHigh();
    }

    public int getNumberOfInputs() {
        return input.getInputs().size();
    }

    public boolean isOn() {
        return isOn;
    }
}
