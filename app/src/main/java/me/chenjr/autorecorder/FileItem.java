package me.chenjr.autorecorder;

public class FileItem {
    private String name;
    private int iconID;
    private long size;


    public FileItem(String name, int iconID, long size) {
        this.name = name;
        this.iconID = iconID;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getIconID() {
        return iconID;
    }

    public long getSize() {
        return size/1024;
    }
}
