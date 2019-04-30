package com.ceg.med.beatheartfactory.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;

import com.ceg.med.beatheartfactory.data.CallbackAble;
import com.ceg.med.beatheartfactory.data.NiniGattCallback;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.ceg.med.beatheartfactory.activity.MainActivity.BEATH_HEART_FACTORY_LOG_TAG;

public class BeatHeartBluetoothController implements CallbackAble<Integer, String> {

    // device scanning time in ms
    private static final long SCAN_PERIOD = 900000;

    private static BeatHeartBluetoothController instance;

    private static CallbackAble<Integer, String> rescanValueCallback;
    private static Map<String, BluetoothGatt> connectedDevices;
    private static String selectedBall;
    private static List<CallbackAble<Integer, String>> callbacks;

    //Bluetooth Specific Handlers
    private BluetoothAdapter bluetoothAdapter;
    private ScanCallback scanCallback;
    private ScanCallback rescanCallback;
    private Context context;

    private static final Object synchron = new Object();

    public BeatHeartBluetoothController(BluetoothManager bleMng, Context context) {
        connectedDevices = new HashMap<>();
        selectedBall = null;
        callbacks = new LinkedList<>();
        bluetoothAdapter = bleMng.getAdapter();
        this.context = context;
        instance = this;
    }

    public static BeatHeartBluetoothController getInstance() {
        return instance;
    }

    public void connectDevice(BluetoothDevice device) {
        if (!connectedDevices.containsKey(device.getAddress())) {
            NiniGattCallback niniGattCallback = new NiniGattCallback(this, device.getAddress());
            BluetoothGatt bluetoothGatt = device.connectGatt(context, false, niniGattCallback);
            niniGattCallback.setBluetoothGatt(bluetoothGatt);
            connectedDevices.put(device.getAddress(), bluetoothGatt);
        }
    }

    public boolean setSelectedBall(String mac) {
        synchronized (synchron) {
            if (connectedDevices.containsKey(mac)) {
                selectedBall = mac;
                //Disconnect all other devices
                for (Map.Entry<String, BluetoothGatt> entry : connectedDevices.entrySet()) {
                    if (!entry.getKey().equals(mac)) {
                        entry.getValue().disconnect();
                    }
                }
                HashMap<String, BluetoothGatt> newMap = new HashMap<>();
                newMap.put(mac, connectedDevices.get(mac));
                connectedDevices = newMap;
                if (rescanCallback != null) {
                    BeatHeartBluetoothController.unregisterCallback(rescanValueCallback);
                }
                BeatHeartBluetoothController.getInstance().stopScan();
                return true;
            }
            return false;
        }
    }

    /**
     * Starts a rescan, if the selected ball is disconnected (either because of inactivity or no battery)
     *
     * @param mac Address of the device disconnected
     */
    public void startRescan(String mac) {
        synchronized (synchron) {
            if (mac.equals(selectedBall)) {
                connectedDevices.get(mac).disconnect();
                connectedDevices.remove(mac);
                selectedBall = null;
                Log.d(BEATH_HEART_FACTORY_LOG_TAG, "Start RESCAN");
                rescanValueCallback = new CallbackAble<Integer, String>() {
                    @Override
                    public void callback(Integer value, String id) {
                        BeatHeartBluetoothController.getInstance().setSelectedBall(id);
                    }
                };
                registerCallback(rescanValueCallback);
                rescanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        handleBluetoothResult(result);
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        super.onScanFailed(errorCode);
                        BeatHeartBluetoothController.getInstance().stopScan();
                    }
                };
                bluetoothAdapter.getBluetoothLeScanner().startScan(rescanCallback);
            }
        }
    }

    /**
     * Handles the result of the bluetooth search.
     *
     * @param scanResult The {@link ScanResult} of the bluetooth search.
     */
    private void handleBluetoothResult(ScanResult scanResult) {
        synchronized (synchron) {
            BluetoothDevice device = scanResult.getDevice();
            if (device.getName() != null && device.getName().equals("weixin-nini")) {
                this.connectDevice(device);
            }
        }
    }

    public void startScan(ScanCallback callback) {
        this.scanCallback = callback;
        bluetoothAdapter.getBluetoothLeScanner().startScan(callback);
        // Stop Scanning after some time

       /* bluetoothScanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BeatHeartBluetoothController.getInstance(null).stopScan();
            }
        }, SCAN_PERIOD);
*/
    }

    public void stopScan() {
        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        if(rescanCallback != null){
            bluetoothAdapter.getBluetoothLeScanner().stopScan(rescanCallback);
        }

    }

    public static void registerCallback(CallbackAble<Integer, String> callbackAble) {
        synchronized (synchron) {
            if (!callbacks.contains(callbackAble)) {
                callbacks.add(callbackAble);
            }
        }
    }

    public static void unregisterCallback(CallbackAble<Integer, String> callbackAble) {
        synchronized (synchron) {
            callbacks.remove(callbackAble);
        }
    }

    @Override
    public void callback(final Integer value, String id) {
        for (CallbackAble<Integer, String> call : callbacks) {
            call.callback(value, id);
        }
    }
}
