package com.cookandroid.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class SendEmailActivity extends AppCompatActivity {
    static EditText editWriteEmail;
    EditText editWriteCode;
    Button btnSendCode,btnRegister;
    String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();//액션바 숨기기

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        editSet();
        btnSet();
        btnClick();
    }
    private void editSet() {
        editWriteEmail = findViewById(R.id.editWriteEmail);
        editWriteCode = findViewById(R.id.editWriteCode);
    }
    private void btnSet() {
        btnSendCode = findViewById(R.id.btnSendCode);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void btnClick() {

        btnSendCode.setOnClickListener(view -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference user = database.getReference("user");
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snap : snapshot.getChildren()){
                        if(snap.child("email").getValue().equals(editWriteEmail.getText().toString())){//동일한 이메일이 존재한다면
                            Toast.makeText(view.getContext(), "이미 가입된 이메일 이에요", Toast.LENGTH_SHORT).show();
                            return;// 코드를 보내지 않는다.
                        }
                    }
                    sendEmail();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        btnRegister.setOnClickListener(view -> {
            if(!editWriteCode.getText().toString().equals(code)) {//이메일로 보낸코드와 입력한 코드가 다를때
                Toast.makeText(this, "코드가 달라요", Toast.LENGTH_SHORT).show();
            }
            else{//이메일로 보낸코드와 입력한 코드가 일치할때
                Intent intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    private void sendEmail(){
        String email=editWriteEmail.getText().toString();
        int isTrue = email.indexOf("@mjc.ac.kr",email.length()-10);

        if(isTrue==-1){//@ 뒤 주소가 mjc.ac.kr가 아닐때
            Toast.makeText(this, "@mjc.ac.kr 형식만 가능해요", Toast.LENGTH_SHORT).show();
        }
        else{//@ 뒤 주소가 mjc.ac.kr가 맞을때
            try {
                GMailSender gMailSender = new GMailSender("shinage0202@gmail.com", "vziwdddayckmuwfh");
                code = gMailSender.getEmailCode();
                //GMailSender.sendMail(제목, 본문내용, 받는사람);
                gMailSender.sendMail("여유앱 인증 코드 입니다.", code,editWriteEmail.getText().toString());//제목, 전송할 코드, 받는사람 이메일
                Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈어요.", Toast.LENGTH_SHORT).show();
            } catch (SendFailedException e) {
                Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었어요.", Toast.LENGTH_SHORT).show();
            } catch (MessagingException e) {
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}