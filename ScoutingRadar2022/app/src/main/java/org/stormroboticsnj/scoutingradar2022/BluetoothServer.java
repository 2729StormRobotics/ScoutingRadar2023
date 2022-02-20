package org.stormroboticsnj.scoutingradar2022;

import static org.stormroboticsnj.scoutingradar2022.Constants.Objective_Data_UUID;
import static org.stormroboticsnj.scoutingradar2022.Constants.Pit_Data_UUID;
import static org.stormroboticsnj.scoutingradar2022.Constants.Service_UUID;
import static org.stormroboticsnj.scoutingradar2022.Constants.Subjective_Data_UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothPeripheralManager;
import com.welie.blessed.BluetoothPeripheralManagerCallback;
import com.welie.blessed.GattStatus;

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class BluetoothServer {

    private static final String LOG_TAG = BluetoothServer.class.getSimpleName();
    private static BluetoothServer instance = null;
    private final boolean btSupported;
    private BluetoothPeripheralManager peripheralManager = null;
    private BluetoothGattCharacteristic pitData = null;
    private BluetoothGattCharacteristic objectiveData = null;
    private BluetoothGattCharacteristic subjectiveData = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    @SuppressLint("MissingPermission")
    public BluetoothServer(Context context) {
        BluetoothManager
                bluetoothManager = (BluetoothManager) context.getApplicationContext()
                                                             .getSystemService(
                                                                     Context.BLUETOOTH_SERVICE);

        if (bluetoothManager == null) {
            Log.e(LOG_TAG, "Bluetooth not supported.");
            btSupported = false;
            return;
        }
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Log.e(LOG_TAG, "Bluetooth not supported.");
            btSupported = false;
            return;
        }

        if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            Log.e(LOG_TAG, "Advertising not supported.");
            btSupported = false;
            return;
        }

        BluetoothPeripheralManagerCallback peripheralManagerCallback =
                new BluetoothPeripheralManagerCallback() {

                    @Override
                    public void onCentralConnected(@NonNull BluetoothCentral bluetoothCentral) {
                        super.onCentralConnected(bluetoothCentral);
                    }

                    @Override
                    public void onCharacteristicRead(
                            @NonNull BluetoothCentral bluetoothCentral,
                            @NonNull BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicRead(bluetoothCentral, characteristic);
                    }

                    @Override
                    public GattStatus onCharacteristicWrite(
                            @NonNull BluetoothCentral bluetoothCentral,
                            @NonNull BluetoothGattCharacteristic characteristic,
                            @NonNull byte[] value) {
                        return super.onCharacteristicWrite(bluetoothCentral, characteristic, value);
                    }
                };
        this.peripheralManager = new BluetoothPeripheralManager(context, bluetoothManager,
                peripheralManagerCallback);

        BluetoothGattService service = new BluetoothGattService(Service_UUID.getUuid(),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        pitData =
                new BluetoothGattCharacteristic(Pit_Data_UUID.getUuid(),
                        BluetoothGattCharacteristic.PROPERTY_READ,
                        BluetoothGattCharacteristic.PERMISSION_READ);
        objectiveData = new BluetoothGattCharacteristic(Objective_Data_UUID.getUuid(),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        subjectiveData = new BluetoothGattCharacteristic(Subjective_Data_UUID.getUuid(),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);

        service.addCharacteristic(pitData);
        service.addCharacteristic(objectiveData);
        service.addCharacteristic(subjectiveData);

        peripheralManager.add(service);
        btSupported = true;
    }


    public static synchronized BluetoothServer getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothServer(context.getApplicationContext());
        }
        return instance;
    }

    public boolean isBtSupported() {
        return btSupported;
    }

    @SuppressLint("MissingPermission")
    public void startAdvertising(String teamNumber, String deviceName) {
        if (btSupported) {
            mBluetoothAdapter.setName(teamNumber + ":" + deviceName);
            peripheralManager.startAdvertising(buildAdvertiseSettings(), buildAdvertiseData(),
                    buildScanResponse());
        }
    }

    public void setPitData(byte[] data) {
        pitData.setValue(data);
    }

    public void setObjectiveData(byte[] data) {
        objectiveData.setValue(data);
    }

    public void setSubjectiveData(byte[] data) {
        subjectiveData.setValue(data);
    }

    private AdvertiseSettings buildAdvertiseSettings() {
        return new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setConnectable(true)
                .setTimeout(0)
                .build();
    }

    /**
     * Returns an AdvertiseData object which includes the Service UUID and Device Name.
     */
    private AdvertiseData buildAdvertiseData() {


        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.addServiceUuid(Service_UUID);

        return dataBuilder.build();
    }

    private AdvertiseData buildScanResponse() {
        return new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .build();
    }

    public void stopAndClose() {
        peripheralManager.stopAdvertising();
        peripheralManager.close();
        instance = null;
    }


}
