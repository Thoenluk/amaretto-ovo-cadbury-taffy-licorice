package ch.thoenluk.solvers.challenge7.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Directory extends File {

    //---- Statics

    protected static final long UNDETERMINED_SIZE = -1;


    //---- Fields

    private final Map<String, File> contents = new HashMap<>();


    //---- Constructor

    public Directory(String name, File parent) {
        super(name, UNDETERMINED_SIZE, parent);
    }


    //---- Methods

    @Override
    public long getSize() {
        if (size == UNDETERMINED_SIZE) {
            size = 0;
            for (File file : contents.values()) {
                size += file.getSize();
            }
        }
        return size;
    }

    public File getContainedFile(String name) {
        if (!contents.containsKey(name)) {
            throw new IllegalArgumentException(String.format("This directory does not contain a file named %s", name));
        }
        return contents.get(name);
    }

    public Collection<File> getContainedFiles() {
        return contents.values();
    }

    public void addContainedFile(File file) {
        contents.put(file.getName(), file);
    }
}
