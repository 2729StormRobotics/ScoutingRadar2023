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
    private final BluetoothPeripheralManagerCallback peripheralManagerCallback =
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
    private BluetoothPeripheralManager peripheralManager = null;
    private BluetoothGattCharacteristic pitData = null;
    private BluetoothGattCharacteristic objectiveData = null;
    private BluetoothGattCharacteristic subjectiveData = null;

    @SuppressLint("MissingPermission")
    public BluetoothServer(Context context) {
        BluetoothManager
                bluetoothManager = (BluetoothManager) context.getApplicationContext()
                                                             .getSystemService(
                                                                     Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null || bluetoothManager == null) {
            Log.e(LOG_TAG, "Bluetooth not supported.");
            btSupported = false;
            return;
        }

        if (!bluetoothAdapter.isMultipleAdvertisementSupported()) {
            Log.e(LOG_TAG, "Advertising not supported.");
            btSupported = false;
            return;
        }

        bluetoothAdapter.setName("Scouting test");

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
    public void startAdvertising() {
        if (btSupported) {
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

        /**
         * Note: There is a strict limit of 31 Bytes on packets sent over BLE Advertisements.
         *  This includes everything put into AdvertiseData including UUIDs, device info, &
         *  arbitrary service or manufacturer data.
         *  Attempting to send packets over this limit will result in a failure with error code
         *  AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE. Catch this error in the
         *  onStartFailure() method of an AdvertiseCallback implementation.
         */

        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.addServiceUuid(Service_UUID);

        /* For example - this will cause advertising to fail (exceeds size limit) */
        //String failureData = "asdghkajsghalkxcjhfa;sghtalksjcfhalskfjhasldkjfhdskf";
        //dataBuilder.addServiceData(Constants.Service_UUID, failureData.getBytes());

        return dataBuilder.build();
    }

    private AdvertiseData buildScanResponse() {
        return new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .build();
    }


}
