package org.com.example.mp3player;

public class MusicFile {
    private String name;
    private String path;
    private boolean isSelected;


    public MusicFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
