package com.hafezi.games.spaceshooter2d;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hafezi.games.spaceshooter2d.GameObjects.Dust;
import com.hafezi.games.spaceshooter2d.GameObjects.Enemy;
import com.hafezi.games.spaceshooter2d.GameObjects.Explosion;
import com.hafezi.games.spaceshooter2d.GameObjects.Laser;
import com.hafezi.games.spaceshooter2d.GameObjects.Player;
import com.hafezi.games.spaceshooter2d.Utility.InputController;
import com.hafezi.games.spaceshooter2d.Utility.Pref;

import java.util.ArrayList;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Mojtaba Hafezi on 18.02.2018.
 */

//View for the main game since everything needs to be drawn on screen
public class GameView extends SurfaceView implements Runnable, SensorEventListener, InputManager.InputDeviceListener {

    //Thread related attributes
    volatile boolean playing;
    Thread gameThread = null;


    //Game objects
    private Player player;
    private Explosion[] explosions;
    private Explosion quickExplosion;
    private boolean isExplosionTriggered;
    private Enemy[] enemies;
    private ArrayList<Dust> whiteDusts;
    private ArrayList<Dust> yellowDusts;
    private ArrayList<Dust> redDusts;
    private final int WHITEDUST = 75;
    private final int YELLOWDUST = 45;
    private final int REDDUST = 30;
    private long enemiesDestroyed;
    private long score;

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
    //measures time since game loop is running + tracks record
    private long longestTime = 0;
    private long timeTaken;
    private long timeStarted;

    //utility
    private SoundManager soundManager;
    private InputController inputController;
    private Vibrator vibrator;
    private long[] vibratorPattern = {300, 100, 300, 100, 600, 100, 1000, 100, 1000};
    private boolean useSensor; //needs to be changed in option, saved and loaded properly
    private SensorManager sensorManager;
    private Sensor sensor;
    private InputManager inputManager;
    //persistence
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        inputManager = (InputManager) getContext().getSystemService(Context.INPUT_SERVICE);
        setContext(context);
        setScreenX(screenX);
        setScreenY(screenY);
        paint = new Paint();
        surfaceHolder = getHolder();
        sharedPreferences = getContext().getSharedPreferences(Pref.GAME.toString(), context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        soundManager = SoundManager.getInstance(context);
        vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        inputController = new InputController(this, screenX, screenY);

        initialiseGame();
        resume();
    }

