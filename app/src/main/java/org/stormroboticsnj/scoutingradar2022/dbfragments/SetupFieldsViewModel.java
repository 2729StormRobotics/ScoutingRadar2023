package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.stormroboticsnj.scoutingradar2022.database.field.Field;
import org.stormroboticsnj.scoutingradar2022.database.field.FieldRepository;

import java.util.List;

public class SetupFieldsViewModel extends AndroidViewModel {

    private final FieldRepository mRepository;
    private final LiveData<List<Field>> mDataList;

    public SetupFieldsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new FieldRepository(application);
        mDataList = mRepository.getDataList();
    }

    public LiveData<List<Field>> getDataList() {
        return mDataList;
    }

    public void insert(Field field) {
        mRepository.insert(field);
    }

    public void delete(Field field) {
        mRepository.delete(field);
    }
}
