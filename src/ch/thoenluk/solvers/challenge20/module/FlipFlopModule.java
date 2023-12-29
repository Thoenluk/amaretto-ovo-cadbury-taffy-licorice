package ch.thoenluk.solvers.challenge20.module;

import java.math.BigInteger;
import java.util.List;

class FlipFlopModule extends Module {
    private boolean level = LOW;

    public FlipFlopModule(final String label, final List<String> outputLabels) {
        super(label, outputLabels);
    }

    @Override
    public void receivePulse(final Pulse pulse) {
        if (pulse.level() != HIGH) {
            super.receivePulse(pulse);
        }
    }

    @Override
    protected void processPulse(final Pulse pulse) {
        level = !level;
        sendToAllOutputs(level);
    }

    @Override
    protected BigInteger encodeInternalState(final BigInteger existingState) {
        final BigInteger internalState = level ? BigInteger.ONE : BigInteger.ZERO;
        return existingState.add(internalState).multiply(BigInteger.TWO);
    }
}
