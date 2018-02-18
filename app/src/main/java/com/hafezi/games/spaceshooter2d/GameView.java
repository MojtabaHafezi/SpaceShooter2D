package com.hafezi.games.spaceshooter2d;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Mojtaba Hafezi on 18.02.2018.
 */

//View for the main game since everything needs to be drawn on screen
public class GameView extends SurfaceView implements Runnable {

    volatile  boolean playing;
    Thread gameThread = null;

    private SoundManager soundManager;


    //Attributes req. for drawing
    private Canvas canvas;
    private Paint paint;
    private SurfaceHolder surfaceHolder;
    private int screenX;
    private int screenY;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        resume();
        paint = new Paint();
        surfaceHolder = getHolder();
        soundManager =  SoundManager.getInstance(context);

        setScreenX(screenX);
        setScreenY(screenY);
        /*
        setContext(context);
        //Sound
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            //loading the ogg files into the corresponding attributes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor assetFileDescriptor;

            assetFileDescriptor = assetManager.openFd("start.ogg");
            start = soundPool.load(assetFileDescriptor, 0);
            assetFileDescriptor = assetManager.openFd("win.ogg");
            win = soundPool.load(assetFileDescriptor, 0);
            assetFileDescriptor = assetManager.openFd("bump.ogg");
            bump = soundPool.load(assetFileDescriptor, 0);
            assetFileDescriptor = assetManager.openFd("destroyed.ogg");
            destroyed = soundPool.load(assetFileDescriptor, 0);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("sound error", "Error while loading sound files");
        }
        startGame();
        */
    }

    @Override
    public void run() {
        while(isPlaying())
        {
            control();
            draw();
        }
    }

    private void draw()
    {
        //only draw if valid
        if (surfaceHolder.getSurface().isValid()) {

            //Lock & repaint canvas
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);


            //unlock and post at the end
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    //for fps
    private void control()
    {
        try {
            //control frame rate (1000/60 = ca. 17)
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause()
    {
        setPlaying(false);
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            //Todo: error handling
            e.printStackTrace();
        }
    }

    public void resume()
    {
        setPlaying(true);
        gameThread = new Thread(this);
        gameThread.start();
    }

    //GETTER AND SETTERS
    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public int getScreenX() {
        return screenX;
    }

    public void setScreenX(int screenX) {
        this.screenX = screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    public void setScreenY(int screenY) {
        this.screenY = screenY;
    }
}
