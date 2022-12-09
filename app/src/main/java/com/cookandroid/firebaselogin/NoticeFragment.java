package com.cookandroid.firebaselogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoticeFragment extends Fragment {
    Button btnWrite;

    private RecyclerView recyclerView;
    private NoticeListAdapter recyclerAdapter;
    private ArrayList<Notice> noticeList;//리사이클러뷰에 넣어줄 리스트

    final int[] count = new int[1];//현재글 개수를 담아오는 배열 변수, 마찬가지로 배열로 하지 않으면 저장이 안된다.

    final int[] number = new int[1];//프론트에선 안보임
    final String[] title = new String[1];
    final String[] date = new String[1];
    final String[] context = new String[1];
    final boolean[] isCheck = new boolean[1];
    final int[] htCnt = new int[1];
    final int[] cmtCnt = new int[1];

    final int[] next = new int[1];//다음 차례 글 번호를 불러오는 변수(홀수번째)

    boolean flag=false;//count에 값을 넣는것을 한번만 하기 위해서
    Context con;
    View view;
    public NoticeFragment(Context context) {
        this.con = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_notice, container, false);

        btnSet();//글작성 버튼을 세팅한다.
        btnClick();//글작성 버튼 클릭시 발생하는 이벤트 함수 설정한다.

        recyclerViewSet();//RecyclerView 세팅한다.
        readNotices(-100);//리사이클러뷰에 담을 글 목록 가져오기, 맨처음에는 -100을 줘서 맨처음 임을 암시
        recyclerViewScrolled();//이제부터 스크롤 끝에 다다르면 이 함수가 실행된다.
        return view;

    }
    private void btnSet() {btnWrite = view.findViewById(R.id.btnWrite);}
    private void btnClick() {
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, WriteActivity.class);//글 작성 화면
                startActivity(intent);
            }
        });

    }
    private void readNotices(int tmp){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference noticeCount = database.getReference("noticeCount");

        DatabaseReference notice = database.getReference("notice");

        noticeCount.addListenerForSingleValueEvent(new ValueEventListener() {//노티스 넘버에 접근하기 위함
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(flag==false)count[0]=Integer.valueOf(snapshot.getValue().toString());//str을 int로 변환후 number[0]에 저장
                int i[] = new int[1];//for문에 쓸 i도 배열로 해야함
                if(tmp==-100) i[0]=count[0];
                else i[0]=tmp;
                for(;i[0]>count[0]-10;i[0]--){//i는 noticeCount 수, 10개의 글을 보여주기 위함이다. 단 삭제된 글이 있으면 10개가 안될 수도 있다.

                    notice.child(i[0]+"").addListenerForSingleValueEvent(new ValueEventListener() {//글을 읽어 오기 위함
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){//없는 글을 불러오지 않기 위함
                                number[0] = Integer.valueOf(snapshot.child("number").getValue()+"");
                                title[0] = snapshot.child("title").getValue()+"";
                                date[0] = snapshot.child("date").getValue()+"";
                                context[0] = snapshot.child("context").getValue()+"";
                                String id = MainActivity.editId.getText().toString();
                                if(snapshot.child("hearth").child(id).getValue()==null||(boolean)snapshot.child("hearth").child(id).getValue()==false){//내가 좋아요를 누른 글인지 아닌지 판단
                                    isCheck[0] = false;
                                }
                                else isCheck[0] = true;
                                htCnt[0] = Integer.valueOf(snapshot.child("hearthCount").getValue()+"");
                                cmtCnt[0] = Integer.valueOf(snapshot.child("commentCount").getValue()+"");

                                noticeList.add(new Notice(number[0], title[0],date[0], context[0],isCheck[0],htCnt[0],cmtCnt[0]));
                                recyclerAdapter.setNoticeList(noticeList);//리사이클러뷰에 데이터를 넣는다.
                                next[0]=count[0]-10;//for문에 안넣으려 했으나 오류 때문에 반복문에 넣음
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    if(i[0]==count[0]-10+1&&noticeList.size()==0&&count[0]>0){//삭제된 글로 인해 아무 글도 불러오지 않을때를 대비
                        flag=true;//이미 한번 불러왔다는 뜻
                        readNotices(count[0]-10);//다시 한번 불러온다.
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void recyclerViewSet() {
        noticeList = new ArrayList<Notice>();

        recyclerView = view.findViewById(R.id.recyclerView);
        /* initiate adapter */
        recyclerAdapter = new NoticeListAdapter();

        /* initiate recyclerview */
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(con));
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));

        recyclerAdapter.setNoticeList(noticeList);//RecyclerView에 noticeList를 연결한다.
    }
    private void recyclerViewScrolled(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {//스크롤 끝 감지, 근데 이상하게 맨처음에 한번 실행됨
                    loadNotice();
                }
            }
        });
    }
    private void loadNotice(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference notice = database.getReference("notice");

        notice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i[] = new int[1];//for문에 쓸 i도 배열로 해야함
                for (i[0] = next[0]; i[0] > next[0] - 10; i[0]--) {//i는 noticeCount 수, 10개의 글을 보여주기 위함이다. 단 삭제된 글이 있으면 10개가 안될 수도 있다.

                    notice.child(i[0] + "").addListenerForSingleValueEvent(new ValueEventListener() {//글을 읽어 오기 위함
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){//없는 글을 불러오지 않기 위함
                                number[0] = Integer.valueOf(snapshot.child("number").getValue().toString());
                                title[0] = snapshot.child("title").getValue().toString();
                                date[0] = snapshot.child("date").getValue().toString();
                                context[0] = snapshot.child("context").getValue().toString();
                                String id = MainActivity.editId.getText().toString();
                                if(snapshot.child("hearth").child(id).getValue()==null||(boolean)snapshot.child("hearth").child(id).getValue()==false){//내가 좋아요를 누른 글인지 아닌지 판단
                                    isCheck[0]=false;
                                }
                                htCnt[0] = Integer.valueOf(snapshot.child("hearthCount").getValue()+"");
                                cmtCnt[0] = Integer.valueOf(snapshot.child("commentCount").getValue()+"");

                                ArrayList<Notice> n = new ArrayList<Notice>();
                                n.add(new Notice(number[0], title[0],date[0], context[0],isCheck[0],htCnt[0],cmtCnt[0]));
                                recyclerAdapter.addNoticeList(n);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                next[0]-=10;//다음글 번째를 10 낮춤
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

