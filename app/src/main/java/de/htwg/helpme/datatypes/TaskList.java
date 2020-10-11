package de.htwg.helpme.datatypes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class TaskList implements Serializable {
    public List<TaskRoom> tasks;

    public List<TaskRoom> getTasks(){
        return tasks;
    }

    public void setTasks(List<TaskRoom> tasks){
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public String toString() {
        return tasks.toString();
    }
}
