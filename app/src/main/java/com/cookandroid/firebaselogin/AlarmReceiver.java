package com.cookandroid.firebaselogin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private Context context;
    private String channelId = "alarm_channel";
    String str2 = ((DiaryActivity)DiaryActivity.context_main).str;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;


        Intent bIntent = new Intent(context, DiaryActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(bIntent);
        PendingIntent bPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)

                // Notification 함수의 필수 요소 SmallIcon, ContentText, ContentTitle

                // 푸시 알림 시 앱의 아이콘을 정함(추후 앱의 아이콘으로 변경할 예정)
                .setSmallIcon(R.drawable.ic_launcher_background)
                // 소리 울리기 효과음에 관한 코드
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                // 푸시된 알람 클릭 시 자동으로 앱 접속 후 알람 삭제
                .setAutoCancel(true)
                // 알람의 제목 설정(
                .setContentTitle("에약된 알람")
                // 캘린더에 설정된 알람의 본문 텍스트 설정(작성한 메모를 받아와 알람하도록 변경 예정)
                .setContentText(str2)
                // Notification 실행 시 클릭할 경우 해당 액티비티로 넘어감
                .setContentIntent(bPendingIntent);

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // final 키워드는 지역변수를 상수화 시켜주는 키워드

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            // 현재 기기의 SDK 버전이 안드로이드 버전 26보다 높다면
            // 즉, 현재기기의 버전이 26보다 낮다면 실행 x(Oreo 버전(26, 27) 이후부터는 채널 생성을 꼭 해줘야 함)
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
            // 알림 채널 생성

            int id = (int)System.currentTimeMillis();

            // notificationManager notify 함수는 실제 사용자에게 UI를 그리도록 알려주는 역할
            notificationManager.notify(id, notificationBuilder.build());
        }

    }
}
