package it.unisa.ildd;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SensorMapListener implements SensorEventListener {

    private LinearAcceleration linearAcceleration;
    private OrientationAPR orientationAPR;

    public SensorMapListener(LinearAcceleration la, OrientationAPR oAPR){
        this.linearAcceleration = la;
        this.orientationAPR = oAPR;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            linearAcceleration.setX(event.values[0]);
            linearAcceleration.setY(event.values[1]);
            linearAcceleration.setZ(event.values[2]);

        }
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
            orientationAPR.setAzimuth(event.values[0]);
            orientationAPR.setPitch(event.values[1]);
            orientationAPR.setRoll(event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
