package com.hafezi.games.spaceshooter2d;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hafezi.games.spaceshooter2d.Database.Constants;
import com.hafezi.games.spaceshooter2d.Database.GameDataBase;
import com.hafezi.games.spaceshooter2d.Database.MyDBhelper;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {

    private Button exitButton;
    private Button playButton;
    private LinearLayout scoreColumn;
    private LinearLayout shipsColumn;
    private SoundManager soundManager;
    private ArrayList<TextView> scoreList;
    private ArrayList<TextView> shipList;

    //Database
    private GameDataBase gameDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        gameDataBase = new GameDataBase(this);

        scoreList = new ArrayList<>();
        shipList = new ArrayList<>();
        //saveToDB(10,10);
       // saveToDB(20,10);
        loadDataFromDB();

        soundManager = SoundManager.getInstance(this);
        soundManager.playMusic();
        scoreColumn = (LinearLayout) findViewById(R.id.scoreColumn);
        shipsColumn = (LinearLayout) findViewById(R.id.shipsColumn);
        exitButton = (Button) findViewById(R.id.hsBackButton);
        playButton = (Button) findViewById(R.id.hsPlayButton);

        setColumns();
        setButtonListener();
    }

    private void setButtonListener() {
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitButton.setBackgroundResource(R.drawable.yellow_button);
                soundManager.playSound(SoundManager.Sounds.MENU);
                Intent i = new Intent(HighScoreActivity.this, MainActivity.class);
                finish();
                startActivity(i);

            }
        });

        exitButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    exitButton.setBackgroundResource(R.drawable.red_button);
                else
                    exitButton.setBackgroundResource(R.drawable.blue_button);
            }
        });


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButton.setBackgroundResource(R.drawable.yellow_button);
                soundManager.playSound(SoundManager.Sounds.MENU);
                Intent i = new Intent(HighScoreActivity.this, GameActivity.class);
                finish();
                startActivity(i);

            }
        });

        playButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    playButton.setBackgroundResource(R.drawable.red_button);
                else
                    playButton.setBackgroundResource(R.drawable.blue_button);
            }
        });
    }

    private void saveToDB(int score, int ships) {
        gameDataBase.openWritable();
        gameDataBase.insertScore(score, ships);
        gameDataBase.close();
    }

    private void setColumns() {
        loadDataFromDB();
        for(TextView tv : scoreList)
        {
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(25);
            scoreColumn.addView(tv);
        }
        for(TextView tv : shipList)
        {
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(25);
            shipsColumn.addView(tv);
        }
    }

    public void loadDataFromDB() {
        gameDataBase.openReadable();
        Cursor c = gameDataBase.getScores();
        //startManagingCursor(c);
        if (c.moveToFirst()) {
            do {
                Long id = c.getLong(c.getColumnIndex(Constants.KEY_ID));
                String score = "Time: " + c.getInt(c.getColumnIndex(Constants.SCORE));
                String ships = "Destroyed: " + c.getInt(c.getColumnIndex(Constants.SHIPS));
                TextView tv = new TextView(this);
                tv.setText("ID: " + id + " " +  score);
                TextView tv2 = new TextView(this);
                tv2.setText(ships);
                scoreList.add(tv);
                shipList.add(tv2);

            } while (c.moveToNext());
        }
        gameDataBase.close();

    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.playMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundManager.stopMusic();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }
}
