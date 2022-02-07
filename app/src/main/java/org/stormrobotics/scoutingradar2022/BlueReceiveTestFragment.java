package org.stormrobotics.scoutingradar2022;

import static org.stormrobotics.scoutingradar2022.Constants.Service_UUID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.*;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlueReceiveTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlueReceiveTestFragment extends PermissionsFragment {
    private Context mContext;

    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {
        @Override
        public void onScanFailed(@NonNull ScanFailure scanFailure) {
            super.onScanFailed(scanFailure);
            Log.e("tag", "scan failed");
        }

        @Override
        public void onDiscoveredPeripheral(
                @NonNull BluetoothPeripheral peripheral, @NonNull ScanResult scanResult){
            central.stopScan();
            central.connectPeripheral(peripheral, peripheralCallback);
        }

        @Override
        public void onConnectingPeripheral(@NonNull BluetoothPeripheral peripheral) {
            super.onConnectingPeripheral(peripheral);
            Log.d("BT RECEIVE", "CONNECTING");
        }

        @Override
        public void onConnectedPeripheral(@NonNull BluetoothPeripheral peripheral) {
            super.onConnectedPeripheral(peripheral);
            Log.d("BT RECEIVE", "CONNECTED");
        }
    };
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {

        @Override
        public void onCharacteristicUpdate(
                @NonNull BluetoothPeripheral peripheral,
                @NonNull byte[] value,
                @NonNull BluetoothGattCharacteristic characteristic, @NonNull GattStatus status) {
            super.onCharacteristicUpdate(peripheral, value, characteristic, status);
            // Data transferred to this device can be accessed from here.

            new AlertDialog.Builder(mContext).setTitle("Read!")
                                     .setMessage("P " + peripheral + "\nC "
                                                 + characteristic + "\nV " +
                                                 value[0] + "\nS " + status).show();
        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothPeripheral peripheral) {
            super.onServicesDiscovered(peripheral);
            BluetoothGattService scoutingService = peripheral.getServices().get(0);

            BluetoothGattCharacteristic pitCharacteristic = scoutingService.getCharacteristics().get(0);
            BluetoothGattCharacteristic objectiveMatchCharacteristic = scoutingService.getCharacteristics().get(1);
            BluetoothGattCharacteristic subjectiveMatchCharacteristic = scoutingService.getCharacteristics().get(2);

            peripheral.readCharacteristic(pitCharacteristic);
            peripheral.readCharacteristic(objectiveMatchCharacteristic);
            peripheral.readCharacteristic(subjectiveMatchCharacteristic);
        }


    };
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }
    };

    public BlueReceiveTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BlueTestFragment.
     */
    public static BlueReceiveTestFragment newInstance() {
        return new BlueReceiveTestFragment();
    }


    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private BluetoothCentralManager central = null;

    @SuppressLint("MissingPermission")
    private void scan(Context context) {
        central.scanForPeripheralsWithServices(new UUID[] {Service_UUID.getUuid()});
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
        View v = inflater.inflate(R.layout.fragment_blue_receive_test, container, false);
        Button mReceiveButton = v.findViewById(R.id.receive_button_receive);
        mReceiveButton.setOnClickListener(this::onReceiveButtonClick);
        return v;
    }

    private void onReceiveButtonClick(View view){
        checkPermissionsAndAct(mContext);
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



    @Override
    protected String[] getPermissionsToRequest() {
        // Permissions to ask for, dependent on version
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
        if (central == null) central = new BluetoothCentralManager(mContext.getApplicationContext(),
                bluetoothCentralManagerCallback, new Handler(Looper.getMainLooper()));
        central.enableLogging();
        scan(mContext);

    }

    @Override
    protected String getExplanationDialogTitle() {
        return getString(R.string.bluetooth_permission_explanation_title);
    }

    @Override
    protected String getExplanationDialogMessage() {
        return getString(R.string.bluetooth_permission_explanation);
    }
}