package com.ceg.med.beatheartfactory.data;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import static android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
import static com.ceg.med.beatheartfactory.activity.MainActivity.BEATH_HEART_FACTORY_LOG_TAG;

public class NiniGattCallback extends BluetoothGattCallback {
    /**
     * Service ID
     */
    private static final String NINI_SEND_PRESSURE = "0000ffb0-0000-1000-8000-00805f9b34fb";
    /**
     * Characteristics ID
     */
    private static final String SEND_DATA = "0000ffb2-0000-1000-8000-00805f9b34fb";
    private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();

    private BluetoothGatt bluetoothGatt;

    private static CallbackAble<Integer> callbackAble;

    public NiniGattCallback(CallbackAble<Integer> callback) {
        callbackAble = callback;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Log.d(BEATH_HEART_FACTORY_LOG_TAG, "onConnectionStateChange: " + status + " -> " + newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // GATT Connected
            // Searching GATT Service
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // GATT Disconnected
            Log.d(BEATH_HEART_FACTORY_LOG_TAG, "Bluetooth Disconnected");
        }
    }

    /**
     * naoki
     *
     * @param gatt
     * @param status
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        Log.d(BEATH_HEART_FACTORY_LOG_TAG, "onServicesDiscovered received: " + status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BluetoothGattService bluetoothGattService = gatt.getService(UUID.fromString(NINI_SEND_PRESSURE));
            Log.d(BEATH_HEART_FACTORY_LOG_TAG, "service: " + bluetoothGattService.getUuid().toString());
            BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(UUID.fromString(SEND_DATA));
            Log.d(BEATH_HEART_FACTORY_LOG_TAG, "characteristic: " + characteristic.getUuid().toString());
            boolean b = gatt.setCharacteristicNotification(characteristic, true);
            if (b) {
                for (BluetoothGattDescriptor bluetoothGattDescriptor : characteristic.getDescriptors()) {
                    bluetoothGattDescriptor.setValue(ENABLE_NOTIFICATION_VALUE);
                    writeGattDescriptor(bluetoothGattDescriptor);
                }
            }
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] emg_data = characteristic.getValue();
        ByteReader byteReader = new ByteReader();
        byteReader.setByteData(emg_data);
        //String value = String.format("%d %d %d %d",emg_data[2],emg_data[3],emg_data[4],emg_data[5]);
        int val;

        val = (emg_data[3] - 40)*100 + (emg_data[4]-40)*10 + (emg_data[5]-40) - 868;
        if(emg_data[5] == 46){
           val = val + 909;
        }
        val = (int)((val/1700.0f) * 100.0f);
        val = val > 100 ? 100 : val < 0 ? 0 : val;
        System.out.println(val);
        callbackAble.callback(val);
    }

    /**
     * naoki
     *
     * @param d
     */
    public void writeGattDescriptor(BluetoothGattDescriptor d) {
        //put the descriptor into the write queue
        descriptorWriteQueue.add(d);
        //if there is only 1 item in the queue, then write it.  If more than 1, we handle asynchronously in the callback above
        if (descriptorWriteQueue.size() == 1) {
            bluetoothGatt.writeDescriptor(d);
        }
    }

    public void setBluetoothGatt(BluetoothGatt gatt) {
        bluetoothGatt = gatt;
    }

    public static void set(CallbackAble<Integer> callback) {
        callbackAble = callback;
        Log.d(BEATH_HEART_FACTORY_LOG_TAG, "SET CALLBACK " + callback.toString());
    }

}
