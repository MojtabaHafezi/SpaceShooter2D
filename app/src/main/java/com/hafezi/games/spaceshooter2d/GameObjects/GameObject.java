package com.hafezi.games.spaceshooter2d.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Debug;
import android.util.Log;

import java.util.Random;

/**
 * Created by Mojtaba Hafezi on 19.02.2018.
 */
//Basic class for all game objects
public abstract class GameObject {

    //the sprite's width and height
    private int width;
    private int height;
    // the sprite
    private Bitmap bitmap;
    private Context context;

    //current position
    private int x, y;
    //limits on screen
    private int minY, maxY;
    private int minX, maxX;
    private int screenX, screenY;

    Random random;
    //collision box
    private Rect hitbox;
    private int speed;
    //all objects should have this method
    public abstract void update();

    //GETTERS AND SETTERS

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rect hitbox) {
        this.hitbox = hitbox;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    //prepares the bitmap by getting the identifier from the resources
    public void prepareBitmap(String bitmapName) {
        int resourceId = getContext().getResources().getIdentifier(bitmapName, "drawable", getContext().getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), resourceId);
        this.bitmap = bitmap;
        //scaleBitmap();
    }

    public int getScreenY() {
        return screenY;
    }

    public void setScreenY(int screenY) {
        this.screenY = screenY;
    }

    public int getScreenX() {
        return screenX;
    }

    public void setScreenX(int screenX) {
        this.screenX = screenX;
    }

    //this method could have helped balancing the game play on different screen resolutions
    //content scaling depending on different resolutions. Pixelated outcome is bad but acceptable
    private void scaleBitmap() {
        int divider = 100;

        //picture has aspect ratio 4:3
        int standardWidth = 1600;
        int standardHeight = 1200;
        int optimalWidth = standardWidth / divider;
        int optimalHeight = standardHeight / divider;

        int currentWidth = getScreenX() / divider;
        int currentHeight = getScreenY() / divider;

        float widthMultiplier =  ( (float) currentWidth / (float) optimalWidth);
        float heightMultiplier =  ( (float) currentHeight / (float) optimalHeight);

        int desiredWidth = (int) (getBitmap().getWidth() * widthMultiplier);
        int desiredHeight = (int) (getBitmap().getHeight() * heightMultiplier);

        Bitmap scaledBitmap = getBitmap();
        scaledBitmap = Bitmap.createScaledBitmap(scaledBitmap,
                desiredWidth, desiredHeight, false);
        this.bitmap = scaledBitmap;
    }

}
