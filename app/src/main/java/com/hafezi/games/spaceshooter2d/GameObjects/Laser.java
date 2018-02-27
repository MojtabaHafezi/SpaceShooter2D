package com.hafezi.games.spaceshooter2d.GameObjects;

import android.content.Context;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Mojtaba Hafezi on 27.02.2018.
 */

public class Laser extends GameObject {
    private Random random;
    private boolean isAvailable;

    public Laser(Context context,  int screenX, int screenY, int startX, int startY) {
        setContext(context);
        setScreenX(screenX);
        setScreenY(screenY);
        setAvailable(true);
        random = new Random();
        int randomSpeed = 40 + random.nextInt(5);
        setSpeed(randomSpeed);
        prepareBitmap("laser");
        setMinY(0);
        setMinX(0);
        setMaxX(screenX - getBitmap().getWidth());
        setMaxY(screenY - getBitmap().getHeight());
        setWidth(getBitmap().getWidth());
        setHeight(getBitmap().getHeight());
        setHitbox(new Rect(getX(), getY(), getWidth(), getHeight()));
        setPosition(startX,startY);
    }

    public void setPosition(int x, int y) {
        if (x <= getMaxX() && x >= getMinX())
            setX(x);
        if (y <= getMaxY() && y >= getMinY())
            setY(y);
    }

    @Override
    public void update() {
        if(!isAvailable)
        {
            setX(getX() + getSpeed());
            if (getX() >= getMaxX()) {
                setX(getMaxX());
                setAvailable(true);
            } else
            {
                setAvailable(false);
            }

            //update location of the rectangle collision hitbox with some margin
            getHitbox().left = getX();
            getHitbox().right = getX() + getBitmap().getWidth() - getBitmap().getWidth() / 5;
            getHitbox().top = getY();
            getHitbox().bottom = getY() + getBitmap().getHeight() - getBitmap().getHeight() / 5;
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
