package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.stormroboticsnj.scoutingradar2022.database.DataUtils;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitRepository;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitScoutData;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PitScoutViewModel extends AndroidViewModel {

    private final PitRepository mSubjectiveRepository;
    private final List<DataUtils.Action> mActions;

    public PitScoutViewModel(@NonNull Application application) {
        super(application);
        mSubjectiveRepository = new PitRepository(application);
        mActions = new ArrayList<>();
    }

    public void addAction(DataUtils.Action action) {
        mActions.add(action);
    }

    public void saveMatch(PitScoutData data) {
        mSubjectiveRepository.insert(data);
    }

    public void processAndSaveData(int teamNumber, String notes, String motorInfo, String motorInfo2) {
        new Thread(() -> {
            PitScoutData data =
                    DataUtils.processPitData(mActions, notes, motorInfo, motorInfo2, teamNumber);
            saveMatch(data);
        }).start();
    }
}
