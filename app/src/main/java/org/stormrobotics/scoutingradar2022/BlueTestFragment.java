package org.stormrobotics.scoutingradar2022;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothPeripheral;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlueTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlueTestFragment extends Fragment {
    private Context mContext;
//    private static final int REQUEST_ENABLE_BT = 1;
//    private static final int ACCESS_LOCATION_REQUEST = 2;

    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    private EditText mSsidEditText;
    private final ActivityResultLauncher<String[]> requestLocationPermissionsLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(), (isGranted) -> {
                        if (!isGranted.containsValue(false)) {
                            fillSsid(mContext);
                        }
                    }
            );
    private final ActivityResultLauncher<String[]> requestBluetoothPermissionsLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(), (isGranted) -> {
                        if (!isGranted.containsValue(false)) {
                            fillSsid(mContext);
                        }
                    }
            );




    public BlueTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BlueTestFragment.
     */
    public static BlueTestFragment newInstance() {
        return new BlueTestFragment();
    }

    private void fillSsid(@NonNull Context context) {
        // Get current WiFi info
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        if (ssid != null && !TextUtils.isEmpty(ssid) && !ssid.equalsIgnoreCase("<unknown ssid>")) {
            mSsidEditText.setText(ssid.substring(1, ssid.length() - 1));
        }
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkPermsAndFillSsid(mContext);
    }

    private void checkPermsAndFillSsid(@NonNull Context context) {
        boolean areAllPermissionsGranted = true;
        for (int j = 0; j < Integer.parseInt(String.valueOf(PERMISSIONS.length)); j++){
            if (ContextCompat.checkSelfPermission(context, PERMISSIONS[j]) != PackageManager.PERMISSION_GRANTED){
                areAllPermissionsGranted = false;
            }
        }

        if (areAllPermissionsGranted){
            fillSsid(context);
        } else{
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                     shouldShowRequestPermissionRationale(
                             Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showLocPermExplanation();
            } else {
                requestLocationPermissionsLauncher.launch(PERMISSIONS);
            }
            if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH) ||
                  shouldShowRequestPermissionRationale(
                          Manifest.permission.BLUETOOTH_ADMIN)) {
                showBtPermExplanation();
            } else {
                requestBluetoothPermissionsLauncher.launch(PERMISSIONS);
            }
        }


        // if all the permissions are granted then fillSsid
        // else  if we should show location perms show the explanation
        //       if we should show bluetooth perms show the explanation
        //otherwise launch location permissions

    }

    private void showLocPermExplanation() {
        // Build an alert dialog to explain why we need location permission
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.location_permission_explanation);
        builder.setTitle(R.string.location_permission_explanation_title);
        builder.setPositiveButton(R.string.proceed, (dialog, which) -> {
            requestLocationPermissionsLauncher.launch(PERMISSIONS);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void showBtPermExplanation() {
        // Build an alert dialog to explain why we need Bluetooth permission
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.bluetooth_permission_explanation);
        builder.setTitle(R.string.bluetooth_permission_explanation_title);
        builder.setPositiveButton(R.string.proceed, (dialog, which) -> {
            requestBluetoothPermissionsLauncher.launch(PERMISSIONS);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
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
        View v = inflater.inflate(R.layout.fragment_blue_test, container, false);
        Button mSendButton = v.findViewById(R.id.blue_btn_send);
        mSendButton.setOnClickListener(this::onSendButtonClick);
        Button mReceiveButton = v.findViewById(R.id.blue_btn_receive);
        mSendButton.setOnClickListener(this::onReceiveButtonClick);
        return v;
    }

    private void onSendButtonClick(View view){

    }

    private void onReceiveButtonClick(View view){

    }

    private boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = getBluetoothManager().getAdapter();
        if(bluetoothAdapter == null) return false;
        return bluetoothAdapter.isEnabled();
    }


    @NotNull
    private BluetoothManager getBluetoothManager() {
        return Objects.requireNonNull((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE),"cannot get BluetoothManager");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

//    private BluetoothPeripheral getPeripheral(String peripheralAddress) {
//        BluetoothCentralManager central = BluetoothHandler.getInstance(
//                mContext.getApplicationContext()).central;
//        return central.getPeripheral(peripheralAddress);
//    }



    private boolean areLocationServicesEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return locationManager.isLocationEnabled();
        } else {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            return isGpsEnabled || isNetworkEnabled;
        }
    }

    private boolean checkLocationServices() {
        if (!areLocationServicesEnabled()) {
            new AlertDialog.Builder(mContext)
                    .setTitle("Location services are not enabled")
                    .setMessage("Scanning for Bluetooth peripherals requires locations services to be enabled.") // Want to enable?
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
            return false;
        } else {
            return true;
        }
    }


}