package ch.thoenluk.solvers.challenge20.module;

import java.math.BigInteger;
import java.util.List;

class DummyModule extends Module {
    protected DummyModule(final String label, final List<String> outputLabels) {
        super(label, outputLabels);
    }

    @Override
    protected void processPulse(final Pulse pulse) {

    }

    @Override
    protected BigInteger encodeInternalState(final BigInteger existingState) {
        return existingState;
    }
}
