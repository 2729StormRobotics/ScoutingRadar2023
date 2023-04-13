package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import android.app.Application;
import android.os.SystemClock;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.stormroboticsnj.scoutingradar2022.database.DataUtils;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectiveScoutingViewModel extends AndroidViewModel {

    private final MutableLiveData<List<DataUtils.Action>> mActionsLiveData;
    private final List<DataUtils.Action> mActions;
    private final ObjectiveRepository mRepository;
    private boolean mIsMatchRunning = false;
    private long mChronometerBase = -1;

    public ObjectiveScoutingViewModel(Application app) {
        super(app);
        mActionsLiveData = new MutableLiveData<>();
        mActions = new ArrayList<>();
        mActionsLiveData.setValue(Collections.unmodifiableList(mActions));
        mRepository = new ObjectiveRepository(app);
    }

    public boolean isMatchRunning() {
        return mIsMatchRunning;
    }

    public long getChronometerBase() {
        if (mChronometerBase == -1) {
            mChronometerBase = SystemClock.elapsedRealtime();
            mIsMatchRunning = true;
        }
        return mChronometerBase;
    }

    public LiveData<List<DataUtils.Action>> getActionsLiveData() {
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

    private void saveMatch(ObjectiveMatchData matchData) {
        mRepository.insert(matchData);
    }

    public void processAndSaveMatch(List<DataUtils.Action> actions, int teamNumber, int matchNumber, String initials, String notes, boolean isRed) {
        mIsMatchRunning = false;
        new Thread(() -> {
            ObjectiveMatchData data =
                    DataUtils.processObjectiveMatchData(actions, teamNumber, matchNumber, initials, notes, isRed);
            saveMatch(data);
        }).start();
    }


}
