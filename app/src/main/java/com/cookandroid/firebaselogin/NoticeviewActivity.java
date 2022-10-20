package com.cookandroid.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    TextView id,date,context;
    ActionBar actionBar;

    @Override//액션바 생성
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override//액션바 클릭시
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {

            case R.id.delete:
                if(id.getText().toString().equals(MainActivity.editId.getText().toString())){//삭제하려는 글의 아이디와 로그인한 유저의 아이디를 확인
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference user = database.getReference("notice").child(number+"");

                    user.removeValue();//글 삭제
                    Toast.makeText(getApplicationContext(), "글이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(),NoticeActivity.class);
                    startActivity(intent);//삭제 후 이동
                }
                else Toast.makeText(getApplicationContext(), "자신의 글만 삭제 가능합니다.", Toast.LENGTH_SHORT).show();//일치하지 않을시
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeview);
        actionBar = getSupportActionBar();//액션바

        numberSet();//받아온 게시글의 number를 가져옴
        textSet();//TextVeiw 세팅
        setNotice();//제목, id, 내용, 날짜 등을 가져옴
    }

    private void numberSet() {
        Intent intent = getIntent();
        number = Integer.parseInt(intent.getExtras().getString("number"));
    }
    private void textSet() {
        id = findViewById(R.id.id);
        date = findViewById(R.id.date);
        context = findViewById(R.id.context);
    }
    private void setNotice() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference("notice").child(number+"");

        user.addListenerForSingleValueEvent(new ValueEventListener() {//addValueEventListener은 항상 데이터를 대기하고 있기에 쓰면 안된다.
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                actionBar.setTitle(snapshot.child("title").getValue()+"");
                id.setText(snapshot.child("id").getValue()+"");
                date.setText(snapshot.child("date").getValue()+"");
                context.setText(snapshot.child("context").getValue()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"네트워크를 확인해 주세요.",Toast.LENGTH_SHORT);
            }
        });
    }

}