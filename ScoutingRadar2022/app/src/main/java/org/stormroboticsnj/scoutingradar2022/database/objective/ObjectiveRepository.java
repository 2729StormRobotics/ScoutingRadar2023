package org.stormroboticsnj.scoutingradar2022.database.objective;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.stormroboticsnj.scoutingradar2022.database.AppDatabase;

import java.util.List;

import io.reactivex.rxjava3.core.Maybe;

public class ObjectiveRepository {

    private final ObjectiveMatchDao mDao;
    private final LiveData<List<ObjectiveMatchData>> mDataList;

    public ObjectiveRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        mDao = db.objectiveMatchDao();
        mDataList = mDao.getLiveMatches();
    }

    public LiveData<List<ObjectiveMatchData>> getLiveMatches() {
        return mDataList;
    }

    public void insert(@NonNull ObjectiveMatchData match) {
        AppDatabase.databaseWriteExecutor.execute(() -> mDao.insert(match));
    }

    public Maybe<List<ObjectiveMatchData>> getAllMatches() {
        return mDao.getAllMatches();
    }
}
