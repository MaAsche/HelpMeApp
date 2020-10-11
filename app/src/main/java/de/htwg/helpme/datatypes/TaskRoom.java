package de.htwg.helpme.datatypes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

import de.htwg.helpme.datatypes.Point;

@Entity(tableName = "task")
public class TaskRoom {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "category")
    public int category;

    @ColumnInfo(name = "duedate")
    public String duedate;

    @ColumnInfo(name = "status")
    public int status;

    @ColumnInfo(name = "owner")
    @Nullable
    public int owner;


    public TaskRoom(int id, String description, int category, String duedate, int status, int owner) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.duedate = duedate;
        this.status = status;
        this.owner = owner;
    }

    @Ignore
    public TaskRoom(int id, String description, int category, String duedate, int status) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.duedate = duedate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getOwner() {
        return owner;
    }

    public int getStatus() {
        return status;
    }

    public String getDuedate() {
        return duedate;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryNumber(){return category;}

    public String getCategory() {
        switch (category) {
            case 0:
                return "Einkaufen";
            case 1:
                return "Haushalt";
            case 2:
                return "Garten";
            case 3:
                return "Sonstiges";
            default:
                return null;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return id + " - " + description + " - " + category + " - " + duedate + " - " + status;
    }
}

