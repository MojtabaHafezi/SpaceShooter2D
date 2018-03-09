package com.hafezi.games.spaceshooter2d;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.hafezi.games.spaceshooter2d.Utility.Pref;

import java.io.IOException;

/**
 * Created by Mojtaba Hafezi on 18.02.2018.
 */

/***************************************************************************************
 *    @Citation
 *    Title: Mastering Android Game Development
 *    Author: Portales Raul
 *    Date: 2015
 *    Code version: 1.0
 *    Change of original code: Major
 *    Original idea for: Sound pool and sound loading
 ***************************************************************************************/

// The SoundManager needs to be accessible from all other activities -> singleton design pattern
public class SoundManager {
    //the instance every other class will access
    private static SoundManager instance;
    private Context context;
    //classes required for sound and music
    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;
    //keep track where mediaplayer stopped to continue whenever the game is paused
    private int length;
    private boolean mute;

    // ids for the sound effects - will be loaded upon instantiation of the class
    int menu = -1;
    int explosion = -1;
    int hit = -1;
    int laser = -1;

    //persistence - gets the mute value
    private SharedPreferences sharedPreferences;

    //enum for the sound effects
    public enum Sounds {
        MENU, EXPLOSION, HIT, LASER
    }

    private SoundManager(Context context) {
        this.context = context;
        // If id doesn't exist one is created
        sharedPreferences = context.getSharedPreferences(Pref.GAME.toString(), context.MODE_PRIVATE);
        boolean toMute = sharedPreferences.getBoolean(Pref.AUDIO.toString(), false);
        setMute(toMute);
        //loads all sound effects so it can play them whenever required
        loadSound(context);
    }

    //returns the actual instance - only one instance of this class will be available
    public static SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    private void loadSound(Context context) {
        //Sound
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            //Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            //create the sounds
            descriptor = assetManager.openFd("explosion.ogg");
            explosion = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("hit.ogg");
            hit = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("menu.ogg");
            menu = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("laser.ogg");
            laser = soundPool.load(descriptor, 0);
        } catch (IOException e) {
            Log.e("error", "failed to load sound files");
        }
        //Media
        mediaPlayer = MediaPlayer.create(context, R.raw.ambient);
    }

    //if sound is enabled then the sound effect is played once
    public void playSound(Sounds sound) {
        if (isMute())
            return;
        switch (sound) {
            case HIT:
                soundPool.play(hit, 1, 1, 0, 0, 1);
                break;
            case EXPLOSION:
                soundPool.play(explosion, 1, 1, 0, 0, 1);
                break;
            case MENU:
                soundPool.play(menu, 1, 1, 0, 0, 1);
                break;
            case LASER:
                soundPool.play(laser, 1, 1, 0, 0, 1);
                break;
        }

    }

    //if sound is enabled the music will be played in a loop
    public void playMusic() {
        if (isMute())
            return;
        //use of media player is recommended by Google instead of sound pool for ambient music
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            mediaPlayer = MediaPlayer.create(this.context, R.raw.ambient);
            mediaPlayer.setLooping(true);
            //if the music was stopped before - continue
            if (length > 0) {
                mediaPlayer.seekTo(length);
            }
            mediaPlayer.start();
        }
    }
    //should the music be paused then the current position will be stored
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            length = mediaPlayer.getCurrentPosition();
        } else
            length = 0;
    }
    //the media player needs to be released once the application is about to exit
    public void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            length = 0;
        }

    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }
}
