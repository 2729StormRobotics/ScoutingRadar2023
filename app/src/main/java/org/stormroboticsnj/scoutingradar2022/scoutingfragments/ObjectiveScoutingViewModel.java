package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.stormroboticsnj.scoutingradar2022.database.DataProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectiveScoutingViewModel extends ViewModel {

    private final MutableLiveData<List<DataProcessor.Action>> actionsLiveData;
    private final List<DataProcessor.Action> actionList;

    public ObjectiveScoutingViewModel() {
        actionsLiveData = new MutableLiveData<>();
        actionList = new ArrayList<>();
        actionsLiveData.setValue(Collections.unmodifiableList(actionList));
    }

    public LiveData<List<DataProcessor.Action>> getLiveData() {
        return actionsLiveData;
    }

    public void addAction(DataProcessor.Action action) {
        actionList.add(action);
        actionsLiveData.setValue(Collections.unmodifiableList(actionList));
    }
    public void removeLastAction() {
        if (actionList.size() > 0) {
            actionList.remove(actionList.size() - 1);
            actionsLiveData.setValue(Collections.unmodifiableList(actionList));
        }
    }


}
