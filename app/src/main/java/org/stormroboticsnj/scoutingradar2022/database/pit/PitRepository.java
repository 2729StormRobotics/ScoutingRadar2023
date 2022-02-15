package org.stormroboticsnj.scoutingradar2022.database.pit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.stormroboticsnj.scoutingradar2022.database.AppDatabase;

import java.util.List;

public class PitRepository {

    private final PitScoutDao mDao;
    private final LiveData<List<PitScoutData>> mDataList;

    public PitRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        mDao = db.pitScoutDao();
        mDataList = mDao.getAllMatches();
    }

    public LiveData<List<PitScoutData>> getDataList() {
        return mDataList;
    }

    public void insert(@NonNull PitScoutData data) {
        AppDatabase.databaseWriteExecutor.execute(() -> mDao.insert(data));
    }

}
