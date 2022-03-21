package org.stormroboticsnj.scoutingradar2022.database.pit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.stormroboticsnj.scoutingradar2022.database.AppDatabase;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

public class PitRepository {

    private final PitScoutDao mDao;
    private final LiveData<List<PitScoutData>> mDataList;

    public PitRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        mDao = db.pitScoutDao();
        mDataList = mDao.getLiveTeams();
    }

    public LiveData<List<PitScoutData>> getDataList() {
        return mDataList;
    }

    public Maybe<List<PitScoutData>> getAllTeams() {return mDao.getAllTeams();}

    public void insert(@NonNull PitScoutData data) {
        AppDatabase.databaseWriteExecutor.execute(() -> mDao.insert(data));
    }

    public Completable deleteAllData() {
        return mDao.deleteAll();
    }
}
