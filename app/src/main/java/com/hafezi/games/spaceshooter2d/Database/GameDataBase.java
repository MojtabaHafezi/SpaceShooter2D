package com.hafezi.games.spaceshooter2d.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.SyncStateContract;
import android.util.Log;

/**
 * Created by Mojtaba Hafezi on 28.02.2018.
 */

public class GameDataBase {
    private SQLiteDatabase db;
    private final Context context;
    private final MyDBhelper dbhelper;

    public GameDataBase(Context context) {
        this.context = context;
        dbhelper = new MyDBhelper(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        openWritable();
    }

    public void close() {
        db.close();
    }

    public void openWritable() throws SQLiteException {
        try {
            db = dbhelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            Log.e("DB", ex.getMessage());
            db = dbhelper.getReadableDatabase();
        }
    }

    public void openReadable() {
        db = dbhelper.getReadableDatabase();

    }

    public long insertScore(int score, int ships) {
        try {
            ContentValues newTaskValue = new ContentValues();
            newTaskValue.put(Constants.SCORE, score);
            newTaskValue.put(Constants.SHIPS,  ships);

            return db.insert(Constants.TABLE_NAME, null, newTaskValue);
        } catch (SQLiteException ex) {
            Log.e("DBInsert",
                    ex.getMessage() + "Insert into database exception caught");
            return -1;
        }
    }

    public Cursor getScores() {
        Cursor c = db.query(Constants.TABLE_NAME, null, null,
                null, null, null, Constants.SCORE + " DESC");
        return c;
    }
}
