package com.hafezi.games.spaceshooter2d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    Button playButton;
    Button tutorialButton;
    Button highscoreButton;
    Button exitButton;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();
        playButton = (Button) findViewById(R.id.playButton);
        tutorialButton = (Button) findViewById(R.id.tutorialButton);
        highscoreButton = (Button) findViewById(R.id.scoreButton);
        exitButton = (Button) findViewById(R.id.exitButton);

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
                soundManager.playSound("menu");
                playButton.setBackgroundResource(R.drawable.yellow_button);
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound("menu");
                exitButton.setBackgroundResource(R.drawable.yellow_button);
                soundManager.releasePlayer();
                finish();
            }
        });

        highscoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound("menu");
                highscoreButton.setBackgroundResource(R.drawable.yellow_button);

                //start new activity
            }
        });


        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound("menu");
                tutorialButton.setBackgroundResource(R.drawable.yellow_button);

                //start new activity
            }
        });
    }


    private void resetButtons() {
        playButton.setBackgroundResource(R.drawable.blue_button);
        highscoreButton.setBackgroundResource(R.drawable.blue_button);
        tutorialButton.setBackgroundResource(R.drawable.blue_button);
        exitButton.setBackgroundResource(R.drawable.blue_button);
    }
}
