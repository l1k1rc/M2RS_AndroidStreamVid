package com.example.m2rs_androidstreamvid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

public class ClientActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private Set<BluetoothDevice> devices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
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
            }
            devices = adapter.getBondedDevices();
            for (BluetoothDevice deviceBlu : devices){
                Log.e(MainActivity.TAG, deviceBlu.getAddress());
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            registerReceiver(mReceiver, filter);
            registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            if(!adapter.isDiscovering()) {
                adapter.startDiscovery();
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
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String exemple = "Device found : " + device.getName();
            Toast.makeText(getApplicationContext(), "zizi", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterReceiver(discoveryResult);
        super.onDestroy();
    }
}
