package com.hafezi.games.spaceshooter2d.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by Mojtaba Hafezi on 19.02.2018.
 */

public class Player extends GameObject {

    private int shields;

    public Player(Context context, int startX, int startY, int speed, int screenX, int screenY) {
        setContext(context);
        setScreenX(screenX);
        setScreenY(screenY);
        setX(startX);
        setY(startY);
        setSpeed(speed);
        setShields(2);
        prepareBitmap("player");
        setMinY(0 + getBitmap().getHeight());
        setMinX(0);
        setMaxX(screenX - getBitmap().getWidth());
        setMaxY(screenY);
        setWidth(getBitmap().getWidth());
        setHeight(getBitmap().getHeight());
        setHitbox(new Rect(getX(), getY(), getWidth(), getHeight()));

    }

    @Override
    public void update() {

    }

    public int getShields() {
        return shields;
    }

    public void setShields(int shields) {
        this.shields = shields;
    }


}
