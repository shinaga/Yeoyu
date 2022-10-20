package com.cookandroid.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;

public class MainActivity extends AppCompatActivity {
    static EditText editId,editPasswd;
    Button btnLogin;
    TextView textRegister,textFindPw;
    int tmp;//Git Test용
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();//액션바 숨기기

        editSet();
        btnSet();
        btnClick();
        textSet();
        textClick();
    }
    private void editSet() {
        editId = findViewById(R.id.editId);
        editPasswd = findViewById(R.id.editPasswd);
    }
    private void btnSet() {
        btnLogin = findViewById(R.id.btnLogin);
    }
    private void btnClick() {
        btnLogin.setOnClickListener(new Login(this));
    }
    private void textSet() {
        textRegister = findViewById(R.id.textRegister);
        textFindPw = findViewById(R.id.textFindPw);
    }
    private void textClick(){
        textRegister.setOnClickListener(view -> {
                Intent intent = new Intent(this,SendEmailActivity.class);
                startActivity(intent);
            return;
        });
    }
}