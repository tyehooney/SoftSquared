package com.hoonhooney.softsquared_3;

import java.util.Date;

public class Note {
    private long id;
    private String title;
    private String details;
    private Date lastEdited;
    private boolean focused;

    public Note(String title, String details) {
        this.title = title;
        this.details = details;
        this.focused = false;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Date lastEdited) {
        this.lastEdited = lastEdited;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }
}
