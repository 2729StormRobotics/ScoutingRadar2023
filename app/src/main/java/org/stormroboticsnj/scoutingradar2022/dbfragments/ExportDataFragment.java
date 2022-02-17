package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import org.stormroboticsnj.scoutingradar2022.BluetoothServer;
import org.stormroboticsnj.scoutingradar2022.PermissionsFragment;
import org.stormroboticsnj.scoutingradar2022.R;

import java.io.FileNotFoundException;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExportDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExportDataFragment extends PermissionsFragment {


    private SharedPreferences mSharedPreferences;
    private ActivityResultLauncher<String> fileResultLauncher;
    private Context mContext;
    private TextView mTextView;
    private ExportViewModel mViewModel;
    private String[] mObjSpinners;
    private String[] mObjButtons;
    private String[] mSubSpinners;
    private String[] mPitSpinners;

    private static final String FILENAME_OBJ = "objective_data.csv";
    private static final String FILENAME_SUB = "subjective_data.csv";
    private static final String FILENAME_PIT = "pit_data.csv";

    public ExportDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ExportDataFragment.
     */
    public static ExportDataFragment newInstance() {
        return new ExportDataFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        fileResultLauncher = registerForActivityResult(new CsvFileContract(), this::writeCsv);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_export_data, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView = view.findViewById(R.id.export_text_test);
        mViewModel = new ViewModelProvider(this).get(ExportViewModel.class);

        mObjSpinners = readPrefs(R.string.pref_key_obj_spinner, R.array.obj_spinners);
        mObjButtons = readPrefs(R.string.pref_key_obj_buttons, R.array.obj_buttons);
        mSubSpinners = readPrefs(R.string.pref_key_sub_spinner, R.array.sub_spinners);
        mPitSpinners = readPrefs(R.string.pref_key_pit_spinner, R.array.pit_spinners);

        fileResultLauncher.launch(FILENAME_OBJ);
    }

    public void onButtonClicked(View view) {
        // if objective CSV


//        mCsvMode = MODE_OBJ;

        // if BT transfer
        // checkPermissionsAndAct(mContext);


    }

    private void writeCsv(Intent intent) {
        final String title = intent.getStringExtra(Intent.EXTRA_TITLE);
        if (title == null) {
            Log.e("Export Data", "Write CSV Fail: title of intent is null");
            return;
        }

        Uri uri = intent.getData();
        ParcelFileDescriptor pfd = null;
        try {
            pfd = mContext.getContentResolver().openFileDescriptor(uri, "w");
        } catch (FileNotFoundException e) {
            Log.e("Export Fragment", "writeCSV: file not found", e);
        }

        switch (title) {
            case FILENAME_OBJ:
                mViewModel.createObjectiveCsv(pfd, mObjButtons, mObjSpinners);
                break;
            case FILENAME_SUB:
                mViewModel.createSubjectiveCsv(pfd, mSubSpinners);
                break;
            case FILENAME_PIT:
                mViewModel.createPitCsv(pfd, mPitSpinners);
                break;
        }
        
    }

    private String[] readPrefs(int prefKey, int defaultArray) {
        // Get all of the spinner contents
        Set<String> set = mSharedPreferences.getStringSet(getString(prefKey),
                null);
        String[] prefArray;
        if (set != null) {
            // Convert to array
            prefArray = set.toArray(new String[0]);
        } else {
            // Preference has never been set; use default options.
            prefArray = getResources().getStringArray(defaultArray);
        }
        return prefArray;
    }

    @Override
    protected String[] getPermissionsToRequest() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
               new String[]{
                       Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.BLUETOOTH_CONNECT,
                       Manifest.permission.BLUETOOTH_ADVERTISE
               } : new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                };
    }

    @Override
    protected void onPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BluetoothServer bluetoothServer = BluetoothServer.getInstance(mContext);
            if (bluetoothServer.isBtSupported()) {
                mViewModel.getmObjectiveLiveData().observe(this, (data) -> {
                    mTextView.setText(mTextView.getText() + "\n Objective data loaded...");
                    bluetoothServer.setObjectiveData(data);
                });
                mViewModel.getmSubjectiveLiveData().observe(this, (data) -> {
                    mTextView.setText(mTextView.getText() + "\n Subjective data loaded...");
                    bluetoothServer.setSubjectiveData(data);
                });
                mViewModel.getmPitScoutData().observe(this, (data) -> {
                    mTextView.setText(mTextView.getText() + "\n Pit data loaded...");
                    bluetoothServer.setPitData(data);
                });
                bluetoothServer.startAdvertising();
            }
        }
    }

    @Override
    protected String getExplanationDialogTitle() {
        return null;
    }

    @Override
    protected String getExplanationDialogMessage() {
        return null;
    }

    private static class CsvFileContract extends ActivityResultContract<String, Intent> {

        public CsvFileContract() {
            super();
        }

        private String mInput;

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, @NonNull String input) {
            mInput = input;
            return new Intent(Intent.ACTION_CREATE_DOCUMENT)
                    .setType("*/*")
                    .putExtra(Intent.EXTRA_TITLE, input);
        }

        @Nullable
        @Override
        public final Intent parseResult(int resultCode, @Nullable Intent intent) {
            if (intent == null || resultCode != Activity.RESULT_OK) {
                return null;
            }
            return intent.putExtra(Intent.EXTRA_TITLE, mInput);
        }

        @Nullable
        @Override
        public final SynchronousResult<Intent> getSynchronousResult(
                @NonNull Context context,
                @NonNull String input) {
            return null;
        }

    }
}