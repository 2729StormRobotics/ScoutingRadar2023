package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.app.Application;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import org.stormroboticsnj.scoutingradar2022.database.CSVCreator;
import org.stormroboticsnj.scoutingradar2022.database.DataUtils;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.objective.ObjectiveRepository;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitRepository;
import org.stormroboticsnj.scoutingradar2022.database.pit.PitScoutData;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveMatchData;
import org.stormroboticsnj.scoutingradar2022.database.subjective.SubjectiveRepository;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.observers.DisposableMaybeObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

        mObjectiveLiveData = Transformations.map(mObjectiveRepository.getLiveMatches(),
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

    public void createObjectiveCsv(ParcelFileDescriptor pfd, String[] buttons, String[] spinners) {
        mObjectiveRepository
                .getAllMatches()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableMaybeObserver<List<ObjectiveMatchData>>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<ObjectiveMatchData> objectiveMatchData) {
                        CSVCreator.createObjectiveCsv(pfd, buttons, spinners, objectiveMatchData);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e("Export Maybe Observer", "Objective Data Error", e);
                    }

                    @Override
                    public void onComplete() {
                        // Complete!
                        Log.d("Export Maybe Observer", "Done!");
                    }
                });
    }

    public void createPitCsv(ParcelFileDescriptor pfd, String[] spinners) {
        mPitRepository
                .getAllTeams()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableMaybeObserver<List<PitScoutData>>() {

                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<PitScoutData> pitScoutData) {
                        CSVCreator.createPitCsv(pfd, spinners, pitScoutData);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e("Export Maybe Observer", "Pit Data Error", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Export Maybe Observer", "Pit Done!");
                    }
                });
    }

    public void createSubjectiveCsv(ParcelFileDescriptor pfd, String[] spinners) {
        mSubjectiveRepository
                .getAllMatches()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableMaybeObserver<List<SubjectiveMatchData>>() {

                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<SubjectiveMatchData> subjectiveMatchData) {
                        CSVCreator.createSubjectiveCsv(pfd, spinners, subjectiveMatchData);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e("Export Maybe Observer", "Subjective Data Error", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Export Maybe Observer", "Subjective Done!");
                    }
                });
    }
}
