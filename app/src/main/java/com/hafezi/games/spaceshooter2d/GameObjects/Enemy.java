package com.hafezi.games.spaceshooter2d.GameObjects;

import android.content.Context;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Mojtaba Hafezi on 21.02.2018.
 */


public class Enemy extends GameObject {

    private Random random;
    private int shield;

    /***************************************************************************************
     *    @Citation
     *    Title: Android game programming by example
     *    Author: Horton John
     *    Date: 2015
     *    Code version: 1.0
     *    Change of original code: Major
     *    The basic idea for this code was acquired through reading the mentioned book
     ***************************************************************************************/
    public Enemy(Context context, int screenX, int screenY) {
        random = new Random();
        setContext(context);
        setScreenX(screenX);
        setScreenY(screenY);
        setMinX(0);
        setMinY(0);
        //choose a random enemy sprite
        String bitmapName = "enemy" + (random.nextInt(5) + 1);
        prepareBitmap(bitmapName);
        setMaxX(getScreenX());
        setMaxY(getScreenY());
        setWidth(getBitmap().getWidth());
        setHeight(getBitmap().getHeight());
        setHitbox(new Rect(getX(), getY(), getWidth(), getHeight()));
        setRandomAttributes();
    }

    public void setRandomAttributes() {
        int randomPosition = random.nextInt(getScreenY() - getBitmap().getHeight());
        int randomSpeed = 5 + random.nextInt(5) ;
        int randomShields = 1 + random.nextInt(3);
        setX(getScreenX() + getBitmap().getWidth());
        setY(randomPosition);
        setSpeed(randomSpeed);
        setShield(randomShields);
    }

    @Override
    public void update() {

        setX(getX() - getSpeed());
        if ((getX() <= (getMinX() - getBitmap().getWidth())) || getShield() <= 0) {
            setX(getMaxX());
            setRandomAttributes();
        }

        //update location of the rectangle collision hitbox
        getHitbox().left = getX();
        getHitbox().right = getX() + getBitmap().getWidth() - getBitmap().getWidth() / 6;
        getHitbox().top = getY();
        getHitbox().bottom = getY() + getBitmap().getHeight() - getBitmap().getHeight() / 6;

    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }
}
