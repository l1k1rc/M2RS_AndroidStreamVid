package com.example.m2rs_androidstreamvid;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity{
    public static final String TAG = "LOG_TAG_PROJECT";
    public static final String UUID_CONNECTION = "00001101-0000-1000-8000-00805F9B34FB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button clientButton = (Button) findViewById(R.id.btnClient);
        final Button serverButton = (Button) findViewById(R.id.btnServer);
        final TextView titleView = (TextView) findViewById(R.id.songText);

        runtimePermission();
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
        for (BluetoothDevice bt : pairedDevices)
            s.add(bt.getName());

        for (String str : s)
            Log.i(MainActivity.TAG, str);


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}