package com.hafezi.games.spaceshooter2d;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Mojtaba Hafezi on 18.02.2018.
 */
//using the Singleton pattern
public class SoundManager {
    private static SoundManager instance;
    private SoundPool soundPool;
    private MediaPlayer mediaPlayer ;
    private boolean mute;
    int menu = -1;
    int explosion = -1;
    int hit = -1;

    private SoundManager(Context context) {
        loadSound(context);
    }

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

        } catch (IOException e) {
            Log.e("error", "failed to load sound files");
        }
        //Media
        mediaPlayer = MediaPlayer.create(context, R.raw.ambient);

    }


    public void playSound(String sound) {
        if (!isMute()) {
            switch (sound) {
                case "hit":
                    soundPool.play(hit, 1, 1, 0, 0, 1);
                    break;
                case "explosion":
                    soundPool.play(explosion, 1, 1, 0, 0, 1);
                    break;
                case "menu":
                    soundPool.play(menu, 1, 1, 0, 0, 1);
                    break;
            }
        }
    }

    public void playMusic()
    {
        if(!isMute())
        {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

        }
    }

    public void releasePlayer()
    {
        mediaPlayer.release();
        mediaPlayer = null;
    }
    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }
}
