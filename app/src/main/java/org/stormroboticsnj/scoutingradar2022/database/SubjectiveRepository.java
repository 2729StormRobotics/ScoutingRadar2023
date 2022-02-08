package org.stormroboticsnj.scoutingradar2022.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SubjectiveRepository {

    private final SubjectiveMatchDao mDao;
    private final LiveData<List<SubjectiveMatchData>> mDataList;

    public SubjectiveRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        mDao = db.subjectiveMatchDao();
        mDataList = mDao.getAllMatches();
    }

    public LiveData<List<SubjectiveMatchData>> getDataList() {
        return mDataList;
    }

    public void insert(SubjectiveMatchData match) {
        AppDatabase.databaseWriteExecutor.execute(() -> mDao.insert(match));
    }

}
