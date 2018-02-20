package com.hafezi.games.spaceshooter2d.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Mojtaba Hafezi on 19.02.2018.
 */
//Basic class for all game objects
public abstract class GameObject {

    private int width;
    private int height;
    private Bitmap bitmap;
    private Context context;

    private int x, y;
    private int minY, maxY;
    private int minX, maxX;

    Random random;
    //collision box
    private Rect hitbox;
    private int speed;

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

    public void prepareBitmap(String bitmapName) {
        int resourceId = getContext().getResources().getIdentifier(bitmapName, "drawable", getContext().getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), resourceId);
        this.bitmap = bitmap;
    }
}
