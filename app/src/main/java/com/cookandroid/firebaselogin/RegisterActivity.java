package com.cookandroid.firebaselogin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    static EditText editId,editPasswd,editPasswd2,editName,editStudentId;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();//액션바 숨기기

        editSet();
        btnSet();
        btnClick();

    }
    private void editSet() {
        editId = findViewById(R.id.editId);
        editPasswd = findViewById(R.id.editPasswd);
        editPasswd2 = findViewById(R.id.editPasswd2);
        editName = findViewById(R.id.editName);
        editStudentId = findViewById(R.id.editStudentId);
    }  private void btnSet() {
        btnRegister = findViewById(R.id.btnRegister);
    }
    private void btnClick() {
        btnRegister.setOnClickListener(new Register(this));
    }


}