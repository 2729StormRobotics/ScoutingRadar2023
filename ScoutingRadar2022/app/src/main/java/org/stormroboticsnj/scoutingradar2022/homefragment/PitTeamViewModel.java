package org.stormroboticsnj.scoutingradar2022.homefragment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import org.stormroboticsnj.scoutingradar2022.database.pit.PitRepository;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitScoutData;

import java.util.List;

public class PitTeamViewModel extends AndroidViewModel {

    private final PitRepository mRepository;
    private final LiveData<List<PitScoutData>> mDataList;

    public PitTeamViewModel (Application app) {
        super(app);
        mRepository = new PitRepository(app);
        mDataList = mRepository.getDataList();
    }

    public LiveData<List<PitScoutData>> getDataList() {
        return mDataList;
    }

    public void insert(PitScoutData team) {
        mRepository.insert(team);
    }

}
