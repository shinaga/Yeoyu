package com.cookandroid.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.cookandroid.firebaselogin.CheckList.Schedule.CheckFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

import noman.googleplaces.Place;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class HomeActivity extends AppCompatActivity{
       MapFragment mapFragment;
       NoticeFragment noticeFragment;
       CheckFragment checkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();//액션바 숨기기

        mapFragment = new MapFragment(HomeActivity.this);
        noticeFragment = new NoticeFragment(HomeActivity.this);
        checkFragment = new CheckFragment(HomeActivity.this,HomeActivity.this);

         getSupportFragmentManager().beginTransaction().replace(R.id.containers, noticeFragment).commit();

        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigationview);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.notice:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, noticeFragment).commit();
                        return true;
                    case R.id.schedule:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, checkFragment).commit();
                        return true;
                    case R.id.map:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, mapFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}