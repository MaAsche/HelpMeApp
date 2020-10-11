package de.htwg.helpme.room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import de.htwg.helpme.datatypes.TaskRoom;

@Database(entities = {TaskRoom.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "helpme_db";

    public abstract TaskDao taskDao();
}
