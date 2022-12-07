package com.cookandroid.firebaselogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends Activity implements View.OnClickListener{
    private Context context;
    public Login(Context context) {//Context 말고 Activity로 해도 됨
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        String id=MainActivity.editId.getText().toString();
        String pw=MainActivity.editPasswd.getText().toString();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference("user").child(id);


        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {//아이디가 있는지 없는지 부터 확인
                    user.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue().equals(pw)){
                                Intent intent = new Intent(context, IntegratedActivity.class);
                                context.startActivity(intent);
                            }
                            else Toast.makeText(context, "아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context, "네트워크를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else Toast.makeText(context, "아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("로그인여부","그냥 오류땜에 로그인실패");
                throw databaseError.toException(); // don't ignore errors
            }
        });//이 방법은 데이터를 읽오어는 방법
    }
}
