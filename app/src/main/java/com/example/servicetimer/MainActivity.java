package com.example.servicetimer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;

    private TextView timerValue;
    private TextView timerValueMils;

    private long miliTime;

    private int beepTime = 30;

    private boolean running = false;
    private boolean beep = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        miliTime = 0L;
        timerValue = (TextView) findViewById(R.id.timerValue);
        timerValueMils = (TextView) findViewById(R.id.timerValueMils);

        registerReceiver(uiUpdated, new IntentFilter("TIMER_UPDATED"));

        startButton = (Button) findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (running){
                    return;
                }
                Intent i = new Intent(MainActivity.this,TimerService.class);
                i.putExtra("timer",miliTime);

                startService(i);
                running = true;
                resetButton.setVisibility(View.GONE);
            }
        });

        pauseButton = (Button) findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(!running){
                    return;
                }
                running = false;
                stopService(new Intent(MainActivity.this, TimerService.class));
                resetButton.setVisibility(View.VISIBLE);
            }
        });

        resetButton = (Button) findViewById(R.id.resetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, TimerService.class));
                running = false;
                miliTime = 0L;
                ((TextView) findViewById(R.id.timerValue)).setText(R.string.timerVal);
                ((TextView) findViewById(R.id.timerValueMils)).setText(R.string.timerValMils);
                beep = false;
            }
        });

        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.times,
                        android.R.layout.simple_spinner_item);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(1);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // On selecting a spinner item
                String label = parent.getItemAtPosition(position).toString();
                beepTime = Integer.parseInt(label);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                beepTime = 30;
            }
        });
    }

    private BroadcastReceiver uiUpdated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //This is the part where I get the timer value from the service and I update it every second, because I send the data from the service every second. The coundtdownTimer is a MenuItem
            try {
                miliTime = intent.getExtras().getLong("timer");
                long secs = miliTime/1000;
                int mins = (int) (secs/60);
                secs = secs % 60;
                if (secs > 0)
                    beep = true;

                if ((miliTime % (beepTime*1000) == 0) && beep)
                    beep();
                int millis = (int) (miliTime % 1000);

                timerValue.setText("" + mins + "  "
                        + String.format("%02d", secs));
                timerValueMils.setText(String.format("%02d", millis/10));
            }
            catch(RuntimeException e){
                e.printStackTrace();
            }
        }

        public void beep(){
            //final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.jobdone);
            mp.start();
            //tg.startTone(ToneGenerator.TONE_PROP_PROMPT,1000);
            //tg.release();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);

        }
    };
}
