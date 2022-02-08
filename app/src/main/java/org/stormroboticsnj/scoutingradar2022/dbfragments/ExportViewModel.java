package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import org.stormroboticsnj.scoutingradar2022.database.DataUtils;
import org.stormroboticsnj.scoutingradar2022.database.ObjectiveRepository;
import org.stormroboticsnj.scoutingradar2022.database.PitRepository;
import org.stormroboticsnj.scoutingradar2022.database.SubjectiveRepository;

public class ExportViewModel extends AndroidViewModel {
    public ExportViewModel(@NonNull Application application) {
        super(application);
        ObjectiveRepository mObjectiveRepository = new ObjectiveRepository(application);
        SubjectiveRepository mSubjectiveRepository = new SubjectiveRepository(application);
        PitRepository mPitRepository = new PitRepository(application);

        mObjectiveLiveData = Transformations.map(mObjectiveRepository.getDataList(), DataUtils::compressData);
        mSubjectiveLiveData = Transformations.map(mSubjectiveRepository.getDataList(), DataUtils::compressData);
        mPitScoutData = Transformations.map(mPitRepository.getDataList(), DataUtils::compressData);

    }
    private final LiveData<byte[]> mObjectiveLiveData;
    private final LiveData<byte[]> mSubjectiveLiveData;
    private final LiveData<byte[]> mPitScoutData;

    public LiveData<byte[]> getmObjectiveLiveData() {
        return mObjectiveLiveData;
    }

    public LiveData<byte[]> getmSubjectiveLiveData() {
        return mSubjectiveLiveData;
    }

    public LiveData<byte[]> getmPitScoutData() {
        return mPitScoutData;
    }
}
