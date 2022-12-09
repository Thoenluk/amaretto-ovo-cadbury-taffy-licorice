package ch.thoenluk.solvers.challenge7.model;

import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.*;

public abstract class Command {

    //---- Statics

    private static final String CD = "cd";
    private static final String LS = "ls";


    //---- Static Methods

    public static List<Command> fromTerminalInputs(String terminalInputs) {
        final List<Command> commands = new LinkedList<>();
        final List<String> inputs = new LinkedList<>(Arrays.asList(UtStrings.splitMultilineString(terminalInputs)));

        while (!inputs.isEmpty()) {
            final String input = inputs.remove(0);
            final String[] parts = input.split(UtStrings.WHITE_SPACE_REGEX);

            if (parts[1].equals(CD)) {
                commands.add(new CDCommand(parts[2]));
            }
            else if (parts[1].equals(LS)) {
                final List<String> fileDescriptions = new LinkedList<>();

                while (!inputs.isEmpty() && inputs.get(0).charAt(0) != '$') {
                    fileDescriptions.add(inputs.remove(0));
                }

                commands.add(new LSCommand(fileDescriptions));
            }
            else {
                throw new IllegalStateException(String.format("Received %s as command which is nonsense!", input));
            }
        }

        return commands;
    }


    //---- Methods

    public abstract File apply(File location);


    //---- Implementing classes

    private static class CDCommand extends Command {

        //---- Fields

        private final String target;


        //---- Constructor

        CDCommand(String target) {
            this.target = target;
        }


        //---- Methods

        @Override
        public File apply(File location) {
            if (target.equals("..")) {
                return location.getParent();
            }
            else if (target.equals("/")) {
                while (location.getParent() != null) {
                    location = location.getParent();
                }
                return location;
            }

            if (location instanceof Directory directory) {
                return directory.getContainedFile(target);
            }

            throw new IllegalArgumentException(String.format("Requested contained file %s from location %s, which is not a directory!", target, location.getName()));
        }
    }

    private static class LSCommand extends Command {

        //---- Statics

        private static final String DIRECTORY = "dir";


        //---- Fields

        private final List<String> fileDescriptions;


        //---- Constructor

        LSCommand(List<String> fileDescriptions) {
            this.fileDescriptions = fileDescriptions;
        }


        //---- Methods

        @Override
        public File apply(File location) {
            if (!(location instanceof final Directory directory)) {
                throw new IllegalArgumentException(String.format("ls was applied to file %s which is not a directory!", location.getName()));
            }

            final Map<String, File> contents = new HashMap<>();

            for (String fileDescription : fileDescriptions) {
                final String[] parts = fileDescription.split(UtStrings.WHITE_SPACE_REGEX);

                if (parts[0].equals(DIRECTORY)) {
                    directory.addContainedFile(new Directory(parts[1], directory));
                }
                else {
                    directory.addContainedFile(new File(parts[1], UtParsing.cachedParseLong(parts[0]), directory));
                }
            }

            return location;
        }
    }
}
