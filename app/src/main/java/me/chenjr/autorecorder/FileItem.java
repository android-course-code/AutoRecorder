package me.chenjr.autorecorder;

public class FileItem {
    private String name;
    private int iconID;

    public FileItem(String name, int iconID) {
        this.name = name;
        this.iconID = iconID;
    }

    public String getName() {
        return name;
    }

    public int getIconID() {
        return iconID;
    }
}
