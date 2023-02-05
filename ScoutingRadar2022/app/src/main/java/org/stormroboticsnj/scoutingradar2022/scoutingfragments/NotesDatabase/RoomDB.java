package org.stormroboticsnj.scoutingradar2022.scoutingfragments.NotesDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.stormroboticsnj.scoutingradar2022.scoutingfragments.SubjectiveMatchNotes;

@Database(entities = SubjectiveMatchNotes.class, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    private static String DATABASE_NAME = "NoteApp";

    public synchronized static  RoomDB getInstance(Context context){
        if (database == null){
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return database;
    }

    public abstract  MainDataAccessObject mainDataAccessObject();
}
