package com.hafezi.games.spaceshooter2d;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hafezi.games.spaceshooter2d.GameObjects.Dust;
import com.hafezi.games.spaceshooter2d.GameObjects.Enemy;
import com.hafezi.games.spaceshooter2d.GameObjects.Player;
import com.hafezi.games.spaceshooter2d.Utility.InputController;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mojtaba Hafezi on 18.02.2018.
 */

//View for the main game since everything needs to be drawn on screen
public class GameView extends SurfaceView implements Runnable {

    //Thread related attributes
    volatile boolean playing;
    Thread gameThread = null;


    //Game objects
    private Player player;
    private Enemy[] enemies;
    private ArrayList<Dust> whiteDusts;
    private ArrayList<Dust> yellowDusts;
    private ArrayList<Dust> redDusts;
    private final int WHITEDUST = 75;
    private final int YELLOWDUST = 45;
    private final int REDDUST = 30;

    //Attributes req. for drawing
    private Canvas canvas;
    private Paint paint;
    private SurfaceHolder surfaceHolder;
    private Context context;
    private int screenX;
    private int screenY;

    //Game loop relevant attributes
    private boolean gameOver;

    //utility
    private SoundManager soundManager;
    private InputController inputController;


    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        setContext(context);
        setScreenX(screenX);
        setScreenY(screenY);
        initialiseGame();
        resume();
        paint = new Paint();
        surfaceHolder = getHolder();

        soundManager = SoundManager.getInstance(context);
        inputController = new InputController(screenX, screenY);

    }

    public void initialiseGame() {
        setGameOver(false);
        setPlaying(true);
        player = new Player(getContext(), 10, 0, 10, getScreenX(), getScreenY());
        enemies = new Enemy[3];
        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = new Enemy(getContext(), getScreenX(), getScreenY());
        }
        whiteDusts = new ArrayList<>();
        yellowDusts = new ArrayList<>();
        redDusts = new ArrayList<>();
        for (int i = 0; i < WHITEDUST; i++) {
            Dust whiteDust = new Dust(getScreenX(), getScreenY());
            whiteDusts.add(whiteDust);
        }
        for (int i = 0; i < YELLOWDUST; i++) {
            Dust yellowDust = new Dust(getScreenX(), getScreenY());
            yellowDusts.add(yellowDust);
        }
        for (int i = 0; i < REDDUST; i++) {
            Dust redDust = new Dust(getScreenX(), getScreenY());
            redDusts.add(redDust);
        }

    }

    @Override
    public void run() {
        while (isPlaying()) {
            update();
            control();
            draw();
        }
    }

    private void update() {
        if (!isGameOver()) {
            //check for collisions


            //update game objects
            player.update();
            for (Enemy enemy : enemies) {
                enemy.update();
            }
            for (Dust whiteDust : whiteDusts) {
                whiteDust.update();
            }
            for (Dust yellowDust : yellowDusts) {
                yellowDust.update();
            }
            for (Dust redDust : redDusts) {
                redDust.update();
            }


            //check for game status
        }
    }

    private void draw() {
        //only draw if valid
        if (surfaceHolder.getSurface().isValid()) {
            if (!isGameOver()) {
                //Lock & repaint canvas
                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);


                //Draw game objects with corresponding paint color
                paint.setColor(Color.YELLOW);
                for (Dust yellowDust : yellowDusts) {
                    canvas.drawPoint(yellowDust.getX(), yellowDust.getY(), paint);
                }
                paint.setColor(Color.RED);
                for(Dust redDust : redDusts)
                {
                    canvas.drawPoint(redDust.getX(), redDust.getY(), paint);
                }
                paint.setColor(Color.WHITE);
                for (Dust whiteDust : whiteDusts) {
                    canvas.drawPoint(whiteDust.getX(), whiteDust.getY(), paint);
                }
                // player
                canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);
                //enemy objects
                for (Enemy enemy : enemies) {
                    canvas.drawBitmap(enemy.getBitmap(), enemy.getX(), enemy.getY(), paint);
                }
            }

            //unlock and post at the end
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    //for fps
    private void control() {
        try {
            //control frame rate (1000/60 = ca. 17)
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        setPlaying(false);
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            //Todo: error handling
            e.printStackTrace();
        }
    }

    public void resume() {
        setPlaying(true);
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (player != null) {
            inputController.handleInput(event, player);
        }
        return true;
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

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
