package com.hafezi.games.spaceshooter2d;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button playButton;
    Button optionButton;
    Button highscoreButton;
    Button exitButton;
    private SoundManager soundManager;
    //bluetooth
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();
        playButton = (Button) findViewById(R.id.playButton);
        optionButton = (Button) findViewById(R.id.optionButton);
        highscoreButton = (Button) findViewById(R.id.scoreButton);
        exitButton = (Button) findViewById(R.id.exitButton);

        //activate bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //if device supports bluetooth -> activate if not already on
        if (! (bluetoothAdapter == null)) {
            if(!bluetoothAdapter.isEnabled())
            {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
            }
            pairedDevices = bluetoothAdapter.getBondedDevices();
            for(BluetoothDevice bluetoothDevice : pairedDevices)
            {
                Log.e("DEVICE:",  bluetoothDevice.getName());
            }
        }



        //Set the Listeners
        setButtonListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.playMusic();
        //reset the background images of the buttons
        resetButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundManager.stopMusic();
    }

    // If the player hits the back button, quit the app
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            soundManager.releasePlayer();
            finish();
            return true;
        }
        return false;
    }

    private void setButtonListeners() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                playButton.setBackgroundResource(R.drawable.yellow_button);
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                exitButton.setBackgroundResource(R.drawable.yellow_button);
                soundManager.releasePlayer();
                finish();
            }
        });

        highscoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                highscoreButton.setBackgroundResource(R.drawable.yellow_button);

                //start new activity
            }
        });


        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                optionButton.setBackgroundResource(R.drawable.yellow_button);
                Intent i = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(i);
            }
        });
    }


    private void resetButtons() {
        playButton.setBackgroundResource(R.drawable.blue_button);
        highscoreButton.setBackgroundResource(R.drawable.blue_button);
        optionButton.setBackgroundResource(R.drawable.blue_button);
        exitButton.setBackgroundResource(R.drawable.blue_button);
    }
}
