package com.zensorium.android.demo1.events;


import com.zensorium.android.demo1.data.Sensor;
import com.zensorium.android.demo1.data.SensorDataPoint;

public class SensorUpdatedEvent {
    private Sensor sensor;
    private SensorDataPoint sensorDataPoint;

    public SensorUpdatedEvent(Sensor sensor, SensorDataPoint sensorDataPoint) {
        this.sensor = sensor;
        this.sensorDataPoint = sensorDataPoint;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public SensorDataPoint getDataPoint() {
        return sensorDataPoint;
    }
}
