package com.hafezi.games.spaceshooter2d;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by Mojtaba Hafezi on 18.02.2018.
 */

public class GameActivity extends Activity {

    private GameView gameView;
    private SoundManager soundManager;

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

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        gameView.handleControllerMotion(event);
        return super.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        gameView.handleControllerKeys(event);
        return super.dispatchKeyEvent(event);
    }
}
