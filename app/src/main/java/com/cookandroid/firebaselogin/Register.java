package com.cookandroid.firebaselogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends Activity implements View.OnClickListener {
    private Context context;
    public Register(Context context) {//Context 말고 Activity로 해도 됨
        this.context = context;
    }
    @Override
    public void onClick(View view) {
        if(RegisterActivity.idCheck==false||RegisterActivity.nicknameCheck==false){
            Toast.makeText(context, "아이디와 닉네임을 중복체크 해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String id,nickName,pw,pw2,name,studentId,email;

        id = RegisterActivity.editId.getText().toString();
        nickName = RegisterActivity.editNinkname.getText().toString();
        pw = RegisterActivity.editPasswd.getText().toString();
        pw2 = RegisterActivity.editPasswd2.getText().toString();
        name = RegisterActivity.editName.getText().toString();
        studentId = RegisterActivity.editStudentId.getText().toString();
        email = SendEmailActivity.editWriteEmail.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference("user").child(id);
        if(!pw.equals(pw2)){//비밀번호를 다르게 입력했을시 아이디를 생성하지 않음
            Toast.makeText(context, "비밀번호를 똑같이 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        else{
            user.addListenerForSingleValueEvent(new ValueEventListener() {//addValueEventListener로 할시에는 아이디가 생성되고 "아이디가 중복이에요가 뜸" 왜냐하면 addValueEventListener는 경로의 전체 내용에 대한 변경 사항을 읽고 "수신 대기"한다.
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {//아이디 중복 확인(2명이 동시에 똑같은 id나 닉네임을 입력하는 것을 방지)
                        Toast.makeText(context, "아이디가 중복이에요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    user.child("nickname").setValue(nickName);//아이디가 중복이 아닐시 아이디를 만든다.
                    user.child("password").setValue(pw);
                    user.child("email").setValue(email);
                    user.child("name").setValue(name);
                    user.child("studentId").setValue(studentId);
                    Toast.makeText(context, "아이디가 생성되었어요.", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, "인터넷을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    throw databaseError.toException();// don't ignore errors
                }
            });//이 방법은 있는지 없는지 확인하는 방법
        }
    }
}
