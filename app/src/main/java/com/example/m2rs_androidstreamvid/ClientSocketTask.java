package com.example.m2rs_androidstreamvid;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

public class ClientSocketTask extends AsyncTask<Void, Void, Void> {

    private boolean mConnected = true;
    private ProgressDialog mProgressDialog;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket mBluetoothSocket = null;
    private AppCompatActivity mCurrentActivity = null;
    private String mAddress = null;

    private static final UUID myUUID = UUID.fromString(MainActivity.UUID_CONNECTION);

    ClientSocketTask(AppCompatActivity activity, String address) {
        mCurrentActivity = activity;
        mAddress =  address;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mCurrentActivity, "Connecting...", "Please wait!!!");  //show a progress dialog
    }

    /**
     * Meanwhile the dialog is showing something to the client in order to wait, the method below allows use to run in background
     **/

    @Override
    protected Void doInBackground(Void... devices) {
        try {
            if (mBluetoothSocket == null || !mConnected) {
                //Here we create the client socket that ask to the server if he can connect by bluetooth using an unique UUID
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mAddress);//connect to the device by using is mac adress and check if the connection is available
                mBluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);//create a RFCOMM connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                mBluetoothSocket.connect();//start connection
                Log.e(MainActivity.TAG, "Socket connected");
            }
        }
        catch (IOException e) {
            mConnected = false;
            Log.e(MainActivity.TAG, "error catch");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) { //after the doInBackground, it checks if everything went fine
        super.onPostExecute(result);
        if (!mConnected){
            message("Connection Failed. Try again.");
            mCurrentActivity.finish();
        }
        else {
            message("Connected.");
        }
        mProgressDialog.dismiss();
    }

    public void disconnect() {
        if (mBluetoothSocket!=null) {
            try  {
                mBluetoothSocket.close();
            }
            catch (IOException e) {
                message("Error");
            }
        }
        message("Disconnected");
        mCurrentActivity.finish();
    }

    private void message(String s) {
        Toast.makeText(mCurrentActivity.getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }

}