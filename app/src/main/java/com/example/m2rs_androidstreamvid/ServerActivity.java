package com.example.m2rs_androidstreamvid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import static androidx.core.util.Preconditions.checkNotNull;

@SuppressWarnings("ALL")
public class ServerActivity extends AppCompatActivity {


    private final int TIMEOUT_CONNECTION = 5000;//5sec
    private final int TIMEOUT_SOCKET = 30000;//30sec
    private ProgressBar spinner;
    private boolean serverUp = false;

    private TextView errorExpected;
    private TextView isAClientConnected;
    private Button btnDonwload;
    private Button btnLaunchServer;
    private Button btnSendFile;
    private MultiAutoCompleteTextView autoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        btnDonwload = (Button) findViewById(R.id.btnDownload);
        btnLaunchServer = (Button) findViewById(R.id.btnLaunchServer);
        btnSendFile = (Button) findViewById(R.id.btnSendFile);
        autoTextView = (MultiAutoCompleteTextView) findViewById(R.id.textViewUrl);

        //final String downloadUrl = "https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        errorExpected = (TextView) findViewById(R.id.errorExpected);
        isAClientConnected = (TextView) findViewById(R.id.isAClientConnected);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);

        spinner.setVisibility(View.GONE);
        btnSendFile.setVisibility(View.GONE);
        btnDonwload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String downloadUrl = autoTextView.getText().toString();
                if (!isValid(downloadUrl)) {
                    errorExpected.setTextColor(Color.rgb(200, 0, 0));
                    errorExpected.setText("Error : file isn't a video. (.mp4, .wav)");
                } else {
                    Toast.makeText(ServerActivity.this, "Start downloading...", Toast.LENGTH_SHORT).show();
                    new DownloadFileFromURL().execute(downloadUrl);
                    errorExpected.setText("");
                }
                Toast.makeText(ServerActivity.this, autoTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        btnLaunchServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptThread ThrSocket = new AcceptThread();
                if (!serverUp) {
                    Log.e(MainActivity.TAG, "ACTIVE THE THREAD");
                    serverUp = true;
                    spinner.setVisibility(View.VISIBLE);
                    btnLaunchServer.setText("Stop stream");
                    ThrSocket.start();
                    btnSendFile.setVisibility(View.VISIBLE);
                } else {
                    Log.e(MainActivity.TAG, "STOP THE THREAD");
                    serverUp = false;
                    btnLaunchServer.setText("Launch stream");
                    spinner.setVisibility(View.GONE);
                    ThrSocket.interrupted();
                }
            }
        });
        btnSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFile("/downloadedfile.mp4");
            }
        });
        /*ContentValues values = new ContentValues();
        values.put(BluetoothShare.URI, "file:///sdcard/refresh.txt");
        values.put(BluetoothShare.DESTINATION, deviceAddress);
        values.put(BluetoothShare.DIRECTION, BluetoothShare.DIRECTION_OUTBOUND);
        Long ts = System.currentTimeMillis();
        values.put(BluetoothShare.TIMESTAMP, ts);
        getContentResolver().insert(BluetoothShare.CONTENT_URI, values);*/

    }

    /**
     * Method to send a file from a bluetooth bridge between 2 devices. It send it into download directory phone.
     *
     * @param fileName : name file to send.
     */
    public void sendFile(String fileName) {

        Log.d(MainActivity.TAG, "Sending file...");

        File dir = Environment.getExternalStorageDirectory();
        File manualFile = new File(dir, "/" + fileName);
        Log.e(MainActivity.TAG, "MANUAL FILE DISPLAYED : " + manualFile);

        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", manualFile);
        Log.e(MainActivity.TAG, "URI DISPLAYED : " + uri);
        String type = "application/mp4";

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.setType(type);
        sharingIntent.setClassName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(sharingIntent);
    }

    /**
     * Know if a file is a mp4 or wav video file.
     *
     * @param text : the link (url)
     * @return a boolean.
     */
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

    /**
     * To get a file extension name.
     *
     * @param fullName : name of the file
     * @return the extension
     */
    public static String getFileExtension(String fullName) {
        checkNotNull(fullName);
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /**
     * This class allows to create a socket which listen via bluetooth. This one wait for a connection from a client socket.
     */
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
            Log.e(MainActivity.TAG, "thread LOG");

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
                        errorExpected.setTextColor(Color.rgb(0, 200, 0));
                        isAClientConnected.setText("A client is now connected");
                        mBluetoothSocket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    /**
     * Private class uses to download a file with a .mp4 or .wav extension from an URL given by the user.
     */
    @SuppressWarnings("ALL")
    private class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                String root = Environment.getExternalStorageDirectory().toString();

                Log.i(MainActivity.TAG, "DOWNLOADING");
                URL url = new URL(f_url[0]);

                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file

                OutputStream output = new FileOutputStream(root + "/downloadedfile.mp4");
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }


        /**
         * After completing background task
         **/
        @Override
        protected void onPostExecute(String file_url) {
            Log.i(MainActivity.TAG, "FILE SUCCESSFULLY DOWNLOADED.");
            errorExpected.setTextColor(Color.rgb(0, 200, 0));
            errorExpected.setText("File successfully downloaded.");
        }

    }


}

