package com.example.m2rs_androidstreamvid;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

@SuppressWarnings("ALL")
public class ServerActivity extends AppCompatActivity {

    private final int TIMEOUT_CONNECTION = 5000;//5sec
    private final int TIMEOUT_SOCKET = 30000;//30sec

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        final String downloadUrl = "https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        final Button btnDonwload = (Button) findViewById(R.id.btnDownload);
        final MultiAutoCompleteTextView autoTextView = (MultiAutoCompleteTextView) findViewById(R.id.textViewUrl);

        btnDonwload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast.makeText(ServerActivity.this, "Start downloading...", Toast.LENGTH_SHORT).show();
                new DownloadFileFromURL().execute(downloadUrl);

                Toast.makeText(ServerActivity.this, autoTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}