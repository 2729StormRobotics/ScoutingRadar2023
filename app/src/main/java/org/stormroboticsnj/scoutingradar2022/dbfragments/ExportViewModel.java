package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import org.stormroboticsnj.scoutingradar2022.database.DataUtils;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveRepository;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitRepository;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveRepository;

import java.util.List;

public class ExportViewModel extends AndroidViewModel {
    private final LiveData<byte[]> mObjectiveLiveData;
    private final LiveData<byte[]> mSubjectiveLiveData;
    private final LiveData<byte[]> mPitScoutData;
    private final ObjectiveRepository mObjectiveRepository;
    private final SubjectiveRepository mSubjectiveRepository;
    private final PitRepository mPitRepository;

    public ExportViewModel(@NonNull Application application) {
        super(application);
        mObjectiveRepository = new ObjectiveRepository(application);
        mSubjectiveRepository = new SubjectiveRepository(application);
        mPitRepository = new PitRepository(application);

        mObjectiveLiveData = Transformations.map(mObjectiveRepository.getDataList(),
                DataUtils::compressData);
        mSubjectiveLiveData = Transformations.map(mSubjectiveRepository.getDataList(),
                DataUtils::compressData);
        mPitScoutData =
                Transformations.map(mPitRepository.getDataList(), DataUtils::compressData);

    }

    public LiveData<byte[]> getmObjectiveLiveData() {
        return mObjectiveLiveData;
    }

    public LiveData<byte[]> getmSubjectiveLiveData() {
        return mSubjectiveLiveData;
    }

    public LiveData<byte[]> getmPitScoutData() {
        return mPitScoutData;
    }

    public List<ObjectiveMatchData> getDataList() {
        return mObjectiveRepository.getRawData();
    }
}
