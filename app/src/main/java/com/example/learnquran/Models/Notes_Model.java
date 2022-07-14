package com.example.learnquran.Models;

public class Notes_Model {
    int NoteId;
    String NotesName;
    int SuraId;
    int VerseId;
    String VerseText;
    String Note;
    String SurahName;

    public Notes_Model(int noteId, String notesName, int suraId, int verseId, String verseText, String note, String surahName) {
        NoteId = noteId;
        NotesName = notesName;
        SuraId = suraId;
        VerseId = verseId;
        VerseText = verseText;
        Note = note;
        SurahName = surahName;
    }

    public int getNoteId() {
        return NoteId;
    }

    public String getNotesName() {
        return NotesName;
    }

    public int getSuraId() {
        return SuraId;
    }

    public int getVerseId() {
        return VerseId;
    }

    public String getVerseText() {
        return VerseText;
    }

    public String getNote() {
        return Note;
    }

    public String getSurahName() {
        return SurahName;
    }
}
