package com.example.m2rs_androidstreamvid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;

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

        /*Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("/");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File("downloadedfile.mp4")));
        startActivity(intent);*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothConnection.disconnect();
    }
}