package com.cookandroid.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.time.LocalDate;
import java.util.ArrayList;

public class NoticeviewActivity extends AppCompatActivity{
    int number;
    String id,nick;
    TextView nickname,date,context;
    EditText editComment;
    ActionBar actionBar;
    ImageView imgUpload[] = new ImageView[3];

    private RecyclerView recyclerView;
    private CommentListAdapter recyclerAdapter;
    private ArrayList<Comment> commentList;//리사이클러뷰에 넣어줄 리스트

    int c_number;
    String c_nickname;
    String c_date;
    String c_comment;

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
                if(id.equals(MainActivity.editId.getText().toString())){//삭제하려는 글의 아이디와 로그인한 유저의 아이디를 확인
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference notice = database.getReference("notice").child(number+"");

                    notice.removeValue();//글 삭제
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

        textSet();//TextVeiw 세팅
        editSet();//EditText 세팅
        numberSet();//받아온 게시글의 number를 가져옴
        setId();//;어떤 아이디의 글인지 아이디 세팅

        recyclerViewSet();//RecyclerView 세팅한다.
        loadComment();//리사이클러뷰에 담을 댓글 목록 가져오기
        imgSet();
    }



    private void numberSet() {
        Intent intent = getIntent();
        number = Integer.parseInt(intent.getExtras().getString("number"));
    }
    private void setId() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference("notice").child(number+"").child("id");
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id = snapshot.getValue()+"";
                setNickname();//어떤 닉네임인지 세팅, onCreate에서 호출할시 파이어베이스의 속도 때문에 오류가 나서 여기서 호출
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void setNickname() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference("user").child(id).child("nickname");
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nick = snapshot.getValue()+"";
                setNotice();//제목, id, 내용, 날짜 등을 가져오고 세팅, 이것도 여기서 호출
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void textSet() {
        nickname = findViewById(R.id.nickname);
        date = findViewById(R.id.date);
        context = findViewById(R.id.context);
    }
    private void editSet() {
        editComment = findViewById(R.id.editComment);
    }
    public void send(View v){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference comment = database.getReference("notice").child(number+"").child("comment");//댓글을 작성할 곳

        final int[] count = new int[1];//현재댓글 개수를 담아오는 배열 변수, 배열로 하지 않으면 저장이 안된다.
        DatabaseReference commentCount = database.getReference("notice").child(number+"").child("commentCount");
        commentCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count[0] = Integer.valueOf(snapshot.getValue()+"");
                commentCount.setValue(++count[0]);

                comment.child(count[0]+"").child("id").setValue(id);
                comment.child(count[0]+"").child("nickname").setValue(nickname.getText().toString());
                comment.child(count[0]+"").child("date").setValue(LocalDate.now().toString());
                comment.child(count[0]+"").child("comment").setValue(editComment.getText().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void recyclerViewSet() {
        commentList = new ArrayList<Comment>();

        recyclerView = findViewById(R.id.recyclerView);
        /* initiate adapter */
        recyclerAdapter = new CommentListAdapter();

        /* initiate recyclerview */
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));

        recyclerAdapter.setCommentList(commentList);//recyclerView에 noticeList를 연결한다.
    }
    private void loadComment(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference comment = database.getReference("notice").child(number+"").child("comment");//for문을 돌 변수

        DatabaseReference count = database.getReference("notice").child(number+"").child("commentCount");

        count.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i[] = new int[1];//for문에 쓸 i도 배열로 해야함

                for(i[0]=1;i[0]<=Integer.valueOf(snapshot.getValue()+"");i[0]++) {
                    comment.child(i[0] + "").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {//없는 댓글을 불러오지 않기 위함
                                c_number = i[0];
                                c_nickname = snapshot.child("nickname").getValue() + "";
                                c_date = snapshot.child("date").getValue() + "";
                                c_comment = snapshot.child("comment").getValue() + "";

                                commentList.add(new Comment(c_number, c_nickname, c_date, c_comment));
                                recyclerAdapter.setCommentList(commentList);//리사이클러뷰에 데이터를 넣는다.
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void setNotice() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference notice = database.getReference("notice").child(number+"");

        notice.addListenerForSingleValueEvent(new ValueEventListener() {//addValueEventListener은 항상 데이터를 대기하고 있기에 쓰면 안된다.
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                actionBar.setTitle(snapshot.child("title").getValue()+"");
                nickname.setText(nick);
                date.setText(snapshot.child("date").getValue()+"");
                context.setText(snapshot.child("context").getValue()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"네트워크를 확인해 주세요.",Toast.LENGTH_SHORT);
            }
        });
    }
    private void imgSet() {
        imgUpload[0] = findViewById(R.id.imgUpload1);
        imgUpload[1] = findViewById(R.id.imgUpload2);
        imgUpload[2] = findViewById(R.id.imgUpload3);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("notice/89/0.png");

        if (pathReference == null) {
            Toast.makeText(NoticeviewActivity.this, "저장소에 사진이 없습니다." ,Toast.LENGTH_SHORT).show();
        } else {
            StorageReference submitProfile = storageReference.child("notice/89/0.png");
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    ImageView i = findViewById(R.id.i);
                    Glide.with(NoticeviewActivity.this).load(uri).into(i);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}