package com.hafezi.games.spaceshooter2d;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends Activity {

    private SoundManager soundManager;
    //bluetooth
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    public ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();

        //BLUETOOTH settings
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevices = new ArrayList<>();

        //Broadcast when pairing status changes
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadCastPairing, intentFilter);


    }

    // If the player hits the back button, quit the app
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            unregisterReceiver();
            finish();
            return true;
        }
        return false;
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundManager.stopMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.playMusic();
    }

    private void activateBluetooth() {
        Log.e("MAIN", "WORKS TILL HERE");
        //if device supports bluetooth -> activate if not already on
        if (!(bluetoothAdapter == null)) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
            }

            //get paired device
            pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice bluetoothDevice : pairedDevices) {
                Log.e("DEVICE:", bluetoothDevice.getName());
            }
        }
    }


    private void discoverDevices() {
        if (bluetoothAdapter.isDiscovering()) {
            //if it is already discovering - cancel and restart
            bluetoothAdapter.cancelDiscovery();
            //checkPermission();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadCastDiscovery, discoverDevicesIntent);
        }
        if (!bluetoothAdapter.isDiscovering()) {
            //checkPermission();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadCastDiscovery, discoverDevicesIntent);
        }
    }


    private void checkPermission() {
        //check for bluetooth permission in the manifest
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d("Main", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    private void unregisterReceiver() {
        unregisterReceiver(broadCastDiscovery);
        unregisterReceiver(broadCastPairing);
    }

    /**
     * Broadcast Receiver for the BT discovery
     */
    private final BroadcastReceiver broadCastDiscovery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(device);
                Log.d("MAIN", "onReceive: " + device.getName() + ": " + device.getAddress());
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver broadCastPairing = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.e("MAIN", "Broadcast: CREATED BOND");
                }
                //case2: creating a bone
                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.e("MAIN", "Broadcast: CREATING BOND");
                }
                //case3: breaking a bond
                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.e("MAIN", "Broadcast: BROKEN BOND");
                }
            }
        }
    };

}
