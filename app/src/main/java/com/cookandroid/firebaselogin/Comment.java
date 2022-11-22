package com.cookandroid.firebaselogin;

public class Comment {
    int number;
    String nickname;
    String date;
    String context;

    public Comment(int number, String nickname, String date, String context) {
        this.number = number;
        this.nickname = nickname;
        this.date= date;
        this.context = context;
    }
    public int getNumber() {
        return number;
    }
    public String getNickname() {
        return nickname;
    }
    public String getDate() {
        return date;
    }
    public String getContext() {return context;}

    public void setNumber(int number) {
        this.number = number;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setContext(String context) {
        this.context = context;
    }
}