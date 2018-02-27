package com.hafezi.games.spaceshooter2d;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class HighScoreActivity extends AppCompatActivity {

    private Button exitButton;
    private Button playButton;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();
        exitButton = (Button) findViewById(R.id.hsBackButton);
        playButton = (Button) findViewById(R.id.hsPlayButton);
        setButtonListener();
    }

    private void setButtonListener() {
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitButton.setBackgroundResource(R.drawable.yellow_button);
                soundManager.playSound(SoundManager.Sounds.MENU);
                Intent i = new Intent(HighScoreActivity.this, MainActivity.class);
                finish();
                startActivity(i);

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


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButton.setBackgroundResource(R.drawable.yellow_button);
                soundManager.playSound(SoundManager.Sounds.MENU);
                Intent i = new Intent(HighScoreActivity.this, GameActivity.class);
                finish();
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
    }


    @Override
    protected void onResume() {
        super.onResume();
        soundManager.playMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundManager.stopMusic();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }
}
