package org.stormroboticsnj.scoutingradar2022;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
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
import com.welie.blessed.ScanFailure;

@RequiresApi (Build.VERSION_CODES.LOLLIPOP)
public class BluetoothReader {
    private final DataReceivedCallback mCallback;
    private BluetoothCentralManager central = null;
    private static final int teamNumber = 0;

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {

        @Override
        public void onCharacteristicUpdate(
                @NonNull BluetoothPeripheral peripheral,
                @NonNull byte[] value,
                @NonNull BluetoothGattCharacteristic characteristic, @NonNull GattStatus status) {
            super.onCharacteristicUpdate(peripheral, value, characteristic, status);
            // Data transferred to this device can be accessed from here.

//            new AlertDialog.Builder(mContext).setTitle("Read!")
//                                             .setMessage("P " + peripheral + "\nC "
//                                                         + characteristic + "\nV " +
//                                                         value[0] + "\nS " + status).show();

            if (characteristic.getUuid().equals(Constants.Objective_Data_UUID.getUuid())){
                mCallback.onObjectiveDataReceived(characteristic.getValue(), peripheral.getName());
            } else if (characteristic.getUuid().equals(Constants.Subjective_Data_UUID.getUuid())){
                mCallback.onSubjectiveDataReceived(characteristic.getValue(), peripheral.getName());
            } else if (characteristic.getUuid().equals(Constants.Pit_Data_UUID.getUuid())){
                mCallback.onPitDataReceived(characteristic.getValue(), peripheral.getName());
            }
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
            int peripheralTeamNumber = Integer.parseInt(peripheral.getName().substring(0, peripheral.getName().indexOf(':')));
            if (teamNumber == peripheralTeamNumber) central.connectPeripheral(peripheral, peripheralCallback);
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

    public BluetoothReader (Context context, DataReceivedCallback callback){
        mCallback = callback;
        central = new BluetoothCentralManager(context.getApplicationContext(),
                bluetoothCentralManagerCallback, new Handler(Looper.getMainLooper()));
    }

    public static abstract class DataReceivedCallback {
        public abstract void onObjectiveDataReceived(byte[] data, String name);
        public abstract void onSubjectiveDataReceived(byte[] data, String name);
        public abstract void onPitDataReceived(byte[] data, String name);
    }
}
