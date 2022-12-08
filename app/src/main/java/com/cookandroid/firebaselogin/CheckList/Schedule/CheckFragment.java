package com.cookandroid.firebaselogin.CheckList.Schedule;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cookandroid.firebaselogin.AddNewTask;
import com.cookandroid.firebaselogin.CheckListActivity;
import com.cookandroid.firebaselogin.DatabaseHandler;
import com.cookandroid.firebaselogin.HomeActivity;
import com.cookandroid.firebaselogin.R;
import com.cookandroid.firebaselogin.RecyclerItemTouchHelper;
import com.cookandroid.firebaselogin.ToDoAdapter;
import com.cookandroid.firebaselogin.ToDoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

public class CheckFragment extends Fragment {
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

        //init();
        return view;
    }
}