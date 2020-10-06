package com.example.m2rs_androidstreamvid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button clientButton = (Button) findViewById(R.id.btnClient);
        final Button serverButton = (Button) findViewById(R.id.btnServer);
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

    }


}