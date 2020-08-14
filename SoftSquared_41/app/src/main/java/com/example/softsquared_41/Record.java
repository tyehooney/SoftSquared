package com.example.softsquared_41;

public class Record {
    private String name;
    private int level;
    private String time;

    public Record(String name, int level, String time) {
        this.name = name;
        this.level = level;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getTime() {
        return time;
    }
}
