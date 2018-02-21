package com.hafezi.games.spaceshooter2d.GameObjects;

import android.content.Context;

/**
 * Created by Mojtaba Hafezi on 21.02.2018.
 */

public class Explosion extends GameObject {


    public Explosion(Context context, int screenX, int screenY, String name, int x, int y)
    {
        setContext(context);
        setScreenX(screenX);
        setScreenY(screenY);
        setX(x);
        setY(y);
        prepareBitmap(name);
        setWidth(getBitmap().getWidth());
        setHeight(getBitmap().getHeight());

    }

    @Override
    public void update() {

    }

}
