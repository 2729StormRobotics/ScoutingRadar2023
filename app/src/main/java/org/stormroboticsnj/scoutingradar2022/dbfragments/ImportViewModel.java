package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.stormroboticsnj.scoutingradar2022.database.DataUtils;
import org.stormroboticsnj.scoutingradar2022.database.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.ObjectiveRepository;
import org.stormroboticsnj.scoutingradar2022.database.PitRepository;
import org.stormroboticsnj.scoutingradar2022.database.PitScoutData;
import org.stormroboticsnj.scoutingradar2022.database.SubjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.SubjectiveRepository;

public class ImportViewModel extends AndroidViewModel {

    private final PitRepository mPitRepository;
    private final ObjectiveRepository mObjectiveRepository;
    private final SubjectiveRepository mSubjectiveRepository;

    public ImportViewModel(@NonNull Application application) {
        super(application);

        mObjectiveRepository = new ObjectiveRepository(application);
        mSubjectiveRepository = new SubjectiveRepository(application);
        mPitRepository = new PitRepository(application);
    }

    public void saveObjectiveData(byte[] data) {
        new Thread(() -> {

            String[] uncompressedData = DataUtils.extractData(data);
            for (String s : uncompressedData) {
                mObjectiveRepository.insert(ObjectiveMatchData.valueOf(s));
            }

        }).start();
    }

    public void saveSubjectiveData(byte[] data) {
        new Thread(() -> {

            String[] uncompressedData = DataUtils.extractData(data);
            for (String s : uncompressedData) {
                mSubjectiveRepository.insert(SubjectiveMatchData.valueOf(s));
            }

        }).start();
    }

    public void savePitData(byte[] data) {
        new Thread(() -> {

            String[] uncompressedData = DataUtils.extractData(data);
            for (String s : uncompressedData) {
                mPitRepository.insert(PitScoutData.valueOf(s));
            }

        }).start();
    }

}