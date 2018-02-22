package com.hafezi.games.spaceshooter2d;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ActionMenuView;

import com.hafezi.games.spaceshooter2d.GameObjects.Dust;
import com.hafezi.games.spaceshooter2d.GameObjects.Enemy;
import com.hafezi.games.spaceshooter2d.GameObjects.Explosion;
import com.hafezi.games.spaceshooter2d.GameObjects.Player;
import com.hafezi.games.spaceshooter2d.Utility.InputController;

import java.util.ArrayList;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Mojtaba Hafezi on 18.02.2018.
 */

//View for the main game since everything needs to be drawn on screen
public class GameView extends SurfaceView implements Runnable, SensorEventListener {

    //Thread related attributes
    volatile boolean playing;
    Thread gameThread = null;


    //Game objects
    private Player player;
    private Explosion[] explosions;
    private Explosion explosion1;
    private Explosion explosion2;
    private Explosion explosion3;
    private Explosion explosion4;
    private Explosion explosion5;
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
    long startFrameTime;
    long timeThisFrame;
    long lastHit;
    long timeForExplosion;
    private boolean playerGotHit;
    //measures time since game loop is running + tracks record
    private long fastestTime = 999999;
    private long timeTaken;
    private long timeStarted;

    //utility
    private SoundManager soundManager;
    private InputController inputController;
    private Vibrator vibrator;
    private long[] vibratorPattern = {300, 100, 300, 100, 600, 200, 1000};
    private SensorManager sensorManager;
    private Sensor sensor;

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
        vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        inputController = new InputController(screenX, screenY);
    }

    public void initialiseGame() {
        setGameOver(false);
        setPlaying(true);
        lastHit = 0;
        timeTaken = 0;
        timeStarted = System.currentTimeMillis();
        //Load fastest time;
        //fastestTime = Load...
        player = new Player(getContext(), 10, 0, 10, getScreenX(), getScreenY());
        setPlayerGotHit(false);
        explosions = new Explosion[5];
        for (int i = 0; i < explosions.length; i++) {
            explosions[i] = new Explosion(getContext(), screenX, screenY, "explosion" + (1 + i), 0, 0);
        }
        timeForExplosion = 0;
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
            startFrameTime = System.currentTimeMillis();

            update();
            draw();
            //control the frames per seconds -> if drawing took too long skip sleep call for thread
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            control();
        }
    }

    private void update() {
        if (!isGameOver()) {

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

            //check for collisions
            boolean collisionDetected;
            for (Enemy enemy : enemies) {
                collisionDetected = collisionDetection(player, enemy);
                if (collisionDetected) {
                    if (player.getShields() >= 1) {
                        soundManager.playSound(SoundManager.Sounds.HIT);
                        vibrator.vibrate(200);
                        //player is immune for 2 sec after a collision but only once
                        if (lastHit != 0 && startFrameTime - lastHit > 1000)
                            player.setShields(player.getShields() - 1);
                        if (lastHit == 0)
                            lastHit = System.currentTimeMillis();
                    }
                }
            }

            timeTaken =  System.currentTimeMillis() - timeStarted; //add the time

            //check for game status
            if (player.getShields() <= 0) {
                //play destroyed sound
                soundManager.playSound(SoundManager.Sounds.EXPLOSION);
                vibrator.vibrate(vibratorPattern, -1);
                setGameOver(true); //gameover
                if (timeTaken < fastestTime) {
                    fastestTime = timeTaken; //new hi-score
                }
            }


        } else {
            if (timeForExplosion == 0)
                timeForExplosion = System.currentTimeMillis();
            //start new activity, with score as parameter
            /*
            Intent i = new Intent(getContext(), MainActivity.class);
            getContext().startActivity(i);
            Activity activity = (Activity) getContext();
            activity.finish();
            */

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
                for (Dust redDust : redDusts) {
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
                //unlock and post at the end
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
            //Draw game over text, score and show ship explosion
            else {
                //Lock & repaint canvas
                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);

                //Draw game objects with corresponding paint color
                paint.setColor(Color.YELLOW);
                for (Dust yellowDust : yellowDusts) {
                    canvas.drawPoint(yellowDust.getX(), yellowDust.getY(), paint);
                }
                paint.setColor(Color.RED);
                for (Dust redDust : redDusts) {
                    canvas.drawPoint(redDust.getX(), redDust.getY(), paint);
                }
                paint.setColor(Color.WHITE);
                for (Dust whiteDust : whiteDusts) {
                    canvas.drawPoint(whiteDust.getX(), whiteDust.getY(), paint);
                }

                //Draw explosion where player was before
                long animExplosion = startFrameTime - timeForExplosion;
                int result = -1;
                if (animExplosion <= 300)
                    result = 0;
                else if (animExplosion <= 600)
                    result = 1;
                else if (animExplosion <= 900)
                    result = 2;
                else if (animExplosion <= 1200)
                    result = 3;
                else if (animExplosion <= 1500)
                    result = 4;
                if (result > 0 && result <= 4)
                    canvas.drawBitmap(explosions[result].getBitmap(), player.getX(), player.getY(), paint);

                //enemy objects
                for (Enemy enemy : enemies) {
                    canvas.drawBitmap(enemy.getBitmap(), enemy.getX(), enemy.getY(), paint);
                }
                //unlock and post at the end
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    //for constant fps
    private void control() {
        try {
            //took too long for the operations
            if (timeThisFrame >= 17) {
                return;
            } else
                //control frame rate (1000/60 = ca. 17) - subtract the time taken for update/draw
                gameThread.sleep(17 - timeThisFrame);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //checks for intersection between the hitboxes - used in the update method
    private boolean collisionDetection(Player player, Enemy enemy) {
        if (Rect.intersects(player.getHitbox(), enemy.getHitbox())) {
            enemy.setRandomAttributes();
            return true;
        }
        return false;
    }


    public void pause() {
        setPlaying(false);
        sensorManager.unregisterListener(this);
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            //Todo: error handling
            e.printStackTrace();
        }
    }

    public void resume() {
        if (sensorManager != null)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        for (Enemy enemy : enemies)
            enemy.setRandomAttributes();
        setPlaying(true);
        gameThread = new Thread(this);
        gameThread.start();
    }

    //InputController manages events
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

    public boolean isPlayerGotHit() {
        return playerGotHit;
    }

    public void setPlayerGotHit(boolean playerGotHit) {
        this.playerGotHit = playerGotHit;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        inputController.handleSensorInput(sensorEvent, player);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
