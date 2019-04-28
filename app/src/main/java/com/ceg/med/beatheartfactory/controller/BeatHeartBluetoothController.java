package com.ceg.med.beatheartfactory.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.ceg.med.beatheartfactory.data.CallbackAble;
import com.ceg.med.beatheartfactory.data.NiniGattCallback;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BeatHeartBluetoothController implements CallbackAble<Integer, String> {

    // device scanning time in ms
    private static final long SCAN_PERIOD = 900000;

    private static BeatHeartBluetoothController instance;

    private Map<String, BluetoothGatt> connectedDevices;
    private String selectedBall;
    private static List<CallbackAble<Integer, String>> callbacks;

    //Bluetooth Specific Handlers
    private BluetoothAdapter bluetoothAdapter;
    private ScanCallback scanCallback;
    private Context context;

    public BeatHeartBluetoothController(BluetoothManager bleMng, Context context) {
        connectedDevices = new HashMap<>();
        selectedBall = null;
        callbacks = new LinkedList<>();
        bluetoothAdapter = bleMng.getAdapter();
        this.context = context;
    }

    public static BeatHeartBluetoothController getInstance() {
       return instance;
    }

    public void connectDevice(BluetoothDevice device) {
        if (!connectedDevices.containsKey(device.getAddress())) {
            NiniGattCallback niniGattCallback = new NiniGattCallback(this, device.getAddress());
            BluetoothGatt bluetoothGatt = device.connectGatt(context, true, niniGattCallback);
            niniGattCallback.setBluetoothGatt(bluetoothGatt);
            connectedDevices.put(device.getAddress(),bluetoothGatt);
        }
    }

    public boolean setSelectedBall(String mac) {
        if(connectedDevices.containsKey(mac)) {
            selectedBall = mac;
            //Disconnect all other devices
            for (Map.Entry<String, BluetoothGatt> entry : connectedDevices.entrySet()) {
                if (!entry.getKey().equals(mac)) {
                    entry.getValue().disconnect();
                    connectedDevices.remove(entry.getKey());
                }
            }
            return true;
        }
        return false;
    }

    public void startScan(ScanCallback callback){
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

    public void stopScan(){
        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
    }

    public static void registerCallback(CallbackAble<Integer, String> callbackAble) {
        callbacks.add(callbackAble);
    }

    public static void unregisterCallback(CallbackAble<Integer, String> callbackAble){
        callbacks.remove(callbackAble);
    }

    @Override
    public void callback(final Integer value, String id) {
        for (CallbackAble<Integer, String> call : callbacks) {
            call.callback(value, id);
        }
    }
}
