package com.zensorium.android.demo1;

import android.content.Intent;
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
import com.zensorium.android.demo1.data.Sensor;
import com.zensorium.android.demo1.database.DatabaseHandler;
import com.zensorium.android.demo1.model.MonitorDataModel;

import java.util.Arrays;
import java.util.List;

public class SensorReceiverService extends WearableListenerService {
    private static final String TAG = "SensorDashboard/SensorReceiverService";

    private RemoteSensorManager sensorManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = RemoteSensorManager.getInstance(this);
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        Log.i(TAG, "Connected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.i(TAG, "Disconnected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged()");

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();

                if (path.startsWith("/sensors/")) {
                    unpackSensorData(
                        Integer.parseInt(uri.getLastPathSegment()),
                        DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }
            }
        }
    }

    private void unpackSensorData(int sensorType, DataMap dataMap) {
        int accuracy = dataMap.getInt(DataMapKeys.ACCURACY);
        long timestamp = dataMap.getLong(DataMapKeys.TIMESTAMP);
        float[] values = dataMap.getFloatArray(DataMapKeys.VALUES);
        Log.d(TAG, "Received sensor data " + sensorType + " = " + Arrays.toString(values) + " timestamp:" + timestamp);
        // Save to database
        DatabaseHandler db = new DatabaseHandler(this);
        db.addMonitorData(new MonitorDataModel(0, String.valueOf(sensorType), "anonymous", Arrays.toString(values), String.valueOf(timestamp), String.valueOf(accuracy)));
        // get all books
        List<MonitorDataModel> list = db.getAllUserMonitorData();
        Log.d(TAG,"Broadcast HR.");
        Intent intent = new Intent();
        if ((sensorType == 21) && (values[0] > 0)) {
            intent.setAction("com.example.Broadcast");
            intent.putExtra("HR", values);
            intent.putExtra("ACCR", accuracy);
            intent.putExtra("TIME", timestamp);
            sendBroadcast(intent);
        }
        sensorManager.addSensorData(sensorType, accuracy, timestamp, values);
    }
}
