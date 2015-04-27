package com.taweechai.android.demo1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private TextView timeStampTxt;
    private TextView hrTxt;
    private Button measureBtn;
    boolean toggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                timeStampTxt = (TextView) stub.findViewById(R.id.timeStampTxt);
                hrTxt = (TextView) stub.findViewById(R.id.hrTxt);
                /*
                measureBtn = (Button) stub.findViewById(R.id.measureBtn);
                measureBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!toggle) {
                            startService(new Intent(getApplicationContext(), SensorService.class));
                            toggle = true;
                        } else {
                            stopService(new Intent(getApplicationContext(), SensorService.class));
                            toggle = false;
                        }

                    }
                });*/
            }
        });
        this.registerReceiver(mMessageReceiver, new IntentFilter("com.example.Broadcast"));
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            float[] message1 = intent.getFloatArrayExtra("HR");
            int message2 = intent.getIntExtra("ACCR", 0);
            Log.d("Receiver", "Got message1: " + message1[0] + "Got message2: " + message2);
            int tmpHr = (int)Math.ceil(message1[0] - 0.5f);
            if (tmpHr > 0) {
                long timeStamp = intent.getLongExtra("TIME", 0)/1000000L;
                hrTxt.setText(String.valueOf(tmpHr));
                timeStampTxt.setText(String.valueOf(getDate(timeStamp)));
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Register mMessageReceiver to receive messages.
        this.registerReceiver(mMessageReceiver, new IntentFilter("com.example.Broadcast"));
    }


    private String getDate(long timeStamp){

        try{
            DateFormat sdf = new SimpleDateFormat("HH:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "7:00";
        }
    }

}
