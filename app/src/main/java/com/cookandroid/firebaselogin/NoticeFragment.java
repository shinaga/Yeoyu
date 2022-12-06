package com.cookandroid.firebaselogin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.MapView;

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

    final int[] next = new int[1];//다음 차례 글 번호를 불러오는 변수(홀수번째)

    boolean flag=false;//count에 값을 넣는것을 한번만 하기 위해서

    public NoticeFragment(Context context) {
        this.con = context;
    }
    Context con;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notice, container, false);

        return view;
    }
}
