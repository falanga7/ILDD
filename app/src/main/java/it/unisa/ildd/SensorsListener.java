package it.unisa.ildd;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.text.Html;
import android.text.Spanned;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static java.lang.Math.round;


public class SensorsListener implements SensorEventListener {
    private MyMonitorRecyclerViewAdapter adapter;

    public SensorsListener(MyMonitorRecyclerViewAdapter adapter){
            this.adapter = adapter;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            long time= System.currentTimeMillis();
            Spanned timeString = Html.fromHtml(Long.toString(time) + " ms");
            ((SensorModel) adapter.items.get(0)).setValues(timeString);
            String linearAcceleration = "<b> XB= </b>"  + Float.toString(round(event.values[0],1))+" m/s<sup>2</sup>\t <b>Y= </b>"+ Float.toString(round(event.values[1],1))+" m/s<sup>2</sup>\t <b>Z= </b>"+ Float.toString(round(event.values[2],1))+" m/s<sup>2</sup>";
            ((SensorModel) adapter.items.get(1)).setValues(Html.fromHtml(linearAcceleration));
            adapter.notifyDataSetChanged();

        }
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
            String orientation = "<b> Azimuth= </b>"  +Float.toString(round(event.values[0],1))+"\t <b>Pitch= </b>"+ Float.toString(round(event.values[1],1))+"\t <b>Roll= </b>"+ Float.toString(round(event.values[2],1));
            ((SensorModel) adapter.items.get(2)).setValues(Html.fromHtml(orientation));
            adapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
