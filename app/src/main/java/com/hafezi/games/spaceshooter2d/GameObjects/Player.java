package com.hafezi.games.spaceshooter2d.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Mojtaba Hafezi on 19.02.2018.
 */

public class Player extends GameObject {

    private int shields;
    private boolean moveUp;
    private boolean moveDown;

    public Player(Context context, int startX, int startY, int speed, int screenX, int screenY) {
        setContext(context);
        setScreenX(screenX);
        setScreenY(screenY);
        setX(startX);
        setY(startY);
        setSpeed(speed);
        setShields(2);
        prepareBitmap("player");
        setMinY(0);
        setMinX(0);
        setMaxX(screenX - getBitmap().getWidth());
        setMaxY(screenY - getBitmap().getHeight());
        setWidth(getBitmap().getWidth());
        setHeight(getBitmap().getHeight());
        setHitbox(new Rect(getX(), getY(), getWidth(), getHeight()));

    }

    @Override
    public void update() {
        Log.e("HMM", getY() + "");
        Log.e("MoveDown", isMoveDown() + "");
        Log.e("MoveUp", isMoveUp() + "");
        Log.e("Y POS:", getY() + "");
        if(isMoveDown())
        {
            setY(getY()+ getSpeed());
        }
        if(isMoveUp())
        {
            setY(getY() - getSpeed());
        }

        if(getY() <= getMinY())
        {
           setY(getMinY());
        }
        if(getY() >= getMaxY())
        {
            setY(getMaxY());
        }

        //update location of the rectangle collision hitbox with some margin
        getHitbox().left = getX();
        getHitbox().right = getX() + getBitmap().getWidth() - getBitmap().getWidth()/6;
        getHitbox().top = getY();
        getHitbox().bottom = getY() + getBitmap().getHeight() - getBitmap().getHeight()/6;

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
}
