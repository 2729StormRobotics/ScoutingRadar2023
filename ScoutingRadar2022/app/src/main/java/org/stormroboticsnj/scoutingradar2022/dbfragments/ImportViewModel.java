package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.app.Application;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.stormroboticsnj.scoutingradar2022.database.DataUtils;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveRepository;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitRepository;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitScoutData;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveRepository;

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
                ObjectiveMatchData obj = ObjectiveMatchData.valueOf(s);
                if (obj != null) {
                    mObjectiveRepository.insert(obj);
                }
            }

        }).start();
    }

    public void saveSubjectiveData(byte[] data) {
        new Thread(() -> {

            String[] uncompressedData = DataUtils.extractData(data);
            for (String s : uncompressedData) {
                SubjectiveMatchData sub = SubjectiveMatchData.valueOf(s);
                if (sub != null) {
                    mSubjectiveRepository.insert(sub);
                }
            }

        }).start();
    }

    public void savePitData(byte[] data) {
        new Thread(() -> {

            String[] uncompressedData = DataUtils.extractData(data);
            for (String s : uncompressedData) {
                PitScoutData pit = PitScoutData.valueOf(s);
                if (pit != null) {
                    mPitRepository.insert(pit);
                }
            }

        }).start();
    }

    public void saveQrData(final String res) {
        new Thread(() -> {
            switch (res.substring(0, 3)) {
                case "OBJ":
                    saveObjectiveData(
                            Base64.decode(res.substring(3), Base64.NO_WRAP));
                    break;
                case "SUB":
                    saveSubjectiveData(
                            Base64.decode(res.substring(3), Base64.NO_WRAP));
                    break;
                case "PIT":
                    savePitData(
                            Base64.decode(res.substring(3), Base64.NO_WRAP));
                    break;
            }
        }).start();

    }

}
