package com.example.m2rs_androidstreamvid;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "LOG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button clientButton = (Button) findViewById(R.id.btnClient);
        final Button serverButton = (Button) findViewById(R.id.btnServer);
        final TextView titleView = (TextView) findViewById(R.id.songText);

        titleView.setSelected(true);
        clientButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.i("TAG", "TETS");
                startActivity(new Intent(MainActivity.this, ClientActivity.class));
            }
        });
        serverButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.i("TAG", "TETS");
                startActivity(new Intent(MainActivity.this, ServerActivity.class));
            }
        });

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices)
            s.add(bt.getName());

        for(String str : s)
            Log.i(MainActivity.TAG,str);
    }
    public void init(BluetoothAdapter bluetoothAdapter, ServiceListenerCallback callback) {
        mListenerCallback = callback;
        if (mBluetoothAdapter != null || mBluetoothServerSocket != null) {
            return;
        }
        mBluetoothAdapter = bluetoothAdapter;
        BluetoothServerSocket tmp = null;
        try {
            // 明文传输，无需配对
            // adapter.listenUsingInsecureRfcommWithServiceRecord(TAG, SPP_UUID);
            // 加密传输，会自动执行配对
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(CbtConstant.CBT_NAME, CbtConstant.CBT_UUID);
        } catch (IOException e) {
            mListenerCallback.onStartError(e);
            CbtLogs.e(e.getMessage());
            return;
        }
        mBluetoothServerSocket = tmp;
        listener();
    }

}