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
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hafezi.games.spaceshooter2d.Utility.DeviceAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Button exitButton;
    Button activateButton;
    Button discoverButton;

    private SoundManager soundManager;
    //bluetooth
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    public ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    private ListView newDevices;
    //required to convert array list of BT devices into ListView
    public DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();
        exitButton = (Button) findViewById(R.id.backButton);
        activateButton = (Button) findViewById(R.id.activateButton);
        discoverButton = (Button) findViewById(R.id.discoverButton);
        setButtonListeners();

        //BLUETOOTH settings
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevices = new ArrayList<>();
        newDevices = (ListView) findViewById(R.id.listView);
        newDevices.setOnItemClickListener(BluetoothActivity.this);

        //Broadcast when pairing status changes
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadCastPairing, intentFilter);

        //Broadcast when discovering new devices
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadCastDiscovery, discoverDevicesIntent);

    }

    private void setButtonListeners() {

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                unregisterReceiver();
                //soundManager.releasePlayer();
                finish();
            }
        });

        exitButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    exitButton.setBackgroundResource(R.drawable.red_button);
                else
                    exitButton.setBackgroundResource(R.drawable.blue_button);
            }
        });

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                activateBluetooth();
            }
        });

        activateButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    activateButton.setBackgroundResource(R.drawable.red_button);
                else
                    activateButton.setBackgroundResource(R.drawable.blue_button);
            }
        });

        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                discoverDevices();
            }
        });

        discoverButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    discoverButton.setBackgroundResource(R.drawable.red_button);
                else
                    discoverButton.setBackgroundResource(R.drawable.blue_button);
            }
        });

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
        //if device supports bluetooth -> activate if not already on
        if (!(bluetoothAdapter == null)) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);

                IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(broadCastActivation, intentFilter);

            } else {
                bluetoothAdapter.disable();

                IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(broadCastActivation, intentFilter);
            }
        }
    }


    private void discoverDevices() {
        if (!bluetoothAdapter.isEnabled())
            activateBluetooth();

        if (bluetoothAdapter.isDiscovering()) {
            //if it is already discovering - cancel and restart
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
            //Broadcast when discovering new devices
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadCastDiscovery, discoverDevicesIntent);
            showShortToast(getBaseContext(), "Searching for devices...");

        } else {
            bluetoothAdapter.startDiscovery();
            //Broadcast when discovering new devices
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadCastDiscovery, discoverDevicesIntent);
            showShortToast(getBaseContext(), "Searching for devices...");

        }
    }

    //Broadcast receiver for discovering
    private final BroadcastReceiver broadCastDiscovery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!bluetoothDevices.contains(device))
                    bluetoothDevices.add(device);
                updateList(context);
            }
        }
    };

    //Broadcast receiver for pairing
    private final BroadcastReceiver broadCastPairing = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    showShortToast(context, "Bluetooth pairing finished.");
                }
                //case2: creating a bone
                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    showShortToast(context, "Pairing...");
                }
            }
        }
    };

    //Broadcast receiver for enabling/disabling BT
    private final BroadcastReceiver broadCastActivation = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        //disabled bluetooth -> no devices
                        showShortToast(context, "Bluetooth disabled.");
                        //get paired device
                        bluetoothDevices.clear();
                        updateList(getBaseContext());
                        break;
                    case BluetoothAdapter.STATE_ON:
                        //enabled bluetooth -> show devices
                        showShortToast(context, "Bluetooth enabled.");
                        //get paired device
                        bluetoothDevices.clear();
                        pairedDevices = bluetoothAdapter.getBondedDevices();
                        for (BluetoothDevice bluetoothDevice : pairedDevices) {
                            bluetoothDevices.add(bluetoothDevice);
                        }
                        updateList(getBaseContext());
                        break;
                        /* not required for this project
                        case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.e("BT", "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                        Log.e("BT", "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                        */
                }
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Cancel discovery to save energy
        bluetoothAdapter.cancelDiscovery();

        showShortToast(getBaseContext(), "Bluetooth enabled.");
        String deviceName = bluetoothDevices.get(i).getName();

        bluetoothDevices.get(i).createBond();
    }

    //Shows a short toast with given text
    private void showShortToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    //updates the list by adding all items from the bluetoothDevices container
    private void updateList(Context context) {
        //Convert from ArrayList to ListView
        deviceAdapter = new DeviceAdapter(context, bluetoothDevices);
        newDevices.setAdapter(deviceAdapter);
    }

    private void unregisterReceiver() {
        try {
            unregisterReceiver(broadCastPairing);
            unregisterReceiver(broadCastActivation);
            unregisterReceiver(broadCastDiscovery);
        } catch (Exception e) {
            Log.e("BT", "Trying to unregister not registered receiver");
        }
        finish();
    }

}
