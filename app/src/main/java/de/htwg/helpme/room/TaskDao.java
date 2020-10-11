package de.htwg.helpme.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.htwg.helpme.datatypes.TaskRoom;


@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    List<TaskRoom> getTaskList();

    @Query("DELETE FROM task")
    void nukeTable();

    @Query("UPDATE task SET status = :status WHERE id = :id")
    void updateStatus(int status, int id);

    @Query("SELECT * FROM task WHERE owner = :stranger")
    List<TaskRoom> getStrangerList(int stranger);


    @Query("DELETE FROM task WHERE id = :id")
    void deleteId(int id);

    @Insert
    void insertAllTasks(List<TaskRoom> tasks);


    @Insert
    void insertTask(TaskRoom taskRoom);

    @Update
    void updateTask(TaskRoom taskRoom);

    @Delete
    void deleteTask(TaskRoom taskRoom);


}
