package com.wireddevs.dementassist.Alarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.wireddevs.dementassist.R;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private long[] vibrate={200,500,200,500,200,500,200,500,200,500,200,500,200,500};
    MediaPlayer mp = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        SharedPreferences alarmtype= context.getSharedPreferences("alarmtype",Context.MODE_PRIVATE);


        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        try{
            int requestCode = 0;
            AlarmHelper ah=new AlarmHelper(context);
            requestCode=intent.getExtras().getInt("requestCode");
            Alarm a=ah.getAlarm(requestCode);
            String sname=a.getName();
            long updatetime=0;

            if(a.getDaysinweek().charAt(day-1)=='1'){
                switch(alarmtype.getInt("alarmtype",0)){
                    case 0:
                        v.vibrate(vibrate,-1);
                        break;
                    case 1:
                        v.vibrate(vibrate,-1);
                        setUpMP(context);
                        mp.start();
                        break;
                    case 2:
                        setUpMP(context);
                        mp.start();
                        break;
                    default:
                        break;
                }

                NotificationManager alarmNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "Name";
                    String description = "Desc";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(String.valueOf(a.getCode()), name, importance);
                    channel.setDescription(description);
                    alarmNotificationManager.createNotificationChannel(channel);
                }

                Intent sintent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, a.getCode(), sintent, PendingIntent.FLAG_ONE_SHOT);

                String msg="Alarm for: "+sname+", swipe to dismiss";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, String.valueOf(a.getCode()))
                        .setSmallIcon(a.getLogo())
                        .setContentTitle("DementAssist: "+sname)
                        .setContentText(msg)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setLights(Color.RED, 3000, 3000)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setAutoCancel(false);

                alarmNotificationManager.notify(a.getCode(), builder.build());


                updatetime=Calendar.getInstance().getTimeInMillis()+a.getInterval();
                AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                PendingIntent pIntent = PendingIntent.getBroadcast(context,a.getCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= 23) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC, updatetime, pIntent);
                }
                else {
                    am.setExact(AlarmManager.RTC,updatetime,pIntent);
                }
                ah.updateTime(sname,updatetime);
            }
            else{
                Toast.makeText(context, "Alarm will not ring", Toast.LENGTH_SHORT).show();
                Log.v("AlarmReceiver","ALARM WILL NOT RING");
                updatetime=calendar.getTimeInMillis()+1000*60*60*24;
                AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                PendingIntent pIntent = PendingIntent.getBroadcast(context,a.getCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= 23) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC, updatetime, pIntent);
                }
                else {
                    am.setExact(AlarmManager.RTC,updatetime,pIntent);
                }
                ah.updateTime(sname,updatetime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpMP(Context context){
        SharedPreferences alarmsound= context.getSharedPreferences("alarmsound",Context.MODE_PRIVATE);
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp=null;
        }
        else {
            mp = null;
        }
        mp = MediaPlayer.create(context, alarmsound.getInt("alarmsound",Context.MODE_PRIVATE));
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!mp.isPlaying()) {
                    mp.release();
                }
                else {
                    mp.stop();
                    mp.release();
                }

            }
        });
    }
}
