package com.hafezi.games.spaceshooter2d.Utility;

/**
 * Created by Mojtaba Hafezi on 23.02.2018.
 */

//When using the shared preferences the different strings could become error prone
public enum Pref {
    AUDIO("AUDIO"),
    GAME("GAME"),
    TIME("TIME"),
    SENSOR("SENSOR"),
    SCORE("SCORE");

    // required to give a string back for the enums
    private final String text;

    Pref(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}