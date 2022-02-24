package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.stormroboticsnj.scoutingradar2022.UiUtils;

import java.io.FileNotFoundException;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.observers.DisposableMaybeObserver;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExportDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExportDataFragment extends PermissionsFragment {


    private static final String FILENAME_OBJ = "objective_data.csv";
    private static final String FILENAME_SUB = "subjective_data.csv";
    private static final String FILENAME_PIT = "pit_data.csv";
    private static final String LOG_TAG = ExportDataFragment.class.getSimpleName();
    private SharedPreferences mSharedPreferences;
    private ActivityResultLauncher<String> fileResultLauncher;
    private Context mContext;
    private TextView mTextView;
    private ImageView mQrImageView;
    private ExportViewModel mViewModel;
    private String[] mObjSpinners;
    private String[] mObjButtons;
    private String[] mSubSpinners;
    private String[] mPitSpinners;
    private String mTeamNumber;
    private String mDeviceName;
    private String[] mObjAbbreviations;

    public ExportDataFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static ExportDataFragment newInstance() {
        return new ExportDataFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BluetoothServer.getInstance(mContext).stopAndClose();
        }
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
        mTextView = view.findViewById(R.id.export_text_status);
        mViewModel = new ViewModelProvider(this).get(ExportViewModel.class);

        mObjButtons =
                mSharedPreferences.getString(getString(R.string.pref_key_obj_buttons),
                        getString(R.string.obj_buttons_default)).split(",");
        mObjAbbreviations =
                mSharedPreferences.getString(getString(R.string.pref_key_obj_abbrs),
                        getString(R.string.obj_buttons_default)).split(",");
        mObjSpinners = readPrefs(R.string.pref_key_obj_spinner, R.array.obj_spinners);
        mSubSpinners = readPrefs(R.string.pref_key_sub_spinner, R.array.sub_spinners);
        mPitSpinners = readPrefs(R.string.pref_key_pit_spinner, R.array.pit_spinners);

        view.findViewById(R.id.export_button_bluetooth).setOnClickListener(this::onButtonClicked);
        view.findViewById(R.id.export_button_qr_obj).setOnClickListener(this::onButtonClicked);
        view.findViewById(R.id.export_button_qr_sub).setOnClickListener(this::onButtonClicked);
        view.findViewById(R.id.export_button_qr_pit).setOnClickListener(this::onButtonClicked);
        view.findViewById(R.id.export_button_csv_obj).setOnClickListener(this::onButtonClicked);
        view.findViewById(R.id.export_button_csv_sub).setOnClickListener(this::onButtonClicked);
        view.findViewById(R.id.export_button_csv_pit).setOnClickListener(this::onButtonClicked);

        mQrImageView = view.findViewById(R.id.export_imageview);
        mQrImageView.setOnClickListener(this::onButtonClicked);

        mTeamNumber = PreferenceManager.getDefaultSharedPreferences(mContext)
                                       .getString(getString(R.string.pref_key_teamnum), "0000");
        mDeviceName = PreferenceManager.getDefaultSharedPreferences(mContext)
                                       .getString(getString(R.string.pref_key_device_name), "UNNAMED");
    }

    public void onButtonClicked(@NonNull View view) {
        final int id = view.getId();
        if (id == R.id.export_button_csv_obj) {
            fileResultLauncher.launch(FILENAME_OBJ);
        } else if (id == R.id.export_button_csv_sub) {
            fileResultLauncher.launch(FILENAME_SUB);
        } else if (id == R.id.export_button_csv_pit) {
            fileResultLauncher.launch(FILENAME_PIT);
        } else if (id == R.id.export_button_bluetooth) {
            checkPermissionsAndAct(mContext);
        } else if (id == R.id.export_button_qr_obj) {
            generateQrCode(QR.OBJ);
        } else if (id == R.id.export_button_qr_sub) {
            generateQrCode(QR.SUB);
        } else if (id == R.id.export_button_qr_pit) {
            generateQrCode(QR.PIT);
        }
    }

    private void generateQrCode(@NonNull QR type) {
        Maybe<Bitmap> bitmapMaybe = null;
        switch (type) {
            case OBJ:
                bitmapMaybe = mViewModel.getObjectiveBitmap(mQrImageView.getWidth());
                break;
            case SUB:
                bitmapMaybe = mViewModel.getSubjectiveBitmap(mQrImageView.getWidth());
                break;
            case PIT:
                bitmapMaybe = mViewModel.getPitBitmap(mQrImageView.getWidth());
                break;
        }
        observeBitmapMaybe(bitmapMaybe);
    }

    private void observeBitmapMaybe(@NonNull Maybe<Bitmap> bitmapMaybe) {
        bitmapMaybe
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableMaybeObserver<Bitmap>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Bitmap bitmap) {
                        UiUtils.imageViewAnimatedChange(mContext, mQrImageView, bitmap);
                        mQrImageView.setContentDescription(getString(R.string.qr_code));
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e(LOG_TAG, "QR Generation Problem", e);
                        Toast.makeText(mContext, R.string.qr_error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void writeCsv(@NonNull Intent intent) {
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
                mViewModel.createObjectiveCsv(pfd, mObjButtons, mObjAbbreviations, mObjSpinners);
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
               } :
               new String[]{
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
                mTextView.setText(R.string.data_loaded_for_exporting);
                mTextView.setVisibility(View.VISIBLE);
                mViewModel.getObjectiveLiveData().observe(this, (data) -> {
                    mTextView.append("\n Objective data loaded...");
                    bluetoothServer.setObjectiveData(data);
                });
                mViewModel.getSubjectiveLiveData().observe(this, (data) -> {
                    mTextView.append("\n Subjective data loaded...");
                    bluetoothServer.setSubjectiveData(data);
                });
                mViewModel.getPitScoutData().observe(this, (data) -> {
                    mTextView.append("\n Pit data loaded...");
                    bluetoothServer.setPitData(data);
                });
                bluetoothServer.startAdvertising(mTeamNumber, mDeviceName);
            } else {
                Toast.makeText(mContext, R.string.bt_not_supported, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected String getExplanationDialogTitle() {
        return getString(R.string.bt_perms_header);
    }

    @Override
    protected String getExplanationDialogMessage() {
        return getString(R.string.bt_perms_message);
    }

    @Override
    protected void onPermissionsDenied() {
        Toast.makeText(mContext, "Insufficient Bluetooth Permissions", Toast.LENGTH_SHORT).show();
    }

    private enum QR {
        OBJ,
        SUB,
        PIT
    }

    private static class CsvFileContract extends ActivityResultContract<String, Intent> {

        private String mInput;

        public CsvFileContract() {
            super();
        }

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