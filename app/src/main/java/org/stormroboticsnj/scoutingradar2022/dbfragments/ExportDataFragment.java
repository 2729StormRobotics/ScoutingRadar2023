package org.stormroboticsnj.scoutingradar2022.dbfragments;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.stormroboticsnj.scoutingradar2022.BluetoothServer;
import org.stormroboticsnj.scoutingradar2022.PermissionsFragment;
import org.stormroboticsnj.scoutingradar2022.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExportDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExportDataFragment extends PermissionsFragment {

    private Context mContext;
    private TextView mTextView;

    private ExportViewModel mViewModel;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView = view.findViewById(R.id.export_text_test);
        mViewModel = new ViewModelProvider(this).get(ExportViewModel.class);
        checkPermissionsAndAct(mContext);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_export_data, container, false);
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
}