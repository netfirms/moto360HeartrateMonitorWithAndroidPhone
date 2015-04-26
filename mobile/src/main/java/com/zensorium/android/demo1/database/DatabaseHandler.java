package com.zensorium.android.demo1.database;

/**
 * Created by taweechai on 4/24/15 AD.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zensorium.android.demo1.model.MonitorDataModel;

import java.util.LinkedList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "monitorDB";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MONITOR_DATA_TABLE = "CREATE TABLE monitorData ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sensorUpdateTime TEXT, "+
                "sensorId TEXT, "+
                "userId TEXT, "+
                "accuracy TEXT, "+
                "sensorVal TEXT )";

        db.execSQL(CREATE_MONITOR_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS monitorDB");
        this.onCreate(db);
    }

    public void addMonitorData(MonitorDataModel monitorData){
        Log.d("addMonitorData", monitorData.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sensorUpdateTime", monitorData.getSensorUpdateTime());
        values.put("sensorId", monitorData.getSensorId());
        values.put("userId", monitorData.getUserId());
        values.put("sensorVal", monitorData.getSensorVal());
        values.put("accuracy", monitorData.getAccuracy());
        db.insert("monitorData", null, values);
        db.close();
    }

    public List<MonitorDataModel> getAllUserMonitorData() {
        List<MonitorDataModel> monitorDatas = new LinkedList<MonitorDataModel>();
        String query = "SELECT  * FROM " + "monitorData";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MonitorDataModel monitorData = null;
        if (cursor.moveToFirst()) {
            do {
                monitorData = new MonitorDataModel();
                monitorData.setId(Integer.parseInt(cursor.getString(0)));
                monitorData.setSensorUpdateTime(cursor.getString(1));
                monitorData.setSensorId(cursor.getString(2));
                monitorData.setUserId(cursor.getString(3));
                monitorData.setAccuracy(cursor.getString(4));
                monitorData.setSensorVal(cursor.getString(5));
                monitorDatas.add(monitorData);
            } while (cursor.moveToNext());
        }
        Log.d("getAllMonitorData()", monitorDatas.toString());
        return monitorDatas;
    }
}