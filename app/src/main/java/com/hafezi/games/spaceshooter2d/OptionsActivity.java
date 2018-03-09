package com.hafezi.games.spaceshooter2d;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

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
    private MediaController mediaController;
    private VideoView videoHolder;

    //persistence
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private boolean usingSensor;
    private boolean isMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        //get utility instances
        mediaController = new MediaController(this);
        videoHolder = new VideoView(OptionsActivity.this);
        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();
        //get the values for the options
        sharedPreferences = getSharedPreferences(Pref.GAME.toString(), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //find buttons
        audioEnableButton = (Button) findViewById(R.id.audioEnableButton);
        audioDisableButton = (Button) findViewById(R.id.audioDisableButton);
        accelEnableButton = (Button) findViewById(R.id.accelEnableButton);
        accelDisableButton = (Button) findViewById(R.id.accelDisableButton);
        tutorialButton = (Button) findViewById(R.id.tutorialButton);
        bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        //load the data and set the button listeners and their states
        loadData();
        setButtonListeners();
        setButtonStates();
    }

    //is used to bring the default state of the option screen back after the video is played
    private void initialiseView() {
        soundManager.playMusic();
        audioEnableButton = (Button) findViewById(R.id.audioEnableButton);
        audioDisableButton = (Button) findViewById(R.id.audioDisableButton);
        accelEnableButton = (Button) findViewById(R.id.accelEnableButton);
        accelDisableButton = (Button) findViewById(R.id.accelDisableButton);
        tutorialButton = (Button) findViewById(R.id.tutorialButton);
        bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        setButtonListeners();
        setButtonStates();
    }

    //loads the boolean values from the shared preferences
    private void loadData() {
        boolean isMute = sharedPreferences.getBoolean(Pref.AUDIO.toString(), false);
        setUsingSensor(sharedPreferences.getBoolean(Pref.SENSOR.toString(), false));
        setMute(isMute);
        soundManager.setMute(isMute());
    }

    // private method to save data using shared preferences
    private void saveOptions() {
        editor.putBoolean(Pref.SENSOR.toString(), isUsingSensor());
        editor.putBoolean(Pref.AUDIO.toString(), isMute());
        editor.commit();
    }


    // If the player hits the back button while video is playing leads to closing the video player
    // Should it already been closed then the changed data (boolean values) are discarded
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (videoHolder.isPlaying()) {
                videoHolder.stopPlayback();
                videoHolder.clearFocus();
                OptionsActivity.this.setContentView(R.layout.activity_options);
                initialiseView();
            } else {
                //if quit without saving -> load old data
                loadData();
                finish();
            }


            return true;
        }
        return false;
    }


    /*
    *   Setting the focus to the video holder and reinitialising the content view to default
    *   is implemented into the tutorial button.
    *   Otherwise the following lines of code are similar to main activity
    */

    /***************************************************************************************
     *    @Citation
     *    Title: VideoView
     *    Author: Google LLC
     *    Date: 2018
     *    Code version: 1.0
     *    Change of original code: Major
     *    Original idea for: displaying the video view
     *    Available at: https://developer.android.com/reference/android/widget/VideoView.html
     *    Last access: 09.03.2018
     ***************************************************************************************/
    private void setButtonListeners() {

        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                //with controls if the apk allows it
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    videoHolder.setMediaController(mediaController);
                }
                //get the tutorial video inside the raw folder
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tutorial);
                videoHolder.setVideoURI(video);
                //change content of the activity
                setContentView(videoHolder);
                soundManager.stopMusic();
                videoHolder.requestFocus();
                videoHolder.start();

                //when video finishes the content is set back to the right layout and set to default state
                videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        videoHolder.stopPlayback();
                        videoHolder.clearFocus();
                        OptionsActivity.this.setContentView(R.layout.activity_options);
                        initialiseView();
                    }
                });
            }
        });

        tutorialButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    tutorialButton.setBackgroundResource(R.drawable.red_button);
                else
                    tutorialButton.setBackgroundResource(R.drawable.blue_button);
            }
        });

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound(SoundManager.Sounds.MENU);
                //save and start new activity
                saveOptions();
                Intent i = new Intent(OptionsActivity.this, BluetoothActivity.class);
                finish();
                startActivity(i);
            }
        });

        bluetoothButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    bluetoothButton.setBackgroundResource(R.drawable.red_button);
                else
                    bluetoothButton.setBackgroundResource(R.drawable.blue_button);
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

        saveButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    saveButton.setBackgroundResource(R.drawable.red_button);
                else
                    saveButton.setBackgroundResource(R.drawable.blue_button);
            }
        });

        audioEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMute(false);
                soundManager.setMute(false);
                setButtonStates();
                soundManager.playSound(SoundManager.Sounds.MENU);
                soundManager.playMusic();
            }
        });

        audioDisableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMute(true);
                soundManager.setMute(true);
                setButtonStates();
                soundManager.stopMusic();
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

    //Set the buttons states depending on the boolean values - changes alpha value for transparency
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
