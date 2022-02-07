package org.stormroboticsnj.scoutingradar2022.homefragment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.stormroboticsnj.scoutingradar2022.database.MatchRepository;
import org.stormroboticsnj.scoutingradar2022.database.ObjectiveMatchData;

import java.util.List;

public class MatchViewModel extends AndroidViewModel {

    private final MatchRepository mRepository;
    private final LiveData<List<ObjectiveMatchData>> mDataList;

    public MatchViewModel (Application app) {
        super(app);
        mRepository = new MatchRepository(app);
        mDataList = mRepository.getDataList();
    }

    public LiveData<List<ObjectiveMatchData>> getDataList() {
        return mDataList;
    }

    public void insert(ObjectiveMatchData match) {
        mRepository.insert(match);
    }

}
