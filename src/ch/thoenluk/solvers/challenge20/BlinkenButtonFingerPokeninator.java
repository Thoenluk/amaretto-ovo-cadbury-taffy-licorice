package ch.thoenluk.solvers.challenge20;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.solvers.challenge20.module.Module;
import ch.thoenluk.solvers.challenge20.module.PowerModule;
import ch.thoenluk.ut.UtMath;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

public class BlinkenButtonFingerPokeninator implements ChristmasSaver {
    @Override
    public String saveChristmas(final String input) {
        final List<Module> network = new LinkedList<>(UtStrings.streamInputAsLines(input)
                .map(Module::fromString)
                .toList());
        final List<? extends Module> addedModules = network.stream()
                .map(module -> module.initialise(network))
                .flatMap(Collection::stream)
                .toList();
        network.addAll(addedModules);
        final Module broadcaster = network.stream()
                .filter(module -> module.getLabel().equals("broadcaster"))
                .findFirst()
                .orElseThrow();
        for (int i = 0; i < 1000; i++) {
            pokenTheButton(broadcaster);
        }
        final int lowPulses = network.stream()
                .map(Module::getLowPulsesSent)
                .reduce(UtMath::overflowSafeSum)
                .orElse(0) + 1000;
        final int highPulses = network.stream()
                .map(Module::getHighPulsesSent)
                .reduce(UtMath::overflowSafeSum)
                .orElse(0);
        return Integer.toString(UtMath.overflowSafeProduct(lowPulses, highPulses));
    }

    @Override
    public String saveChristmasAgain(final String input) {
        final List<Module> network = new LinkedList<>(UtStrings.streamInputAsLines(input)
                .map(Module::fromString)
                .toList());
        final PowerModule powerModule = Module.createPowerModule();
        network.add(powerModule);
        network.forEach(module -> module.initialise(network));
        final Module broadcaster = network.stream()
                .filter(module -> module.getLabel().equals("broadcaster"))
                .findFirst()
                .orElseThrow();
        int pokens = 0;
        final Map<Module, Integer> cycleTimesToHigh = new HashMap<>();
        while (!powerModule.isOn()) {
            pokenTheButton(broadcaster);
            pokens++;
            final Set<Module> inputStates = powerModule.getHighInputStates();
            for (final Module inputModule : inputStates) {
                cycleTimesToHigh.putIfAbsent(inputModule, pokens);
            }
            if (cycleTimesToHigh.size() == powerModule.getNumberOfInputs()) {
                return Long.toString(cycleTimesToHigh.values().stream()
                        .mapToLong(Long::valueOf)
                        .reduce(UtMath::superOverflowSafeProduct)
                        .orElseThrow());
            }
        }
        // If this solution doesn't work for you...
        // Too bad!
        // I could make a significantly more complex solution which doesn't assume all the following:
        // 1. rx receives a LOW if and exactly if it is output by exactly one module outputting into it.
        // 2. The module outputting into rx is a Conjunction Module
        // 3. For each module outputting into rx's proxy input:
        // 3a. The module will output HIGH on cycle N if and exactly if N = k * F, where F is the first cycle in which
        //     it had ever output HIGH
        // 3b. To point this out again though it's contained in 3a: There is no pre-cycle warmup time.
        // 3c: The module will not output LOW before each other input module has output HIGH.
        //     Meaning that if all of these modules output HIGH at SOME POINT in the cycle, the inputStates memory will
        //     be all HIGH.
        //        This is a reasonable assumption to doubt since ALL of them will output LOW before the cycle ends.
        //        It is more than plausible that a module may output HIGH, LOW, LOW in one cycle, but then HIGH, LOW,
        //        skipping the middle LOW because of FlipFlop magic (or outputting HIGH, HIGH, LOW) which would break this.
        //
        // It is at this point tradition that AoC contains a reverse engineering challenge. I knew this going in, and
        // it didn't even take that long to solve. I want to mention that generating this kind of Module machine can't
        // be trivial, so good job coding that.
        //
        // It's also tradition for a lot of people to get very upset at the reverse engineering challenge, myself included.
        // Just go to the AoC subreddit and search for "20", I'm sure you'll find some.
        //
        // Fact of the matter is, software engineering in general and every other day of AoC teaches us not to create hacks,
        // solutions which depend on specific input constellations or implementation details of other classes (in this case,
        // the other class is the module machine.) Sure, fit your solutions to possible inputs, but in this case, rx
        // depending on a FlipFlop is very much a possible input that this solution cannot solve.
        //
        // However! Solving the problem for the general case would require, or at least not be much simpler than,
        // simulating the entire machine until rx turns on, i.e. back to the incalculable simulation again.
        // So to be done before the heat death of the universe, a hack is *required*. This challenge requires bad engineering.
        // You see the problem with that?
        throw new IllegalStateException("You somehow reached the end state before determining its cycle?");
    }

    private void pokenTheButton(final Module broadcaster) {
        broadcaster.receivePulse(new Module.Pulse(null, Module.LOW));
        broadcaster.processPulses();
    }
}
