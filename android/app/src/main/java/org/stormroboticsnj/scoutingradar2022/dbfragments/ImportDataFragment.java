package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.stormroboticsnj.scoutingradar2022.BluetoothReader;
import org.stormroboticsnj.scoutingradar2022.PermissionsFragment;
import org.stormroboticsnj.scoutingradar2022.R;

/**
 * The Fragment for importing data. Options are QR and Bluetooth. Extends {@link PermissionsFragment}
 * only for Bluetooth permissions handling, as the QR library handles Camera permissions automatically.
 */
public class ImportDataFragment extends PermissionsFragment {
    private static final String LOG_TAG = ImportDataFragment.class.getSimpleName();

    private ImportViewModel mViewModel;
    private Context mContext;
    private String mTeamNumber;
    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> mQrLauncher =
            registerForActivityResult(new ScanContract(),
                    result -> {
                        if (result.getContents() == null) {
                            Intent originalIntent = result.getOriginalIntent();
                            if (originalIntent == null) {
                                Log.d(LOG_TAG, "Cancelled scan");
                                Toast.makeText(mContext, R.string.cancelled, Toast.LENGTH_LONG)
                                     .show();
                            } else if (originalIntent.hasExtra(
                                    Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                                Log.d(LOG_TAG,
                                        "Cancelled scan due to missing camera permission");
                                Toast.makeText(mContext, R.string.no_camera_perms,
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            final String res = result.getContents();
                            Toast.makeText(mContext, R.string.scanned, Toast.LENGTH_LONG).show();
                            mViewModel.saveQrData(res);
                        }
                    });
    private TextView mTextView;
    private Button mBtButton;

    public ImportDataFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static ImportDataFragment newInstance() {
        return new ImportDataFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_import_data, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView = view.findViewById(R.id.import_text_details);
        mTextView.setText("");
        mViewModel = new ViewModelProvider(this).get(ImportViewModel.class);
        mBtButton = view.findViewById(R.id.import_button_bt);
        mBtButton.setOnClickListener(this::onBtButtonClicked);
        view.findViewById(R.id.import_button_qr).setOnClickListener(this::onQrButtonClicked);

        mTeamNumber = PreferenceManager.getDefaultSharedPreferences(mContext)
                                             .getString(getString(R.string.pref_key_teamnum), "0000");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BluetoothReader.getInstance(mContext).stopScan();
            BluetoothReader.getInstance(mContext).closeScanner();
        }
    }

    private void onQrButtonClicked(View view) {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setBarcodeImageEnabled(false);
        mQrLauncher.launch(options);
    }

    private void onBtButtonClicked(View view) {
        checkPermissionsAndAct(mContext);
        mBtButton.setEnabled(false);
        mBtButton.setText(R.string.scanning);
    }

    @Override
    protected String[] getPermissionsToRequest() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
               new String[]{
                       Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.BLUETOOTH_SCAN,
                       Manifest.permission.BLUETOOTH_CONNECT
               } : new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                };
    }

    @Override
    protected void onPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BluetoothReader.DataReceivedCallback callback =
                    new BluetoothReader.DataReceivedCallback() {
                        @Override
                        public void onObjectiveDataReceived(byte[] data) {
                            mTextView.append(getString(R.string.objective_space));
                            mViewModel.saveObjectiveData(data);
                        }

                        @Override
                        public void onSubjectiveDataReceived(byte[] data) {
                            mTextView.append(getString(R.string.subjective_space));
                            mViewModel.saveSubjectiveData(data);
                        }

                        @Override
                        public void onPitDataReceived(byte[] data) {
                            mTextView.append(getString(R.string.pit_space));
                            mViewModel.savePitData(data);
                        }

                        @Override
                        public void onDataReceived(String name) {
                            mTextView.append(getString(R.string.data_recieved_name) + name + ": ");
                        }

                        @Override
                        public void onScanStopped() {
                            mBtButton.setEnabled(true);
                            mBtButton.setText(R.string.scan_bluetooth);
                        }
                    };
            BluetoothReader.getInstance(mContext).startScan(callback, mTeamNumber);
            if (TextUtils.isEmpty(mTextView.getText())) {
                mTextView.setText(R.string.import_data_details_top);
            }
        } else {
            Toast.makeText(mContext, R.string.not_lollipop, Toast.LENGTH_LONG).show();
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
        Toast.makeText(mContext, R.string.no_bt_perms, Toast.LENGTH_SHORT).show();
    }
}