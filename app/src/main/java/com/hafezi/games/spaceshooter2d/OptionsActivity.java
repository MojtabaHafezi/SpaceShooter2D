package com.hafezi.games.spaceshooter2d;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.hafezi.games.spaceshooter2d.Utility.Pref;

public class OptionsActivity extends AppCompatActivity {

    Button audioEnableButton;
    Button audioDisableButton;
    Button accelEnableButton;
    Button accelDisableButton;
    Button tutorialButton;
    Button bluetoothButton;
    Button saveButton;

    //Utility
    private SoundManager soundManager;


    //persistence
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private boolean usingSensor;
    private boolean isMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        sharedPreferences = getSharedPreferences(Pref.GAME.toString(), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();
        audioEnableButton = (Button) findViewById(R.id.audioEnableButton);
        audioDisableButton = (Button) findViewById(R.id.audioDisableButton);
        accelEnableButton = (Button) findViewById(R.id.accelEnableButton);
        accelDisableButton = (Button) findViewById(R.id.accelDisableButton);
        tutorialButton = (Button) findViewById(R.id.tutorialButton);
        bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        loadData();
        setButtonListeners();
        setButtonStates();

    }

    private void loadData() {
        boolean isMute = sharedPreferences.getBoolean(Pref.AUDIO.toString(), false);
        setUsingSensor(sharedPreferences.getBoolean(Pref.SENSOR.toString(), false));
        setMute(isMute);
        soundManager.setMute(isMute());
    }


    private void setButtonListeners() {

        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                //TODO: Start playing the demo video
            }
        });

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                //save and start new activity
                saveOptions();
                Intent i = new Intent(OptionsActivity.this, BluetoothActivity.class);
                startActivity(i);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                saveOptions();
                //soundManager.releasePlayer();
                finish();
            }
        });

        audioEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMute(false);
                soundManager.setMute(false);
                setButtonStates();
                soundManager.playSound(SoundManager.Sounds.MENU);
            }
        });

        audioDisableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMute(true);
                soundManager.setMute(true);
                setButtonStates();
            }
        });

        accelEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUsingSensor(true);
                setButtonStates();
                soundManager.playSound(SoundManager.Sounds.MENU);
            }
        });

        accelDisableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUsingSensor(false);
                setButtonStates();
                soundManager.playSound(SoundManager.Sounds.MENU);
            }
        });
    }

    //Set the buttons states depending on the boolean values
    private void setButtonStates() {
        if (isMute()) {
            audioDisableButton.setBackgroundResource(R.drawable.blue_button);
            audioDisableButton.setAlpha(1f);
            audioEnableButton.setBackgroundResource(R.drawable.red_button);
            audioEnableButton.setAlpha(.5f);
        } else {
            audioEnableButton.setBackgroundResource(R.drawable.blue_button);
            audioEnableButton.setAlpha(1f);
            audioDisableButton.setBackgroundResource(R.drawable.red_button);
            audioDisableButton.setAlpha(.5f);
        }

        if (isUsingSensor()) {
            accelEnableButton.setBackgroundResource(R.drawable.blue_button);
            accelEnableButton.setAlpha(1f);
            accelDisableButton.setBackgroundResource(R.drawable.red_button);
            accelDisableButton.setAlpha(.5f);
        } else {
            accelDisableButton.setBackgroundResource(R.drawable.blue_button);
            accelDisableButton.setAlpha(1f);
            accelEnableButton.setBackgroundResource(R.drawable.red_button);
            accelEnableButton.setAlpha(.5f);
        }
    }

    private void saveOptions() {
        editor.putBoolean(Pref.SENSOR.toString(), isUsingSensor());
        editor.putBoolean(Pref.AUDIO.toString(), isMute());
        editor.commit();
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

    // If the player hits the back button, quit the app
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //if quit without saving -> load old data
            loadData();
            finish();
            return true;
        }
        return false;
    }


    //GETTERS AND SETTERS
    public boolean isUsingSensor() {
        return usingSensor;
    }

    public void setUsingSensor(boolean usingSensor) {
        this.usingSensor = usingSensor;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        this.isMute = mute;
    }
}
