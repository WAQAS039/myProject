package com.example.learnquran.Models;

public class QuranText_Model {
    int SurahID;
    int VerseId;
    String ArabicText;

    public QuranText_Model(int surahID, int verseId, String arabicText) {
        SurahID = surahID;
        VerseId = verseId;
        ArabicText = arabicText;
    }

    public int getSurahID() {
        return SurahID;
    }

    public void setSurahID(int surahID) {
        SurahID = surahID;
    }

    public int getVerseId() {
        return VerseId;
    }

    public void setVerseId(int verseId) {
        VerseId = verseId;
    }

    public String getArabicText() {
        return ArabicText;
    }

    public void setArabicText(String arabicText) {
        ArabicText = arabicText;
    }
}
