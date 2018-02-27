package com.hafezi.games.spaceshooter2d.Utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Debug;
import android.text.InputType;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.hafezi.games.spaceshooter2d.GameObjects.Player;
import com.hafezi.games.spaceshooter2d.GameView;

import java.util.ArrayList;

/**
 * Created by Mojtaba Hafezi on 21.02.2018.
 */

public class InputController {
    private Rect up;
    private Rect down;
    private Rect shoot;
    private GameView gameView;

    private float[] gravity = new float[]{0, 0, 0};
    private float[] linearAcceleration = new float[]{0, 0, 0};
    final float alpha = 0.915f;

    public InputController(GameView gameView, int screenWidth, int screenHeight) {
        //divide the android screen into up and down area
        up = new Rect(0, 0, screenWidth, screenHeight / 2);
        down = new Rect(0, (screenHeight / 2 - 1), screenWidth, screenHeight);
        //right half of the screen activates shooting
        shoot = new Rect(screenWidth / 2, 0, screenWidth, screenHeight);
        this.gameView = gameView;
    }

    public void handleSensorInput(SensorEvent sensorEvent, Player player) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linearAcceleration[0] = sensorEvent.values[0] - gravity[0];
            linearAcceleration[1] = sensorEvent.values[1] - gravity[1];
            linearAcceleration[2] = sensorEvent.values[2] - gravity[2];


            float x = linearAcceleration[0];
            float y = linearAcceleration[1];
            float z = linearAcceleration[2];

            if (x >= 1) {
                player.setMoveUp(false);
                player.setMoveDown(true);
            }

            if (x <= -1) {
                player.setMoveDown(false);
                player.setMoveUp(true);
            }

            if (x > -1 && x < 1) {
                player.setMoveDown(false);
                player.setMoveUp(false);
            }
        }
    }

    public void handleTouchInput(MotionEvent motionEvent, Player player) {
        int horizontal = (int) motionEvent.getX();
        int vertical = (int) motionEvent.getY();

        switch (motionEvent.getAction() & motionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (shoot.contains(horizontal, vertical))
                    player.fireLaser();
                //check if the user presses on the upper half or lower half of the screen
                if (up.contains(horizontal, vertical)) {
                    player.setMoveUp(true);
                    player.setMoveDown(false);
                } else if (down.contains(horizontal, vertical)) {
                    player.setMoveDown(true);
                    player.setMoveUp(false);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (up.contains(horizontal, vertical)) {
                    player.setMoveUp(false);
                } else if (down.contains(horizontal, vertical)) {
                    player.setMoveDown(false);
                }
                break;
        }
    }

    // Handle gamepad and D-pad button presses to
    // navigate the ship and fire
    public void handleControllerKeysInput(KeyEvent event, Player player) {
        int keyCode = event.getKeyCode();
        int eventAction = event.getAction();
        boolean isGamePad = ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD);
        boolean isJoystick = ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK);
        boolean isDpad = ((event.getSource() & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD);

        if (isGamePad || isDpad || isJoystick) {
            switch (keyCode) {
                //pause game
                case KeyEvent.KEYCODE_BUTTON_START:
                    if (eventAction == KeyEvent.ACTION_DOWN) {
                        if (gameView.isPlaying())
                            gameView.pause();
                        else
                            gameView.resume();
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (eventAction == KeyEvent.ACTION_DOWN) {
                        player.setMoveUp(true);
                        player.setMoveDown(false);
                    } else if (eventAction == KeyEvent.ACTION_UP) {
                        player.setMoveUp(false);
                        player.setMoveDown(false);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (eventAction == KeyEvent.ACTION_DOWN) {
                        player.setMoveUp(false);
                        player.setMoveDown(true);
                    } else if (eventAction == KeyEvent.ACTION_UP) {
                        player.setMoveUp(false);
                        player.setMoveDown(false);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_BUTTON_A:
                case KeyEvent.KEYCODE_BUTTON_X:
                    player.fireLaser();
                    break;

            }
        }
    }

    //Handle joysticks
    public void handleControllerMotionInput(MotionEvent event, Player player) {

        boolean isGamePad = ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD);
        boolean isJoystick = ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK);
        boolean isDpad = ((event.getSource() & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD);

        if (event.getAction() == MotionEvent.ACTION_MOVE && (isGamePad || isDpad || isJoystick)) {
            float vertical = event.getAxisValue(MotionEvent.AXIS_Y);
            if (vertical > 0.1) {
                player.setMoveDown(true);
                player.setMoveUp(false);

            } else if (vertical < -0.1) {
                player.setMoveUp(true);
                player.setMoveDown(false);
            } else {
                player.setMoveDown(false);
                player.setMoveUp(false);
            }
        }
    }
}
