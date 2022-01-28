package org.stormrobotics.scoutingradar2022.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//defines a database that uses the match entity
@Database(entities = {ObjectiveMatchData.class, SubjectiveMatchData.class, PitScoutMatchData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    /* Singleton Pattern
    'volatile' ensures that there is always one instance of the database
    do not want copies to be made */
    private static volatile AppDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public AppDatabase() {}

    public abstract ObjectiveMatchDao objectiveMatchDao();
    public abstract SubjectiveMatchDao subjectiveMatchDao();
    public abstract PitScoutMatchDao pitScoutMatchDao();

    public static synchronized AppDatabase getInstance(Context context) {
        //returns the single instance of the database
        //if it does not exist, creates one
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "RadarDatabase.db").build();
        }
        return instance;
    }
}
