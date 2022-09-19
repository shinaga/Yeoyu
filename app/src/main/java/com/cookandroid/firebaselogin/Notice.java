package com.cookandroid.firebaselogin;

public class Notice {
    int number;
    String title;
    String date;
    String context;

    public Notice(int number, String title, String date, String context) {
        this.number = number;
        this.title = title;
        this.date= date;
        this.context = context;
    }
    public int getNumber() {
        return number;
    }
    public String getTitle() {
        return title;
    }
    public String getDate() {
        return date;
    }
    public String getContext() {return context;}

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
}