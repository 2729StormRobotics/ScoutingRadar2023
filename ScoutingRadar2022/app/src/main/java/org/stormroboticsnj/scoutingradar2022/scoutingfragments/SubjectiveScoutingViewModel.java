package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.stormroboticsnj.scoutingradar2022.database.DataUtils;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubjectiveScoutingViewModel extends AndroidViewModel {

    private SubjectiveRepository mSubjectiveRepository;
    private final List<DataUtils.Action> mActions;

    public SubjectiveScoutingViewModel(
            @NonNull Application application) {
        super(application);
        mSubjectiveRepository = new SubjectiveRepository(application);
        mActions = new ArrayList<>();
    }

    public void addAction(DataUtils.Action action) {
        mActions.add(action);
    }

    public void saveMatch(SubjectiveMatchData matchData) {
        mSubjectiveRepository.insert(matchData);
    }


    public void processAndSaveMatch( int teamNumber, int matchNumber, boolean isRed) {
        new Thread(() -> {
            SubjectiveMatchData data =
                    DataUtils.processSubjectiveData(mActions, teamNumber, matchNumber, isRed);
            saveMatch(data);
        }).start();
    }
}
