package com.hafezi.games.spaceshooter2d;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button playButton;
    Button optionButton;
    Button highscoreButton;
    Button exitButton;
    private SoundManager soundManager;
    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get the bluetooth adapter -> on quit disable bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //set the instance of the soundManager
        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();

        //get the buttons
        playButton = (Button) findViewById(R.id.playButton);
        optionButton = (Button) findViewById(R.id.optionButton);
        highscoreButton = (Button) findViewById(R.id.scoreButton);
        exitButton = (Button) findViewById(R.id.exitButton);

        //Set the Listeners for the buttons
        setButtonListeners();

    }

    //play the music if the application continues
    @Override
    protected void onResume() {
        super.onResume();
        soundManager.playMusic();
        //reset the background images of the buttons
        resetButtons();
    }

    // pause the music as well
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

    /*the listeners for change of focus and onClick are set here accordingly
    // the soundManager is used to play the corresponding sound effects
    // the exit button additionally disables the bluetooth and released the media player
    */
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

        playButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    playButton.setBackgroundResource(R.drawable.red_button);
                else
                    playButton.setBackgroundResource(R.drawable.blue_button);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                exitButton.setBackgroundResource(R.drawable.yellow_button);
                soundManager.releasePlayer();
                bluetoothAdapter.disable();
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

        highscoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                highscoreButton.setBackgroundResource(R.drawable.yellow_button);
                Intent i = new Intent(MainActivity.this, HighScoreActivity.class);
                startActivity(i);
            }
        });

        highscoreButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    highscoreButton.setBackgroundResource(R.drawable.red_button);
                else
                    highscoreButton.setBackgroundResource(R.drawable.blue_button);
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

        optionButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    optionButton.setBackgroundResource(R.drawable.red_button);
                else
                    optionButton.setBackgroundResource(R.drawable.blue_button);
            }
        });
    }


    //simple method to reset all buttons to their initial background
    private void resetButtons() {
        playButton.setBackgroundResource(R.drawable.blue_button);
        highscoreButton.setBackgroundResource(R.drawable.blue_button);
        optionButton.setBackgroundResource(R.drawable.blue_button);
        exitButton.setBackgroundResource(R.drawable.blue_button);
    }


}
