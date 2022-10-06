package com.cookandroid.firebaselogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class NoticeviewActivity extends AppCompatActivity {
    int number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeview);

        numberSet();//받아온 게시글의 number를 가져옴

    }

    private void numberSet() {
        Intent intent = getIntent();
        number = Integer.parseInt(intent.getExtras().getString("number"));
        Toast.makeText(this,number+"",Toast.LENGTH_SHORT).show();
    }
}