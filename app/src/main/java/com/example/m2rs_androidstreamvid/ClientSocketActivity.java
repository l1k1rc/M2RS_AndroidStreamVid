package com.example.m2rs_androidstreamvid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

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

        /*newIntent.setAction(Intent.ACTION_SEND);
        newIntent.setType("/");
        newIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File("downloadedfile.mp4")));
        Log.e(MainActivity.TAG,"INTENT USE");
        startActivity(newIntent);*/

        // Get a reference to the VideoView instance as follows, using the id we set in the XML layout.
        VideoView vidView = (VideoView)findViewById(R.id.video);

        // Add playback controls.
        MediaController vidControl = new MediaController(this);
        // Set it to use the VideoView instance as its anchor.
        vidControl.setAnchorView(vidView);
        // Set it as the media controller for the VideoView object.
        vidView.setMediaController(vidControl);

        File dir = Environment.getExternalStorageDirectory();
        File manualFile = new File(dir, "/" + "/downloadedfile.mp4");
        Log.e(MainActivity.TAG,"CLIENT FILE LOCATION : "+manualFile);

        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", manualFile);
        // Parse the address string as a URI so that we can pass it to the VideoView object.
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