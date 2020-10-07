package com.example.m2rs_androidstreamvid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static androidx.core.util.Preconditions.checkNotNull;

@SuppressWarnings("ALL")
public class ServerActivity extends AppCompatActivity {


    private final int TIMEOUT_CONNECTION = 5000;//5sec
    private final int TIMEOUT_SOCKET = 30000;//30sec
    private ProgressBar spinner;
    private boolean serverUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        final Button btnDonwload = (Button) findViewById(R.id.btnDownload);
        final MultiAutoCompleteTextView autoTextView = (MultiAutoCompleteTextView) findViewById(R.id.textViewUrl);
        final TextView errorExpected = (TextView) findViewById(R.id.errorExpected);

        //final String downloadUrl = "https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        btnDonwload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String downloadUrl = autoTextView.getText().toString();
                Toast.makeText(ServerActivity.this, "Start downloading...", Toast.LENGTH_SHORT).show();
                if (!isValid(downloadUrl)) {
                    errorExpected.setTextColor(Color.rgb(200, 0, 0));
                    errorExpected.setText("Error : file isn't a video. (.mp4, .wav)");
                }else {
                    new DownloadFileFromURL().execute(downloadUrl);
                    errorExpected.setText("");
                }
                Toast.makeText(ServerActivity.this, autoTextView.getText().toString(), Toast.LENGTH_SHORT).show();

                /*AcceptThread ThrSocket = new AcceptThread();
                if (!serverUp) {
                    Log.e(MainActivity.TAG, "ACTIVE THE THREAD");
                    serverUp = true;
                    spinner.setVisibility(View.VISIBLE);
                    ThrSocket.start();
                }else {
                    Log.e(MainActivity.TAG,"STOP THE THREAD");
                    serverUp = false;
                    spinner.setVisibility(View.GONE);
                    ThrSocket.interrupted();
                }*/

            }
        });


    }

    public boolean isValid(String text) {
        String ext = getFileExtension(text);
        Log.e(MainActivity.TAG, getFileExtension(text));
        switch (ext) {
            case "mp4":
                return true;
            case "wav":
                return true;
            default:
                return false;
        }

    }

    public static String getFileExtension(String fullName) {
        checkNotNull(fullName);
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket mBluetoothServerSocket;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        public AcceptThread() {
            try {
                Log.e(MainActivity.TAG, "AcceptThread LOG");
                mBluetoothServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BT_SERVER", UUID.fromString(MainActivity.UUID_CONNECTION));
            } catch (IOException e) {
                Log.e("MainActivity", e.getMessage());
            }
        }

        @Override
        public void run() {
            BluetoothSocket mBluetoothSocket;
            Log.e(MainActivity.TAG, "In run thread LOG");

            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    Log.e(MainActivity.TAG, "Bluetooth socket accept");

                    mBluetoothSocket = mBluetoothServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (mBluetoothSocket != null) {
                    // transfer the data here
                    Log.e(MainActivity.TAG, "Socket created !!!");
                    try {
                        // close the connection to stop to listen any connection now
                        mBluetoothSocket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}

