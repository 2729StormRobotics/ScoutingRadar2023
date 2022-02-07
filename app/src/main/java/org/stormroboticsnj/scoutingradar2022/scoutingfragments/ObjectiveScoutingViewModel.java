package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.stormroboticsnj.scoutingradar2022.database.MatchRepository;
import org.stormroboticsnj.scoutingradar2022.database.ObjectiveMatchData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectiveScoutingViewModel extends AndroidViewModel {

    private final MutableLiveData<List<DataUtils.Action>> mActionsLiveData;
    private final List<DataUtils.Action> mActions;

    private final MatchRepository mRepository;

    public ObjectiveScoutingViewModel(Application app) {
        super(app);
        mActionsLiveData = new MutableLiveData<>();
        mActions = new ArrayList<>();
        mActionsLiveData.setValue(Collections.unmodifiableList(mActions));
        mRepository = new MatchRepository(app);
    }

    public LiveData<List<DataUtils.Action>> getLiveData() {
        return mActionsLiveData;
    }

    public void addAction(DataUtils.Action action) {
        mActions.add(action);
        mActionsLiveData.setValue(Collections.unmodifiableList(mActions));
    }

    public void removeLastAction() {
        if (mActions.size() > 0) {
            mActions.remove(mActions.size() - 1);
            mActionsLiveData.setValue(Collections.unmodifiableList(mActions));
        }
    }

    public void saveMatch(ObjectiveMatchData matchData) {
        mRepository.insert(matchData);
    }

    public void processAndSaveMatch(List<DataUtils.Action> actions, int teamNumber, int matchNumber, boolean isRed) {
        new Thread(() -> {
            ObjectiveMatchData data =
                    DataUtils.processObjectiveMatchData(actions, teamNumber, matchNumber, isRed);
            saveMatch(data);
        }).start();
    }


}
