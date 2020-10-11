package de.htwg.helpme.datatypes;

import androidx.annotation.NonNull;

public class GlobalTaskCount {
    private String done;
    private String open;

    public GlobalTaskCount(String done, String open) {
        this.done = done;
        this.open = open;
    }

    public String getDone() {
        return done;
    }

    public String getOpen() {
        return open;
    }

    @NonNull
    @Override
    public String toString() {
        return "done: " + done + " - open: " + open;
    }
}
