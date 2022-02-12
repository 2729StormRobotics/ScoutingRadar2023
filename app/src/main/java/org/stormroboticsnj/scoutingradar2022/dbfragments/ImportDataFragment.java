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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.stormroboticsnj.scoutingradar2022.BluetoothReader;
import org.stormroboticsnj.scoutingradar2022.PermissionsFragment;
import org.stormroboticsnj.scoutingradar2022.R;
import org.stormroboticsnj.scoutingradar2022.database.DataUtils;
import org.stormroboticsnj.scoutingradar2022.database.ObjectiveMatchData;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImportDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImportDataFragment extends PermissionsFragment {

    private ImportViewModel mViewModel;
    private Context mContext;
    private TextView mTextView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public ImportDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImportDataFragment.
     */
    public static ImportDataFragment newInstance() {
        return new ImportDataFragment();
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
        mTextView = view.findViewById(R.id.import_text_test);

        mViewModel = new ViewModelProvider(this).get(ImportViewModel.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkPermissionsAndAct(mContext);
        }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPermissionsGranted() {
        BluetoothReader.DataReceivedCallback callback = new BluetoothReader.DataReceivedCallback() {
            @Override
            public void onObjectiveDataReceived(byte[] data, String name) {
                mTextView.setText(
                        mTextView.getText() + name);
            }

            @Override
            public void onSubjectiveDataReceived(byte[] data, String name) {

            }

            @Override
            public void onPitDataReceived(byte[] data, String name) {

            }
        };
        BluetoothReader.getInstance(mContext, callback).startScan();
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