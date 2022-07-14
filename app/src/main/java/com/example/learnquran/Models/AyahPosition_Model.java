package com.example.learnquran.Models;

public class AyahPosition_Model {
    int AyahPositionFromDatabase;

    public AyahPosition_Model(int AyahPositionFromDatabase) {
        this.AyahPositionFromDatabase = AyahPositionFromDatabase;
    }

    public int getAyahPositionFromDatabase() {
        return AyahPositionFromDatabase;
    }

    public void setAyahPositionFromDatabase(int ayahPositionFromDatabase) {
        this.AyahPositionFromDatabase = ayahPositionFromDatabase;
    }
}
