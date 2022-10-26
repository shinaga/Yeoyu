package com.cookandroid.firebaselogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
public class Write extends Activity implements View.OnClickListener  {
    private Context context;
    public Write(Context context) {//Context 말고 Activity로 해도 됨
        this.context = context;
    }
    @Override
    public void onClick(View view) {
        String id, nickname, title, content;
        id = MainActivity.editId.getText().toString();
        title = WriteActivity.editTitle.getText().toString();
        content = WriteActivity.editContent.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference noticeCount = database.getReference("noticeCount");//현재 글 개수가 담아져 있는 변수
        final int[] number = new int[1];//현재글 개수를 담아오는 배열 변수, 배열로 하지 않으면 저장이 안된다.
                        noticeCount.addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            number[0]=Integer.valueOf(dataSnapshot.getValue().toString());//str을 int로 변환후 number[0]에 저장
                            noticeCount.setValue(++number[0]);
                            DatabaseReference newNotice=database.getReference("notice").child(number[0]+"");//newNotice는 가장 끝번호를 가진 DatabaseReference가 된다.
                            newNotice.child("number").setValue(number[0]);
                            newNotice.child("id").setValue(id);
                            newNotice.child("title").setValue(title);
                            newNotice.child("context").setValue(content);
                            newNotice.child("date").setValue(LocalDate.now().toString());

                            Intent intent = new Intent(context, NoticeActivity.class);// 다시 돌아간다. NoticeActivity로
                            context.startActivity(intent);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "글 등록에 실패했어요.", Toast.LENGTH_SHORT).show();
                            throw error.toException();// don't ignore errors
                        }
                    });

    }
}
