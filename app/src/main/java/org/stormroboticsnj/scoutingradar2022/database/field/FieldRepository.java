package org.stormroboticsnj.scoutingradar2022.database.field;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.stormroboticsnj.scoutingradar2022.database.AppDatabase;

import java.util.List;

public class FieldRepository {
    private final FieldDao fieldDao;
    private final LiveData<List<Field>> mDataList;

    public FieldRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        fieldDao = db.fieldDao();
        mDataList = fieldDao.getAll();
    }

    public LiveData<List<Field>> getDataList() {
        return mDataList;
    }

    public void insert(Field field) {
        AppDatabase.databaseWriteExecutor.execute(() -> fieldDao.insert(field));
    }

    public void delete(Field field) {
        AppDatabase.databaseWriteExecutor.execute(() -> fieldDao.delete(field));
    }
}
