package com.ceg.med.beatheartfactory.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ceg.med.beatheartfactory.R;
import com.ceg.med.beatheartfactory.controller.BeatHeartBluetoothController;
import com.ceg.med.beatheartfactory.data.CallbackAble;
import com.ceg.med.beatheartfactory.list.MyoListAdapter;
import com.ceg.med.beatheartfactory.list.MyoListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The main activity.
 */
public class MainActivity extends AppCompatActivity implements CallbackAble<Integer, String> {

    public static final String BEATH_HEART_FACTORY_LOG_TAG = "BeartHeartFactory";

    public static String ID;

    // intent code for enabling Bluetooth
    private static final int REQUEST_ENABLE_BT = 1;


    private ArrayList<MyoListItem> listItems = new ArrayList<>();
    private List<String> knownAddresses = new ArrayList<>();
    private MyoListAdapter adapter;
    private ProgressBar searchingBar;
    private String savedMyoMac;

    private BeatHeartBluetoothController beatHeartBluetoothController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        beatHeartBluetoothController = new BeatHeartBluetoothController(mBluetoothManager, this);
        BeatHeartBluetoothController.registerCallback(this);
        searchingBar = (ProgressBar) findViewById(R.id.main_progress);
        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                handleBluetoothResult(result);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                BeatHeartBluetoothController.getInstance().stopScan();
                searchingBar.setProgress(0);
                searchingBar.setVisibility(View.INVISIBLE);
            }
        };
        beatHeartBluetoothController.startScan(scanCallback);
    }


    private void init() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = findViewById(R.id.first_image);
        imageView.setImageDrawable(getDrawable(R.drawable.beatheartlogo));

        ListView listView = findViewById(R.id.list_view);
        adapter = new MyoListAdapter(listItems, this);
       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyoListItem item = (MyoListItem) adapter.getItem(position);
                bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                connectAndContinue(item);
            }
        });
*/
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.starttoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rescan:
//                scanDevice();
                return true;
            case android.R.id.home:
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Adds items to the myo bracelet list.
     *
     * @param title       Title of the bluetooth deivce found.
     * @param description Description of the bluetooth device.
     * @param device      The {@link BluetoothDevice} itself.
     * @return MyoListItem
     */
    public MyoListItem addItems(String title, String description, BluetoothDevice device, ScanRecord scanRecord) {
        MyoListItem myoListItem = new MyoListItem(title, description, device, scanRecord);
        listItems.add(myoListItem);
        adapter.notifyDataSetChanged();
        return myoListItem;
    }

    /**
     * Is used to clear the list.
     */
    private void clearItems() {
        listItems.clear();
        knownAddresses.clear();
        adapter.notifyDataSetChanged();
    }

    /**
     * Handles the result of the bluetooth search.
     *
     * @param scanResult The {@link ScanResult} of the bluetooth search.
     */
    private void handleBluetoothResult(ScanResult scanResult) {
        BluetoothDevice device = scanResult.getDevice();

        String msg = "name=" + device.getName() + ", bondStatus="
                + device.getBondState() + ", address="
                + device.getAddress() + ", type" + device.getType();
        Log.d(BEATH_HEART_FACTORY_LOG_TAG, msg);

        if (device.getName() != null && !knownAddresses.contains(device.getAddress()) && device.getName().equals("weixin-nini")) {
            knownAddresses.add(device.getAddress());
            addItems(device.getName(), device.getAddress(), device, scanResult.getScanRecord());
            beatHeartBluetoothController.connectDevice(device);
        }
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    @Override
    public void callback(Integer value, String mac) {
        if(beatHeartBluetoothController.setSelectedBall(mac)) {
            BeatHeartBluetoothController.unregisterCallback(this);
            // Check if the user presses the ball to determine if it should be connected
            Intent i = new Intent(getApplicationContext(), BeatHeartPlayerActivity.class);
            startActivity(i);
        }
    }

}

