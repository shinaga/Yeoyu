package com.cookandroid.firebaselogin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

public class DiaryActivity extends AppCompatActivity {

    public String fname= null;
    // 추후 구현될 로그인 기능에서 이름을 받아와서 사용할 예정
    public String str=null;
    // 달력에 적힐 메모를 저장하는 변수
    public CalendarView calendarView;
    // 캘린더
    public Button cha_Btn,del_Btn,save_Btn;
    // 각각 메모 저장, 수정, 삭제 버튼
    public TextView diaryTextView,textView2,textView3;
    // 상단의 이름이 들어갈 텍스트 뷰, 캘린더에서 선택한 날짜를 보여줄 텍스트 뷰, 메모를 보여주는 텍스트 뷰
    public EditText contextEditText;
    public TimePicker time_picker;
    // 시간을 예약하는데 사용하는 시계? 기능
    public Button save;
    // 설정한 시간을 예약하는 버튼
    public Button time_btn;

    public static Context context_main;
    public int btn_count = 0;
    // 버튼이 눌린 횟수를 카운트할 변수
    private Long ClickTime = null;
    // 버튼을 너무 빨리 중복해서 누르는 것을 방지할 변수

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();//액션바 숨기기

        setContentView(R.layout.activity_diary);
        calendarView=findViewById(R.id.calendarView);
        // 달력 위젯
        diaryTextView=findViewById(R.id.diaryTextView);
        // 화면에 캘린더에서 선택한 날짜를 띄워줄 텍스트 뷰
        save_Btn=findViewById(R.id.save_Btn);
        // 메모 저장 버튼
        del_Btn=findViewById(R.id.del_Btn);
        // 저장된 메모 삭제 버튼
        cha_Btn=findViewById(R.id.cha_Btn);
        // 저잗된 메모 수정 버튼
        textView2=findViewById(R.id.textView2);
        // 저장되는 메모의 텍스트뷰
        textView3=findViewById(R.id.textView3);
        // ~~의 캘린더(최상단 이름)
        contextEditText=findViewById(R.id.contextEditText);
        // 로그인 및 회원가입 엑티비티에서 이름을 받아옴
        time_picker = (TimePicker)findViewById(R.id.time_picker);
        // 시간 예약을 하게 하는 위젯
        time_btn = (Button)findViewById(R.id.time_btn);
        // 시간 설정을 하기 위해 시계를 불러오는 버튼
        save = (Button)findViewById(R.id.save);
        // 세팅된 알람을 저장하는 버튼

        time_picker.setVisibility(View.INVISIBLE);
        // 시간을 설정하는 time_picker를 보이지 않는 상태로 설정

        context_main = this;

        // "시간예약" 버튼을 클릭 시 발생하는 이벤트
        save.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int hour = time_picker.getHour();
            int minute = time_picker.getMinute();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if(calendar.before(Calendar.getInstance())){
                calendar.add(Calendar.DATE, 1);
            }
            AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
            if(alarmManager != null) {
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

                Toast.makeText(DiaryActivity.this, "알람이 저장되었습니다", Toast.LENGTH_LONG).show();
            }
        });

        // "시간설정" 버튼을 눌렀을 때 발생하는 이벤트
        time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //버튼이 눌린 횟수를 나눈 나머지에 따라서 시간설정 위젯을 키거나 끔
                btn_count += 1;
                // 버튼이 한 번 눌린 경우 시간 설정 창을 활성화
                if(btn_count % 2 == 1){
                    time_picker.setVisibility(View.VISIBLE);
                } else
                    // 버튼이 다시 눌리면 시간 설정 창을 비활설화
                    time_picker.setVisibility(View.INVISIBLE);
            }
        });

        Intent intent = getIntent();
        String name = MainActivity.editId.getText().toString();
        // User의 이름을 받아와서 "~의 다이어리"로 상단에 띄움
        final String userID=intent.getStringExtra("userID");
        textView3.setText(name+"님의 다이어리");
        // 회원가입 시 받아온 유저의 이름을 사용

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                // 저장된 메모를 수정 및 삭제하는 버튼을 제외한 나머지의 위젯만이 드러나도록 설정
                // 수정과 삭제 버튼은 이미 저장된 메모를 대상으로만 보이도록 설정
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d", year, month+1, dayOfMonth));
                contextEditText.setText("");
                checkDay(year,month,dayOfMonth,userID);
            }
        });
        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDiary(fname);
                str = contextEditText.getText().toString();
                textView2.setText(str);
                save_Btn.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);
                time_picker.setVisibility(View.INVISIBLE);

            }
        });

    }

    public void  checkDay(int cYear,int cMonth,int cDay,String userID){
        fname = ""+userID+cYear+"-"+(cMonth+1)+""+"-"+cDay+".txt";//저장할 파일 이름설정
        FileInputStream fis = null;//FileStream fis 변수
        // 하위 디렉터리에 있는 응용프로그램 파일을 읽기 모드로 오픈

        try{
            fis=openFileInput(fname);

            byte[] fileData=new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            str=new String(fileData);

            contextEditText.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView2.setText(str);

            save_Btn.setVisibility(View.INVISIBLE);
            cha_Btn.setVisibility(View.VISIBLE);
            del_Btn.setVisibility(View.VISIBLE);

            // 저장된 메모를 수정하는 버튼, 수정버튼 클릭 시 이벤트
            cha_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contextEditText.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.INVISIBLE);
                    contextEditText.setText(str);

                    save_Btn.setVisibility(View.VISIBLE);
                    cha_Btn.setVisibility(View.INVISIBLE);
                    del_Btn.setVisibility(View.INVISIBLE);
                    textView2.setText(contextEditText.getText());
                }

            });
            // 저장된 메모를 삭제하는 버튼, 삭제버튼 클릭 시 이벤트
            del_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView2.setVisibility(View.INVISIBLE);
                    contextEditText.setText("");
                    contextEditText.setVisibility(View.VISIBLE);
                    save_Btn.setVisibility(View.VISIBLE);
                    cha_Btn.setVisibility(View.INVISIBLE);
                    del_Btn.setVisibility(View.INVISIBLE);
                    removeDiary(fname);
                }
            });

            if(textView2.getText()==null){
                textView2.setVisibility(View.INVISIBLE);
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
            }

        } catch (Exception e){
            // 에러 메세지의 발생 근원지를 찾아서 단계별로 에러를 출력하는 문장
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay){
        FileOutputStream fos=null;

        try{
            fos=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            String content="";
            fos.write((content).getBytes());
            fos.close();

        }catch (Exception e){
            // 에러 메세지의 발생 근원지를 찾아서 단계별로 에러를 출력하는 문장
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay){
        FileOutputStream fos = null;
        // 파일을 입출력하는 파일 입출력용 스트림
        // 바이트 스트림으로 바이너리 데이터를 파일로부터 읽거나 그대로 저장하는 역할을 함

        try{
            fos=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            // 하위 디렉터리에 있는 응용프로그램 파일을 쓰기 모드로 열거나 생성
            String content=contextEditText.getText().toString();
            fos.write((content).getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}