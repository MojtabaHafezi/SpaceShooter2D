package com.hafezi.games.spaceshooter2d.Utility;

import android.graphics.Rect;
import android.os.Debug;
import android.util.Log;
import android.view.MotionEvent;

import com.hafezi.games.spaceshooter2d.GameObjects.Player;

/**
 * Created by Mojtaba Hafezi on 21.02.2018.
 */

public class InputController {
    public Rect up;
    public Rect down;

    public InputController(int screenWidth, int screenHeight) {
        //divide the android screen into up and down area
        up = new Rect(0, 0, screenWidth, screenHeight / 2);
        down = new Rect(0, (screenHeight/2 - 1), screenWidth,screenHeight);
    }


    public void handleInput(MotionEvent motionEvent, Player player) {
        int horizontal = (int) motionEvent.getX();
        int vertical = (int) motionEvent.getY();

        switch (motionEvent.getAction() & motionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
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
}
