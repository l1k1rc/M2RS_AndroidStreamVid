package com.example.m2rs_androidstreamvid;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Set;

public class ClientActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private Set<BluetoothDevice> devices;
    private ArrayList<String> listNameDevice = new ArrayList<String>();
    private ArrayList<String> listUURIDevice = new ArrayList<String>();
    public static String EXTRA_ADDRESS = "device_address";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        final ListView listView = (ListView) findViewById(R.id.myListView);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        runtimePermission();
        if (adapter==null){
            Toast.makeText(getApplicationContext(), "Bluetooth not activated ...", Toast.LENGTH_SHORT).show();
        }
        else {
            if(!adapter.isEnabled()){
                Toast.makeText(getApplicationContext(), "Bluetooth not activated ...", Toast.LENGTH_SHORT).show();
                Intent activateBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(activateBluetooth, REQUEST_CODE_ENABLE_BLUETOOTH);
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth activated ...", Toast.LENGTH_SHORT).show();
                devices = adapter.getBondedDevices();
                for (BluetoothDevice deviceBlu : devices){
                    listNameDevice.add(deviceBlu.getName() + "\n" + deviceBlu.getAddress());
                    Log.e(MainActivity.TAG, deviceBlu.getName());
                    Log.e(MainActivity.TAG, deviceBlu.getAddress());
                }
                ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ClientActivity.this, android.R.layout.simple_list_item_1, listNameDevice);
                listView.setAdapter(myAdapter);
                listView.setOnItemClickListener(myListClickListener);
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                if(!adapter.isDiscovering()) {
                    adapter.startDiscovery();
                }
                registerReceiver(mReceiver, filter);
                registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
            return;
        if(resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(), "Bluetooth activated ...", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Bluetooth not activated ...", Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Toast.makeText(getApplicationContext(), "Starting Discovery ...", Toast.LENGTH_LONG).show();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(getApplicationContext(), "Discovery Done", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(MainActivity.TAG,"Try to get no appaired device");
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String exemple = "Device found : " + device.getName();
            Log.e(MainActivity.TAG, device.getName());
            Toast.makeText(getApplicationContext(), "Is detected", Toast.LENGTH_SHORT).show();
        }
    };

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent i = new Intent(ClientActivity.this, ClientSocketActivity.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at CommunicationsActivity
            startActivity(i);
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterReceiver(discoveryResult);
        super.onDestroy();
    }
    /**
     * To ask the permission to the user to have an access in his external storage
     **/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void runtimePermission() {
        Log.i(MainActivity.TAG, "MainActivity onBind");
        Dexter.withActivity(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override// if the permission is accepted, we launch the displaying of the music
            public void onPermissionGranted(PermissionGrantedResponse response) {
                Log.i(MainActivity.TAG, "Permission granted");
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
}
