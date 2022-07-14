package com.example.learnquran.Models;

public class BookMarkList_Model {
    String BookMarkTitle;
    String BookMarkedSurahName;
    int AyahPosition;

    public BookMarkList_Model(String bookMarkTitle, String bookMarkedSurahName, int ayahPosition) {
        BookMarkTitle = bookMarkTitle;
        BookMarkedSurahName = bookMarkedSurahName;
        AyahPosition = ayahPosition;
    }

    public int getAyahPosition() {
        return AyahPosition;
    }

    public void setAyahPosition(int ayahPosition) {
        AyahPosition = ayahPosition;
    }

    public String getBookMarkTitle() {
        return BookMarkTitle;
    }

    public void setBookMarkTitle(String bookMarkTitle) {
        BookMarkTitle = bookMarkTitle;
    }

    public String getBookMarkedSurahName() {
        return BookMarkedSurahName;
    }

    public void setBookMarkedSurahName(String bookMarkedSurahName) {
        BookMarkedSurahName = bookMarkedSurahName;
    }
}
