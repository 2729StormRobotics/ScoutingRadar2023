package org.stormroboticsnj.scoutingradar2022;

import static org.stormroboticsnj.scoutingradar2022.Constants.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import java.util.concurrent.TimeUnit;

public class BluePeripheralTestFragment extends PermissionsFragment{
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private AdvertiseCallback mAdvertiseCallback;
    private Handler mHandler;
    private Runnable timeoutRunnable;
    private long timeoutLength;
    private Context mContext;
    private boolean isAdvertising;

    private TextView mAdvertisingStatusTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeoutLength = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);
        isAdvertising = false;
        //can check for errors here and let the user know
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_blue_peripheral_test, container, false);

        final Button mBtnSend = view.findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this::onBtnSendClick);

        mAdvertisingStatusTextView = view.findViewById(R.id.textView_advertising_status);

        Button receiveButton = view.findViewById(R.id.peripheral_button_receive);
        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_bluePeripheralTestFragment_to_blueReceiveTestFragment);
            }
        });

        return view;
    }


 //   @SuppressLint("MissingPermission")
//    private void startAdvertising() {
//        if (mBluetoothLeAdvertiser == null) {
//            BluetoothManager mBluetoothManager =
//                    (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
//            if (mBluetoothManager != null) {
//                BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
//                if (mBluetoothAdapter != null) {
//                    mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
//                } else {
//                    Toast.makeText(mContext, getString(R.string.bt_null), Toast.LENGTH_LONG).show();
//                }
//            } else {
//                Toast.makeText(mContext, getString(R.string.bt_null), Toast.LENGTH_LONG).show();
//            }
//        }
//
//
//        if (mAdvertiseCallback == null) {
////            checkPermissionsAndAct(mContext);
//            AdvertiseSettings settings = buildAdvertiseSettings();
//            AdvertiseData data = buildAdvertiseData();
//            mAdvertiseCallback = new SampleAdvertiseCallback();
//            if (mBluetoothLeAdvertiser != null) {
//                mBluetoothLeAdvertiser.startAdvertising(settings, data,
//                        mAdvertiseCallback);
//            }
//        }
   // }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    private void stopAdvertising() {
        Toast.makeText(mContext, "Advertising stopping", Toast.LENGTH_LONG).show();
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            mAdvertiseCallback = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseSettings buildAdvertiseSettings() {
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        settingsBuilder.setTimeout(0);
        return settingsBuilder.build();
    }

    /**
     * Returns an AdvertiseData object which includes the Service UUID and Device Name.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        dataBuilder.setIncludeDeviceName(true);

        /* For example - this will cause advertising to fail (exceeds size limit) */
        //String failureData = "asdghkajsghalkxcjhfa;sghtalksjcfhalskfjhasldkjfhdskf";
        //dataBuilder.addServiceData(Constants.Service_UUID, failureData.getBytes());

        return dataBuilder.build();
    }

    @Override
    protected String[] getPermissionsToRequest() {
        // Permissions to ask for, dependent on version
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    @Override
    protected void onPermissionsGranted() {
        BluetoothServer.getInstance(mContext).startAdvertising();
    }

    @Override
    protected String getExplanationDialogTitle() {
        return getString(R.string.bluetooth_permission_explanation_title);
    }

    @Override
    protected String getExplanationDialogMessage() {
        return getString(R.string.bluetooth_permission_explanation);
    }

    private void onBtnSendClick(View view) {
        if (isAdvertising){
//            stopAdvertising();
            mAdvertisingStatusTextView.setText("Not advertising");
            isAdvertising = false;
        }else{
            checkPermissionsAndAct(mContext);
            mAdvertisingStatusTextView.setText("Advertising");
            isAdvertising = true;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * Custom callback after Advertising succeeds or fails to start. Broadcasts the error code
     * in an Intent to be picked up by AdvertiserFragment and stops this Service.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class SampleAdvertiseCallback extends AdvertiseCallback {

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Toast.makeText(mContext, "Advertising failed " + errorCode, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Toast.makeText(mContext, "Advertising started successfully", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Starts a delayed Runnable that will cause the BLE Advertising to timeout and stop after a
     * set amount of time.
     */
    private void setTimeout() {
        mHandler = new Handler();
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "Advertising has reached timeout of " + timeoutLength +
                                         "ms, stopping advertising.", Toast.LENGTH_LONG).show();
            }
        };
        mHandler.postDelayed(timeoutRunnable, timeoutLength);
    }

}
