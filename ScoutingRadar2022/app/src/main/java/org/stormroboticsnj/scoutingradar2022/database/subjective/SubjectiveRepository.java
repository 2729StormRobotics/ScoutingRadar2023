package org.stormroboticsnj.scoutingradar2022.database.subjective;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.stormroboticsnj.scoutingradar2022.database.AppDatabase;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

public class SubjectiveRepository {

    private final SubjectiveMatchDao mDao;
    private final LiveData<List<SubjectiveMatchData>> mDataList;

    public SubjectiveRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        mDao = db.subjectiveMatchDao();
        mDataList = mDao.getLiveMatches();
    }

    public LiveData<List<SubjectiveMatchData>> getDataList() {
        return mDataList;
    }

    public Maybe<List<SubjectiveMatchData>> getAllMatches() {return mDao.getAllMatches();}

    public void insert(@NonNull SubjectiveMatchData match) {
        AppDatabase.databaseWriteExecutor.execute(() -> mDao.insert(match));
    }

    public Completable deleteAllMatches() {
        return mDao.deleteAll();
    }
}
