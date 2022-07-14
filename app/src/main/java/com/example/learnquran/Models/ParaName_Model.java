package com.example.learnquran.Models;

public class ParaName_Model {
    String ParaName;
    int ParaFirstAyahPosition;

    public ParaName_Model(String paraName, int paraFirstAyahPosition) {
        ParaName = paraName;
        ParaFirstAyahPosition = paraFirstAyahPosition;
    }

    public String getParaName() {
        return ParaName;
    }

    public int getParaFirstAyahPosition() {
        return ParaFirstAyahPosition;
    }
}
