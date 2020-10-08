package com.example.m2rs_androidstreamvid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

public class ClientSocketActivity extends AppCompatActivity {

    private String mDeviceAddress;
    protected ClientSocketTask mBluetoothConnection;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_socket);
        //this button allow us to refresh the app
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        Intent newIntent = getIntent();
        //we get the mac adress device that is sent by the Client Activity via an intent
        mDeviceAddress = newIntent.getStringExtra(ClientActivity.EXTRA_ADDRESS);
        //initiate the background task of the client socket
        mBluetoothConnection = new ClientSocketTask(this, mDeviceAddress);
        mBluetoothConnection.execute();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });

        VideoView vidView = (VideoView)findViewById(R.id.video);

        // Add playback controls.
        MediaController vidControl = new MediaController(this);
        // Set it to use the VideoView instance as its anchor.
        vidControl.setAnchorView(vidView);
        // Set it as the media controller for the VideoView object.
        vidView.setMediaController(vidControl);
        //stock the External Storage directory where our video file is
        File dir = Environment.getExternalStorageDirectory();
        File manualFile = new File(dir, "/" + "Download/downloadedfile.mp4");
        Log.e(MainActivity.TAG,"CLIENT FILE LOCATION : "+manualFile);

        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", manualFile);
        vidView.setVideoURI(uri);
        // Start playback.
        vidView.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothConnection.disconnect();
    }
}