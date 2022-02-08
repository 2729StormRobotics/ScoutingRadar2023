package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import org.stormroboticsnj.scoutingradar2022.database.DataUtils;
import org.stormroboticsnj.scoutingradar2022.database.ObjectiveRepository;
import org.stormroboticsnj.scoutingradar2022.database.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.PitRepository;
import org.stormroboticsnj.scoutingradar2022.database.PitScoutData;
import org.stormroboticsnj.scoutingradar2022.database.SubjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.SubjectiveRepository;

import java.util.List;

public class ExportViewModel extends AndroidViewModel {
    public ExportViewModel(@NonNull Application application) {
        super(application);
        mObjectiveRepository = new ObjectiveRepository(application);
        mSubjectiveRepository = new SubjectiveRepository(application);
        mPitRepository = new PitRepository(application);

        mSubjectiveLiveData = mSubjectiveRepository.getDataList();
        mPitScoutData = mPitRepository.getDataList();

        mObjectiveLiveData = Transformations.map(mObjectiveRepository.getDataList(), data -> DataUtils.compressObjectiveMatchData(data));
    }
    private LiveData<List<byte[]>> mObjectiveLiveData;
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
