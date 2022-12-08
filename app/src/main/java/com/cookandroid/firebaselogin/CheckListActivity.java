package com.cookandroid.firebaselogin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cookandroid.firebaselogin.CheckList.Schedule.Schedule;
import com.cookandroid.firebaselogin.CheckList.Schedule.TimeTableView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CheckListActivity extends AppCompatActivity implements DialogCloseListener, View.OnClickListener {

    private DatabaseHandler db;

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;

    private List<ToDoModel> taskList;


    //기존의 CheckListActivity + ScheduleMainActivity 합침

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
        setContentView(R.layout.checklist_activity);
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //db = new DatabaseHandler(this);
        //db.openDatabase();
//
        //tasksRecyclerView = findViewById(R.id.taskRecyclerView);
        //tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //tasksAdapter = new ToDoAdapter(db, CheckListActivity.this);
        //tasksRecyclerView.setAdapter(tasksAdapter);
//
        //ItemTouchHelper itemTouchHelper = new
        //        ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        //itemTouchHelper.attachToRecyclerView(tasksRecyclerView);
//
        //fab = findViewById(R.id.fab);
//
        //taskList = db.getAllTasks();
        //Collections.reverse(taskList);
//
        //tasksAdapter.setTasks(taskList);
//
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
        //    }
        //});

        init();
    }
    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
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