    public void initialiseGame() {
        setGameOver(false);
        setPlaying(true);
        lastHit = 0;
        timeTaken = 0;
        timeStarted = SystemClock.elapsedRealtime();
        //Load score and options for sensor
        longestTime = sharedPreferences.getLong(Pref.TIME.toString(), 0);
        score = sharedPreferences.getLong(Pref.SCORE.toString(), 0);
        enemiesDestroyed = 0;
        boolean usingSensor = sharedPreferences.getBoolean(Pref.SENSOR.toString(), false);
        setUseSensor(usingSensor);

        player = new Player(getContext(), 10, 0, 10, getScreenX(), getScreenY());
        explosions = new Explosion[5];
        for (int i = 0; i < explosions.length; i++) {
            explosions[i] = new Explosion(getContext(), screenX, screenY, "explosion" + (1 + i), 0, 0);
        }
        quickExplosion = new Explosion(getContext(), screenX, screenY, "quickexplosion", -1000, -1000);
        isExplosionTriggered = false;
        timeForExplosion = 0;
        enemies = new Enemy[6];
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
            startFrameTime = SystemClock.elapsedRealtime();
            update();
            draw();
            //control the frames per seconds -> if drawing took too long skip sleep call for thread
            timeThisFrame = SystemClock.elapsedRealtime() - startFrameTime;
            control();
        }
    }

    private void update() {
        if (!isGameOver()) {
            //if game was paused -> time handled correctly through pause method
            if (timeStarted != 0)
                timeTaken += (SystemClock.elapsedRealtime() - timeStarted);
            timeStarted = SystemClock.elapsedRealtime();

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

            //check for collisions between player and enemies
            boolean collisionDetected;
            for (Enemy enemy : enemies) {
                collisionDetected = collisionDetection(player, enemy);
                if (collisionDetected) {
                    enemiesDestroyed++;
                    isExplosionTriggered = true;
                    if (player.getShields() >= 1) {
                        soundManager.playSound(SoundManager.Sounds.HIT);
                        vibrator.vibrate(200);
                        //player is immune for 2 sec after a collision but only once
                        if (lastHit == 0) {
                            lastHit = SystemClock.elapsedRealtime();
                            player.setShields(player.getShields() - 1);
                        }
                        if (startFrameTime - lastHit > 2000)
                            player.setShields(player.getShields() - 1);

                    }
                } else {
                    isExplosionTriggered = false;
                }
                //if laser hits enemy
                if (!player.getLaser().isAvailable()) {
                    collisionDetected = collisionWithLaser(player, enemy);
                    if (collisionDetected) {
                        player.getLaser().setAvailable(true);
                        enemy.setShield(enemy.getShield() - 1);
                        if (enemy.getShield() <= 0) {
                            enemiesDestroyed++;
                            quickExplosion.setPosition(enemy.getX() - 5, enemy.getY() + enemy.getHeight() / 2);
                            enemy.setRandomAttributes();
                        }
                        isExplosionTriggered = true;
                        soundManager.playSound(SoundManager.Sounds.HIT);
                    } else {
                        isExplosionTriggered = false;
                    }
                }
            }

            //check for game status
            if (player.getShields() <= 0) {
                //play destroyed sound
                soundManager.playSound(SoundManager.Sounds.EXPLOSION);
                vibrator.vibrate(vibratorPattern, -1);
                setGameOver(true); //gameover
                if (timeTaken > longestTime) {
                    longestTime = timeTaken; //new hi-score
                    editor.putLong(Pref.TIME.toString(), longestTime);

                }
                if (enemiesDestroyed > score) {
                    editor.putLong(Pref.SCORE.toString(), enemiesDestroyed);
                }
                editor.commit();
            }


        } else {
            if (timeForExplosion == 0)
                timeForExplosion = SystemClock.elapsedRealtime();
            //if player taps on screen again -> event triggers call to new activity
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
                if (!player.getLaser().isAvailable()) {
                    Laser laser = player.getLaser();
                    canvas.drawBitmap(laser.getBitmap(), laser.getX(), laser.getY(), paint);
                }
                //enemy objects
                for (Enemy enemy : enemies) {
                    canvas.drawBitmap(enemy.getBitmap(), enemy.getX(), enemy.getY(), paint);
                }
                // draw explosion
                if (isExplosionTriggered)
                    canvas.drawBitmap(quickExplosion.getBitmap(), quickExplosion.getX(), quickExplosion.getY(), paint);

                //HUD
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.CYAN);
                paint.setTextSize(30);
                canvas.drawText("Longest: " + (int) longestTime / 1000 + " s", 10, 20, paint);
                canvas.drawText("Time: " + (int) timeTaken / 1000 + " s", getScreenX() / 2, 20, paint);
                canvas.drawText("Shields: " + player.getShields(), 10, getScreenY() - 20, paint);
                canvas.drawText("Destroyed: " + enemiesDestroyed, getScreenX() / 2, getScreenY() - 20, paint);


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

                //GAMEOVER SCREEN

                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.CYAN);
                canvas.drawText("GAME OVER", getScreenX() / 2, 100, paint);
                paint.setTextSize(25);
                canvas.drawText("Longest: " + (int) longestTime / 1000 + " s", getScreenX() / 2, 160, paint);
                canvas.drawText("Time: " + (int) timeTaken / 1000 + " s", getScreenX() / 2, 200, paint);
                canvas.drawText("Ships destroyed: " + enemiesDestroyed, getScreenX() / 2, 240, paint);

                paint.setTextSize(80);
                canvas.drawText("Tap to continue!", getScreenX() / 2, getScreenY() / 2, paint);


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
            quickExplosion.setPosition(player.getX() + 10, player.getY() + player.getHeight() / 2);
            enemy.setRandomAttributes();
            return true;
        }
        return false;
    }

    private boolean collisionWithLaser(Player player, Enemy enemy) {
        if (Rect.intersects(player.getLaser().getHitbox(), enemy.getHitbox())) {
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
        timeStarted = SystemClock.elapsedRealtime();
        setPlaying(true);
        gameThread = new Thread(this);
        gameThread.start();
    }

    //InputController manages events
    @Override
    public boolean onTouchEvent(MotionEvent event) {

            if (player != null) {
                inputController.handleTouchInput(event, player);
            }

        return true;
    }

    //transition to high-score activity and passes the parameters for time and score
    public void startNewActivity() {
        Activity activity = (Activity) getContext();
        Intent i = new Intent(getContext(), HighScoreActivity.class);
        i.putExtra(Pref.TIME.toString(), (int) (timeTaken / 1000));
        i.putExtra(Pref.SCORE.toString(), (int) enemiesDestroyed);
        activity.finish();
        activity.startActivity(i);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (isUseSensor())
            inputController.handleSensorInput(sensorEvent, player);
    }

    //GAMEPAD handling
    public void handleControllerMotion(MotionEvent event) {
        inputController.handleControllerMotionInput(event, player);
    }

    public void handleControllerKeys(KeyEvent event) {
        inputController.handleControllerKeysInput(event, player);
    }


    //Empty - required for Accelerometer
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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


    public boolean isUseSensor() {
        return useSensor;
    }

    public void setUseSensor(boolean useSensor) {
        this.useSensor = useSensor;
    }

    //for gamepad changes ingame
    @Override
    public void onInputDeviceAdded(int i) {

    }

    @Override
    public void onInputDeviceRemoved(int i) {

    }

    @Override
    public void onInputDeviceChanged(int i) {

    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}
