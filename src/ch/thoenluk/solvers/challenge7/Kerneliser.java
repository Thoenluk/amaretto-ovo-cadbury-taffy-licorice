package ch.thoenluk.solvers.challenge7;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.solvers.challenge7.model.Command;
import ch.thoenluk.solvers.challenge7.model.Directory;
import ch.thoenluk.solvers.challenge7.model.File;

import java.util.LinkedList;
import java.util.List;

public class Kerneliser implements ChristmasSaver {
    @Override
    public String saveChristmas(String input) {
        final List<Command> commands = Command.fromTerminalInputs(input);
        final Directory root = new Directory("/", null);
        File location = root;

        for (Command command : commands) {
            location = command.apply(location);
        }

        final List<Directory> directoriesToSearch = new LinkedList<>();
        directoriesToSearch.add(root);

        long totalSizes = 0;

        while (!directoriesToSearch.isEmpty()) {
            final Directory directory = directoriesToSearch.remove(0);
            final long size = directory.getSize();

            if (size <= 100000) {
                totalSizes += size;
            }

            for (File file : directory.getContainedFiles()) {
                if (file instanceof Directory subdirectory) {
                    directoriesToSearch.add(subdirectory);
                }
            }
        }

        return Long.toString(totalSizes);
    }

    @Override
    public String saveChristmasAgain(String input) {
        final List<Command> commands = Command.fromTerminalInputs(input);
        final Directory root = new Directory("/", null);
        File location = root;

        for (Command command : commands) {
            location = command.apply(location);
        }

        final List<Directory> directoriesToSearch = new LinkedList<>();
        directoriesToSearch.add(root);

        final long unusedSpace = 70000000 - root.getSize();
        final long requiredSpace = 30000000 - unusedSpace;
        Directory smallestDeletableDirectory = root;

        while (!directoriesToSearch.isEmpty()) {
            final Directory directory = directoriesToSearch.remove(0);
            final long size = directory.getSize();

            if (size >= requiredSpace && size <= smallestDeletableDirectory.getSize()) {
                smallestDeletableDirectory = directory;
            }

            for (File file : directory.getContainedFiles()) {
                if (file instanceof Directory subdirectory) {
                    directoriesToSearch.add(subdirectory);
                }
            }
        }

        return Long.toString(smallestDeletableDirectory.getSize());
    }
}
