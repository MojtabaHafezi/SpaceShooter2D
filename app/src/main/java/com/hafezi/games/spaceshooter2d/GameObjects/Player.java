package com.hafezi.games.spaceshooter2d.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Mojtaba Hafezi on 19.02.2018.
 */

public class Player extends GameObject {

    private int shields;
    private boolean moveUp;
    private boolean moveDown;
    private Laser laser;

    /***************************************************************************************
     *    @Citation
     *    Title: Android game programming by example
     *    Author: Horton John
     *    Date: 2015
     *    Code version: 1.0
     *    Change of original code: Major
     *    The basic idea for this code was acquired through reading the mentioned book
     ***************************************************************************************/
    public Player(Context context, int startX, int startY, int speed, int screenX, int screenY) {
        setContext(context);
        setScreenX(screenX);
        setScreenY(screenY);
        setY(startY);
        setSpeed(speed);
        setShields(2);
        prepareBitmap("player");
        setX(getBitmap().getWidth() / 3);
        setMinY(0);
        setMinX(0);
        setMaxX(screenX - getBitmap().getWidth());
        setMaxY(screenY - getBitmap().getHeight());
        setWidth(getBitmap().getWidth());
        setHeight(getBitmap().getHeight());
        setHitbox(new Rect(getX(), getY(), getWidth(), getHeight()));
        //laser is not visible until shot
        laser = new Laser(getContext(), getScreenX(), getScreenY(), -1000, -1000);
    }

    @Override
    public void update() {
        laser.update();

        if (isMoveDown()) {
            setY(getY() + getSpeed());
        }
        if (isMoveUp()) {
            setY(getY() - getSpeed());
        }

        if (getY() <= getMinY()) {
            setY(getMinY());
        }
        if (getY() >= getMaxY()) {
            setY(getMaxY());
        }

        //update location of the rectangle collision hitbox with some margin
        getHitbox().left = getX();
        getHitbox().right = getX() + getBitmap().getWidth() - getBitmap().getWidth() / 5;
        getHitbox().top = getY();
        getHitbox().bottom = getY() + getBitmap().getHeight() - getBitmap().getHeight() / 5;


    }

    public void fireLaser() {
        if (laser.isAvailable()) {
            laser.setAvailable(false);
            laser.setPosition(getX() + getWidth(), getY() + getHeight() / 2);
        }
    }

    public int getShields() {
        return shields;
    }

    public void setShields(int shields) {
        this.shields = shields;
    }


    public boolean isMoveUp() {
        return moveUp;
    }

    public void setMoveUp(boolean moveUp) {
        this.moveUp = moveUp;
    }

    public boolean isMoveDown() {
        return moveDown;
    }

    public void setMoveDown(boolean moveDown) {
        this.moveDown = moveDown;
    }

    public Laser getLaser() {
        return this.laser;
    }
}
