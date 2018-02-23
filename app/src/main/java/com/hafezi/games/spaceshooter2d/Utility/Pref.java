package com.hafezi.games.spaceshooter2d.Utility;

/**
 * Created by Mojtaba Hafezi on 23.02.2018.
 */

public enum Pref {
    AUDIO("AUDIO"),
    GAME("GAME"),
    TIME("TIME"),
    SENSOR("SENSOR");

    private final String text;

    /**
     * @param text
     */
    Pref(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}