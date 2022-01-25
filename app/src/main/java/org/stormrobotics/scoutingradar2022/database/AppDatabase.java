package org.stormrobotics.scoutingradar2022.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//defines a database that uses the match entity
@Database(entities = {ObjectiveMatchData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    /* Singleton Pattern
    'volatile' ensures that there is always one instance of the database
    do not want copies to be made */
    private static volatile AppDatabase instance;

    public AppDatabase() {}

    public static synchronized AppDatabase getInstance(Context context) {
        //returns the single instance of the database
        //if it does not exist, creates one
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "RadarDatabase.db").build();
        }
        return instance;
    }
}
