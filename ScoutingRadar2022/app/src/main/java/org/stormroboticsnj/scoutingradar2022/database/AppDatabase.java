package org.stormroboticsnj.scoutingradar2022.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchDao;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitScoutDao;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitScoutData;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveMatchDao;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveMatchData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//defines a database that uses the match entity
@Database(entities = {ObjectiveMatchData.class, SubjectiveMatchData.class, PitScoutData.class},
          version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    /* Singleton Pattern
    'volatile' ensures that there is always one instance of the database
    do not want copies to be made */
    private static volatile AppDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public AppDatabase() {}

    public abstract ObjectiveMatchDao objectiveMatchDao();

    public abstract SubjectiveMatchDao subjectiveMatchDao();

    public abstract PitScoutDao pitScoutDao();

    public static AppDatabase getInstance(final Context context) {
        //returns the single instance of the database
        //if it does not exist, creates one
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, AppDatabase.class,
                            "RadarDatabase.db").addCallback(CALLBACK_PREPOPULATE).build();
                }
            }
        }
        return instance;
    }

    private static final RoomDatabase.Callback CALLBACK_PREPOPULATE = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
//
//            databaseWriteExecutor.execute(() -> {
//                ObjectiveMatchDao dao = instance.objectiveMatchDao();
//                dao.deleteAll();
//
//                ObjectiveMatchData data = new ObjectiveMatchData(2729, 1);
//                dao.insert(data);
//                data = new ObjectiveMatchData(2722, 2);
//                dao.insert(data);
//                data = new ObjectiveMatchData(2720, 3);
//                dao.insert(data);
//
//                SubjectiveMatchDao dao2 = instance.subjectiveMatchDao();
//                dao2.deleteAll();
//
//                SubjectiveMatchData data2 = new SubjectiveMatchData(2729, 1, true, "Sub Data 1");
//                dao2.insert(data2);
//                data2 = new SubjectiveMatchData(2722, 2, true, "Sub Data 2");
//                dao2.insert(data2);
//                data2 = new SubjectiveMatchData(2729, 1, true, "Sub Data 1");
//                dao2.insert(data2);
//
//                PitScoutDao dao3 = instance.pitScoutDao();
//                dao3.deleteAll();
//                PitScoutData data3 = new PitScoutData(2729, "Pit Data 1");
//                dao3.insert(data3);
//
//
//            });

        }
    };
}
