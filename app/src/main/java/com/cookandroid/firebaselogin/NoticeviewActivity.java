package com.cookandroid.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class NoticeviewActivity extends AppCompatActivity {
    int number;
    TextView title,id,date,context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeview);

        numberSet();//받아온 게시글의 number를 가져옴
        textSet();//TextVeiw 세팅
        setNotice();//제목, id, 내용, 날짜 등을 가져옴
    }

    private void numberSet() {
        Intent intent = getIntent();
        number = Integer.parseInt(intent.getExtras().getString("number"));
    }
    private void textSet() {
        title = findViewById(R.id.title);
        id = findViewById(R.id.id);
        date = findViewById(R.id.date);
        context = findViewById(R.id.context);
    }
    private void setNotice() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference("notice").child(number+"");

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                title.setText(snapshot.child("title").getValue()+"");
                id.setText(snapshot.child("id").getValue()+"");
                date.setText(snapshot.child("date").getValue()+"");
                context.setText(snapshot.child("context").getValue()+"");
            }
////////////////////
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"네트워크를 확인해 주세요.",Toast.LENGTH_SHORT);
            }
        });
    }

}