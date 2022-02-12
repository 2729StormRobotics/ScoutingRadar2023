package org.stormroboticsnj.scoutingradar2022.scoutingfragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveRepository;

public class SubjectiveScoutingViewModel extends AndroidViewModel {

    private SubjectiveRepository mSubjectiveRepository;
    public SubjectiveScoutingViewModel(
            @NonNull Application application) {
        super(application);
        mSubjectiveRepository = new SubjectiveRepository(application);
    }
}
