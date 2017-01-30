package com.example.servicetimer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service
{
    private static Timer timer;
    private Context ctx;
    private static long miliTime = 0;

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        timer = new Timer();
        super.onCreate();
        ctx = this;
        miliTime = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            miliTime = intent.getExtras().getLong("timer");
            timer = new Timer();
            timer.scheduleAtFixedRate(new mainTask(), 0, 10);
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        return START_STICKY;
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            miliTime += 10;
            Intent i = new Intent("TIMER_UPDATED");
            i.putExtra("timer",miliTime);

            sendBroadcast(i);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        miliTime = 0L;
    }
}