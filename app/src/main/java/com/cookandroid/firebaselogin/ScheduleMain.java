package com.cookandroid.firebaselogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.firebaselogin.CheckList.Schedule.TimeTableView;
import com.cookandroid.firebaselogin.CheckList.Schedule.Schedule;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;

public class ScheduleMain extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private Context context;

    private Button addBtn;
    private Button clearBtn;
    private Button saveBtn;
    private Button loadBtn;

    private TimeTableView timetable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_main);

        init();
    }

    private void init() {
        this.context = this;

        addBtn = findViewById(R.id.add_btn);
        clearBtn = findViewById(R.id.clear_btn);
        saveBtn = findViewById(R.id.save_btn);
        loadBtn = findViewById(R.id.load_btn);

        timetable = findViewById(R.id.timetable);
        timetable.setHeaderHighlight(2);

        initView();
    }

    private void initView() {
        addBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        loadBtn.setOnClickListener(this);


        timetable.setOnStickerSelectEventListener(new TimeTableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                Intent i = new Intent(context, ScheduleEdit.class);
                i.putExtra("mode", REQUEST_EDIT);
                i.putExtra("idx", idx);
                i.putExtra("schedules", schedules);
                startActivityForResult(i, REQUEST_EDIT);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                Intent i = new Intent(this, ScheduleEdit.class);
                i.putExtra("mode", REQUEST_ADD);
                startActivityForResult(i, REQUEST_ADD);
                break;

            case R.id.clear_btn:
                timetable.removeAll();
                break;

            case R.id.save_btn:
                saveByPreference(timetable.createSaveData());
                break;

            case R.id.load_btn:
                loadSavedData();
                break;
        }
    }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       switch (requestCode) {
           case REQUEST_ADD:
               if (resultCode == ScheduleEdit.RESULT_OK_ADD) {
                   ArrayList<Schedule> item = (ArrayList<Schedule>) data.getSerializableExtra("schedules");
                   timetable.add(item);
               }
               break;
           case REQUEST_EDIT:
               if (resultCode == ScheduleEdit.RESULT_OK_EDIT) {
                   int idx = data.getIntExtra("idx", -1);
                   ArrayList<Schedule> item = (ArrayList<Schedule>) data.getSerializableExtra("schedules");
                   timetable.edit(idx, item);
               } else if (resultCode == ScheduleEdit.RESULT_OK_DELETE) {
                   int idx = data.getIntExtra("idx", -1);
                   timetable.remove(idx);
               }
               break;
       }
   }

    private void saveByPreference(String data){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("timetable_demo",data);
        editor.commit();
        Toast.makeText(this,"saved!",Toast.LENGTH_SHORT).show();
    }

    private void loadSavedData(){
        timetable.removeAll();
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String savedData = mPref.getString("timetable_demo","");
        if(savedData == null && savedData.equals("")) return;
        timetable.load(savedData);
        Toast.makeText(this,"loaded!",Toast.LENGTH_SHORT).show();
    }
}
