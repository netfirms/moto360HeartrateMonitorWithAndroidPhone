package com.taweechai.android.demo1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.github.pocmo.sensordashboard.shared.DataMapKeys;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;
import com.taweechai.android.demo1.data.Sensor;
import com.taweechai.android.demo1.database.DatabaseHandler;
import com.taweechai.android.demo1.model.MonitorDataModel;

import java.util.Arrays;
import java.util.List;

public class SensorReceiverService extends WearableListenerService {
    private static final String TAG = "SensorDashboard/SensorReceiverService";

    private RemoteSensorManager sensorManager;
    long startTime;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = RemoteSensorManager.getInstance(this);
        SharedPreferences pref = getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
        startTime = pref.getLong("START_TIME", 0L);

        Log.d("Receiver", "Got START_TIMED: " + String.valueOf(startTime));
        registerReceiver(mMessageReceiver, new IntentFilter("com.example.Broadcast1"));
        Log.d("Receiver", "Got START_TIMEE: " + String.valueOf(startTime));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mMessageReceiver);
    }
    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.i(TAG, "Connected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
        Log.d("Receiver", "Got START_TIMEF: " + String.valueOf(startTime));
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.i(TAG, "Disconnected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
        Log.d("Receiver", "Got START_TIMEG: " + String.valueOf(startTime));
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged()");
        Log.d("Receiver", "Got START_TIMEH: " + String.valueOf(startTime));
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();
                Log.d("Receiver", "Got START_TIMEI: " + String.valueOf(startTime));
                if (path.startsWith("/sensors/")) {
                    Log.d("Receiver", "Got START_TIMEjJ: " + String.valueOf(startTime));
                    unpackSensorData(
                            Integer.parseInt(uri.getLastPathSegment()),
                            DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                    Log.d("Receiver", "Got START_TIMEK: " + String.valueOf(startTime));
                }
            }
        }
    }

    private void unpackSensorData(int sensorType, DataMap dataMap) {
        int accuracy = dataMap.getInt(DataMapKeys.ACCURACY);
        long timestamp = dataMap.getLong(DataMapKeys.TIMESTAMP);
        float[] values = dataMap.getFloatArray(DataMapKeys.VALUES);
        Log.d("Receiver", "Got START_TIMEC: " + String.valueOf(startTime));
        Log.d(TAG, "Received sensor data " + sensorType + " = " + Arrays.toString(values) + " timestamp:" + timestamp + " start time:" + startTime);
        // Save to database if Start time more than 0
        if (startTime > 0) {
            DatabaseHandler db = new DatabaseHandler(this);
            db.addMonitorData(new MonitorDataModel(0, String.valueOf(sensorType), "anonymous", Arrays.toString(values), String.valueOf(timestamp), String.valueOf(accuracy), String.valueOf(startTime)));
            // get all books
            List<MonitorDataModel> list = db.getAllUserMonitorData();
        }
        Log.d(TAG,"Broadcast Sensor Value.");
        //if ((sensorType == 21) && (values[0] > 0)) {
            Intent intent = new Intent();
            intent.setAction("com.example.Broadcast");
            intent.putExtra("HR", values);
            intent.putExtra("ACCR", accuracy);
            intent.putExtra("TIME", timestamp);
            intent.putExtra("SENSOR_TYPE", sensorType);
            sendBroadcast(intent);
        //}
        sensorManager.addSensorData(sensorType, accuracy, timestamp, values);
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            long message1 = intent.getLongExtra("START_TIME",0L);
            Log.d("Receiver", "Got START_TIME: " + String.valueOf(message1));
            startTime = message1;// set start time
            SharedPreferences pref = getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
            startTime = pref.getLong("START_TIME", 0L);
            Log.d("Receiver", "Got START_TIMEB: " + String.valueOf(startTime));
        }
    };
}
