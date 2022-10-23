package com.cookandroid.firebaselogin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    static EditText editId,editNinkname,editPasswd,editPasswd2,editName,editStudentId;
    TextView text_idOverlap,text_nicknameOverlap;
    Button btn_idOverlap, btn_nicknameOverlap, btnRegister;
    static boolean idCheck, nicknameCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();//액션바 숨기기

        editSet();
        textSet();
        btnSet();
        editEvent();//editText의 내용이 바뀌었을때
        btnClick();
    }
    private void editEvent(){
        editId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text_idOverlap.setText("");
                idCheck=false;
            }
            @Override
            public void afterTextChanged(Editable s) {
            }

        });
        editNinkname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text_nicknameOverlap.setText("");
                nicknameCheck=false;
            }
            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }
    private void editSet() {
        editId = findViewById(R.id.editId);
        editNinkname = findViewById(R.id.editNinkname);
        editPasswd = findViewById(R.id.editPasswd);
        editPasswd2 = findViewById(R.id.editPasswd2);
        editName = findViewById(R.id.editName);
        editStudentId = findViewById(R.id.editStudentId);
    }
    private void textSet() {
        text_idOverlap = findViewById(R.id.text_idOverlap);
        text_nicknameOverlap = findViewById(R.id.text_nicknameOverlap);
    }
    private void btnSet() {
        btn_idOverlap = findViewById(R.id.btn_idOverlap);
        btn_nicknameOverlap = findViewById(R.id.btn_nicknameOverlap);
        btnRegister = findViewById(R.id.btnRegister);
    }
    private void btnClick() {
        btn_idOverlap.setOnClickListener(v ->{
            checkId();//아이디 중복 확인
        });
        btn_nicknameOverlap.setOnClickListener(v ->{
            checkNickname();//아이디 중복 확인
        });
        btnRegister.setOnClickListener(new Register(this));
    }
    private void checkId(){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference user = database.getReference("user").child(editId.getText().toString());
            user.addListenerForSingleValueEvent(new ValueEventListener() {//addValueEventListener로 할시에는 아이디가 생성되고 "아이디가 중복이에요가 뜸" 왜냐하면 addValueEventListener는 경로의 전체 내용에 대한 변경 사항을 읽고 "수신 대기"한다.
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {//아이디 중복 확인
                    if(dataSnapshot.exists()) {
                        Toast.makeText(getApplicationContext(), "사용중인 아이디에요", Toast.LENGTH_SHORT).show();
                        idCheck = false;
                    }
                    else {
                        text_idOverlap.setText("사용 가능한 아이디 입니다.");
                        idCheck = true;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "인터넷을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    throw databaseError.toException();// don't ignore errors
                }
            });//이 방법은 있는지 없는지 확인하는 방법
    }
    private void checkNickname(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference("user");

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(editNinkname.getText().toString().equals(snapshot.child("nickname").getValue())){//닉네임이 존재하면
                        Toast.makeText(getApplicationContext(), "사용중인 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                        text_nicknameOverlap.setText("");
                        nicknameCheck = false;
                        break;
                    }
                    else{
                        text_nicknameOverlap.setText("사용 가능한 닉네임 입니다.");
                        nicknameCheck = true;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "인터넷을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                throw databaseError.toException(); // don't ignore errors
            }
        });//이 방법은 데이터를 for each 문으로 읽오어는 방법
    }
}