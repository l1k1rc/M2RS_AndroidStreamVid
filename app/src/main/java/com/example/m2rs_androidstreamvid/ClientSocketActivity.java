package com.example.m2rs_androidstreamvid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ClientSocketActivity extends AppCompatActivity {

    private String mDeviceAddress;
    protected ClientSocketTask mBluetoothConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_socket);

        Intent newIntent = getIntent();
        mDeviceAddress = newIntent.getStringExtra(ClientActivity.EXTRA_ADDRESS);

        mBluetoothConnection = new ClientSocketTask(this, mDeviceAddress);
        mBluetoothConnection.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothConnection.disconnect();
    }
}