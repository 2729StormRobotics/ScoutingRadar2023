package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.stormroboticsnj.scoutingradar2022.database.ObjectiveRepository;
import org.stormroboticsnj.scoutingradar2022.database.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.PitRepository;
import org.stormroboticsnj.scoutingradar2022.database.PitScoutData;
import org.stormroboticsnj.scoutingradar2022.database.SubjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.SubjectiveRepository;

import java.util.List;

public class ImportViewModel extends AndroidViewModel {
    public ImportViewModel(@NonNull Application application) {
        super(application);
        mObjectiveRepository = new ObjectiveRepository(application);
        mSubjectiveRepository = new SubjectiveRepository(application);
        mPitRepository = new PitRepository(application);

        mObjectiveLiveData = mObjectiveRepository.getDataList();
        mSubjectiveLiveData = mSubjectiveRepository.getDataList();
        mPitScoutData = mPitRepository.getDataList();
    }
    private LiveData<List<ObjectiveMatchData>> mObjectiveLiveData;
    private LiveData<List<SubjectiveMatchData>> mSubjectiveLiveData;
    private LiveData<List<PitScoutData>> mPitScoutData;

    private ObjectiveRepository mObjectiveRepository;
    private SubjectiveRepository mSubjectiveRepository;
    private PitRepository mPitRepository;

    public LiveData<List<ObjectiveMatchData>> getmObjectiveLiveData() {
        return mObjectiveLiveData;
    }

    public LiveData<List<SubjectiveMatchData>> getmSubjectiveLiveData() {
        return mSubjectiveLiveData;
    }

    public LiveData<List<PitScoutData>> getmPitScoutData() {
        return mPitScoutData;
    }
}
