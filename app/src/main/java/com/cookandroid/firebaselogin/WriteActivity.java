package com.cookandroid.firebaselogin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class WriteActivity extends AppCompatActivity {
    static EditText editTitle,editContent;
    Button btnWrite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();//액션바 숨기기

        editSet();
        btnSet();
        btnClick();
    }
    private void editSet() {
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
    }
    private void btnSet() {
        btnWrite = findViewById(R.id.btnWrite);
    }

    private void btnClick() {btnWrite.setOnClickListener(new Write(this));}
}