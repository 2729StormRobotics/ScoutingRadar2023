package org.stormroboticsnj.scoutingradar2022;

import static org.stormroboticsnj.scoutingradar2022.Constants.Service_UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.GattStatus;
import com.welie.blessed.HciStatus;
import com.welie.blessed.ScanFailure;

import java.util.Objects;
import java.util.UUID;

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class BluetoothReader {
    private static volatile BluetoothReader INSTANCE = null;
    private DataReceivedCallback mCallback = null;
    private final BluetoothPeripheralCallback peripheralCallback =
            new BluetoothPeripheralCallback() {

                @Override
                public void onServicesDiscovered(@NonNull BluetoothPeripheral peripheral) {
                    super.onServicesDiscovered(peripheral);
                    BluetoothGattService scoutingService =
                            peripheral.getService(Service_UUID.getUuid());

                    if (scoutingService != null) {
                        BluetoothGattCharacteristic pitCharacteristic =
                                scoutingService.getCharacteristic(
                                        Constants.Pit_Data_UUID.getUuid());
                        BluetoothGattCharacteristic objectiveMatchCharacteristic =
                                scoutingService.getCharacteristic(
                                        Constants.Objective_Data_UUID.getUuid());
                        BluetoothGattCharacteristic subjectiveMatchCharacteristic =
                                scoutingService.getCharacteristic(
                                        Constants.Subjective_Data_UUID.getUuid());

                        peripheral.readCharacteristic(pitCharacteristic);
                        peripheral.readCharacteristic(objectiveMatchCharacteristic);
                        peripheral.readCharacteristic(subjectiveMatchCharacteristic);

                        mCallback.onDataReceived(peripheral.getName()
                                                           .substring(peripheral.getName()
                                                                                .indexOf(':') + 1));
                    }
                    mCallback.onScanStopped();
                }

                @Override
                public void onCharacteristicUpdate(
                        @NonNull BluetoothPeripheral peripheral,
                        @NonNull byte[] value,
                        @NonNull BluetoothGattCharacteristic characteristic,
                        @NonNull GattStatus status) {
                    super.onCharacteristicUpdate(peripheral, value, characteristic, status);
                    // Data transferred to this device can be accessed from here.

                    if (characteristic.getUuid().equals(Constants.Objective_Data_UUID.getUuid())) {
                        mCallback.onObjectiveDataReceived(characteristic.getValue());
                    } else if (characteristic.getUuid()
                                             .equals(Constants.Subjective_Data_UUID.getUuid())) {
                        mCallback.onSubjectiveDataReceived(characteristic.getValue());
                    } else if (characteristic.getUuid().equals(Constants.Pit_Data_UUID.getUuid())) {
                        mCallback.onPitDataReceived(characteristic.getValue());
                    }
                }


            };
    private final BluetoothCentralManager central;
    private String mTeamNumber;

    public BluetoothReader(Context context) {
        BluetoothCentralManagerCallback bluetoothCentralManagerCallback =
                new BluetoothCentralManagerCallback() {
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

                    @Override
                    public void onConnectionFailed(
                            @NonNull BluetoothPeripheral peripheral, @NonNull HciStatus status) {
                        super.onConnectionFailed(peripheral, status);
                        Log.d("test", "test");
                    }

                    @Override
                    public void onDiscoveredPeripheral(
                            @NonNull BluetoothPeripheral peripheral,
                            @NonNull ScanResult scanResult) {
                        central.stopScan();
                        String peripheralTeamNumber =
                                peripheral.getName()
                                          .substring(0, peripheral.getName().indexOf(':'));
                        if (Objects.equals(peripheralTeamNumber, mTeamNumber)) {
                            central.connectPeripheral(peripheral, peripheralCallback);
                        }
                    }

                    @Override
                    public void onScanFailed(@NonNull ScanFailure scanFailure) {
                        super.onScanFailed(scanFailure);
                        Log.e("tag", "scan failed");
                    }
                };
        central = new BluetoothCentralManager(context.getApplicationContext(),
                bluetoothCentralManagerCallback, new Handler(Looper.getMainLooper()));
        central.enableLogging();
    }

    public static BluetoothReader getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new BluetoothReader(context);
        }
        return INSTANCE;
    }

    public void startScan(DataReceivedCallback callback, String teamNum) {
        mCallback = callback;
        central.scanForPeripheralsWithServices(new UUID[]{Service_UUID.getUuid()});
        mTeamNumber = teamNum;

    }

    public void stopScan() {
        central.stopScan();
    }

    public void closeScanner() {
        central.close();
        INSTANCE = null;
    }

    public static abstract class DataReceivedCallback {
        public abstract void onObjectiveDataReceived(byte[] data);

        public abstract void onSubjectiveDataReceived(byte[] data);

        public abstract void onPitDataReceived(byte[] data);

        public abstract void onDataReceived(String name);

        public abstract void onScanStopped();
    }
}
