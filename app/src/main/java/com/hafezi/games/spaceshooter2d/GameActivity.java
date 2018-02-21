package com.hafezi.games.spaceshooter2d;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;

/**
 * Created by Mojtaba Hafezi on 18.02.2018.
 */

public class GameActivity extends Activity {

    private GameView gameView;
    private SoundManager soundManager;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DETECT SCREEN RESOLUTION
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();


        gameView = new GameView(GameActivity.this, point.x, point.y);
        //setContentView(R.layout.activity_game);
        setContentView(gameView);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
        soundManager.stopMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
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
