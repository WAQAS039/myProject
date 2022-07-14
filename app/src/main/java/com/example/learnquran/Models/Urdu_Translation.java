package com.example.learnquran.Models;

public class Urdu_Translation {
    String Translation;
    int sura;
    int aya;

    public Urdu_Translation(String translation, int sura, int aya) {
        Translation = translation;
        this.sura = sura;
        this.aya = aya;
    }

    public String getTranslation() {
        return Translation;
    }

    public void setTranslation(String translation) {
        Translation = translation;
    }

    public int getSura() {
        return sura;
    }

    public void setSura(int sura) {
        this.sura = sura;
    }

    public int getAya() {
        return aya;
    }

    public void setAya(int aya) {
        this.aya = aya;
    }
}
