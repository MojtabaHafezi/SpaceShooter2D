package com.hafezi.games.spaceshooter2d;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class OptionsActivity extends AppCompatActivity {

    Button audioEnableButton;
    Button audioDisableButton;
    Button accelEnableButton;
    Button accelDisableButton;
    Button tutorialButton;
    Button saveButton;

    //Utility
    private SoundManager soundManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //persistence


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        sharedPreferences = getSharedPreferences("GAME", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();
        audioEnableButton = (Button) findViewById(R.id.audioEnableButton);
        audioDisableButton = (Button) findViewById(R.id.audioDisableButton);
        accelEnableButton = (Button) findViewById(R.id.accelEnableButton);
        accelDisableButton = (Button) findViewById(R.id.accelDisableButton);
        tutorialButton = (Button) findViewById(R.id.tutorialButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        loadData();
        setButtonListeners();

    }

    private void loadData()
    {


    }


    private void setButtonListeners() {
        
    }


    private void resetButtons() {


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
            finish();
            return true;
        }
        return false;
    }
}
