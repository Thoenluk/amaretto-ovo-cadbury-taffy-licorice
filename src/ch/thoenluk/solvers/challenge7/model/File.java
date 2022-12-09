package ch.thoenluk.solvers.challenge7.model;

public class File {

    //---- Fields

    protected long size;
    protected final String name;
    protected final File parent;


    //---- Constructor

    public File(String name, long size, File parent) {
        this.size = size;
        this.name = name;
        this.parent = parent;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public File getParent() {
        return parent;
    }
}
