package com.cookandroid.firebaselogin;

public class Notice {
    int number;
    String title;
    String date;
    String context;
    boolean isCheck;
    int htCnt;
    int cmtCnt;
    public Notice(int number, String title, String date, String context, boolean isCheck, int htCnt, int cmtCnt) {
        this.number = number;
        this.title = title;
        this.date= date;
        this.context = context;
        this.isCheck = isCheck;
        this.htCnt = htCnt;
        this.cmtCnt = cmtCnt;
    }
    public int getNumber() {
        return number;
    }
    public String getTitle() {
        return title;
    }
    public String getDate() { return date; }
    public String getContext() {return context;}
    public boolean getIsCheck() {return isCheck;}
    public int getHtCnt() {return htCnt;}
    public int getCmtCnt() {return cmtCnt;}

    public void setNumber(int number) {
        this.number = number;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setContext(String context) {
        this.context = context;
    }
    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
    public void setHtCnt(int htCnt) {
        this.htCnt = htCnt;
    }
    public void setCmtCnt(int cmtCnt) {
        this.cmtCnt = cmtCnt;
    }
}