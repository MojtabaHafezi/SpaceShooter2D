package com.hafezi.games.spaceshooter2d.GameObjects;

import java.util.Random;

/**
 * Created by Mojtaba Hafezi on 21.02.2018.
 */

public class Dust extends GameObject {
    private Random random;


    public Dust(int screenX, int screenY)
    {
        random = new Random();
        setScreenX(screenX);
        setScreenY(screenY);
        setMinX(0);
        setMinY(0);
        setMaxX(getScreenX());
        setMaxY(getScreenY());
        //position the dots randomly
        setRandomAttributes();



    }
    @Override
    public void update() {
        setX(getX() - getSpeed());

        if(getX() <= getMinX())
        {
           setRandomAttributes();
        }
    }

    private void setRandomAttributes()
    {
        setX(random.nextInt(getMaxX()));
        setY(random.nextInt(getMaxY()));
        setSpeed(3+ random.nextInt(9));
    }
}
