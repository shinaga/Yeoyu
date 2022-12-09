package com.cookandroid.firebaselogin.CheckList.Schedule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cookandroid.firebaselogin.AddNewTask;
import com.cookandroid.firebaselogin.CheckListActivity;
import com.cookandroid.firebaselogin.DatabaseHandler;
import com.cookandroid.firebaselogin.DialogCloseListener;
import com.cookandroid.firebaselogin.HomeActivity;
import com.cookandroid.firebaselogin.R;
import com.cookandroid.firebaselogin.RecyclerItemTouchHelper;
import com.cookandroid.firebaselogin.ScheduleEdit;
import com.cookandroid.firebaselogin.ToDoAdapter;
import com.cookandroid.firebaselogin.ToDoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckFragment extends Fragment implements DialogCloseListener, View.OnClickListener{
    public static DatabaseHandler db;

    private RecyclerView tasksRecyclerView;
    public static ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;

    public static List<ToDoModel> taskList;


    //기존의 CheckListActivity + ScheduleMainActivity 합침

    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private Button addBtn;
    private Button clearBtn;
    private Button saveBtn;
    private Button loadBtn;

    private TimeTableView timetable;
    Context con;
    View view;
    HomeActivity h;
    public CheckFragment(Context context, HomeActivity h) {
        this.con = context;
        this.h = h;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.checklist_activity, container, false);
        db = new DatabaseHandler(con);
        db.openDatabase();

        tasksRecyclerView = view.findViewById(R.id.taskRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(con));
        tasksAdapter = new ToDoAdapter(db, h);
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = view.findViewById(R.id.fab);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getActivity().getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        init();
        return view;
    }
    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }

    private void init() {

        addBtn = view.findViewById(R.id.add_btn);
        clearBtn = view.findViewById(R.id.clear_btn);
        saveBtn = view.findViewById(R.id.save_btn);
        loadBtn = view.findViewById(R.id.load_btn);


        timetable = view.findViewById(R.id.timetable);
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
                Intent i = new Intent(con, ScheduleEdit.class);
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
                Intent i = new Intent(con, ScheduleEdit.class);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(con);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("timetable_demo",data);
        editor.commit();
        Toast.makeText(con,"saved!",Toast.LENGTH_SHORT).show();
    }

    private void loadSavedData(){
        timetable.removeAll();
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(con);
        String savedData = mPref.getString("timetable_demo","");
        if(savedData == null && savedData.equals("")) return;
        timetable.load(savedData);
        Toast.makeText(con,"loaded!",Toast.LENGTH_SHORT).show();
    }
}