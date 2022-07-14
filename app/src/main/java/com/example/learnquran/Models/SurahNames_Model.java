package com.example.learnquran.Models;

public class SurahNames_Model {
    String ArabicName;
    String EnglishName;
    int position;

    public SurahNames_Model(String arabicName, String englishName, int position) {
        ArabicName = arabicName;
        EnglishName = englishName;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getArabicName() {
        return ArabicName;
    }

    public void setArabicName(String arabicName) {
        ArabicName = arabicName;
    }

    public String getEnglishName() {
        return EnglishName;
    }

    public void setEnglishName(String englishName) {
        EnglishName = englishName;
    }
}
