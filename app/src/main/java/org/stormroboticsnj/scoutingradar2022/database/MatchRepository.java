package org.stormroboticsnj.scoutingradar2022.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MatchRepository {

    private final ObjectiveMatchDao mDao;
    private final LiveData<List<ObjectiveMatchData>> mDataList;

    public MatchRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        mDao = db.objectiveMatchDao();
        mDataList = mDao.getAllMatches();
    }

    public LiveData<List<ObjectiveMatchData>> getDataList() {
        return mDataList;
    }

    public void insert(ObjectiveMatchData match) {
        AppDatabase.databaseWriteExecutor.execute(() -> mDao.insert(match));
    }

}
