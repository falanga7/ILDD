package it.unisa.ildd;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.unisa.ildd.MonitorFragment.OnListFragmentInteractionListener;



/**
 * {@link RecyclerView.Adapter} that can display every kind of item and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyMonitorRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Object> items;
    private final int HEADER = 0, NETWORK = 1, SENSORS = 2;
    private SensorManager mSensorManager;
    private Sensor mSensorModelLA;
    private Sensor mSensorModelO;


    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return HEADER;
        }
        else if ((items.get(position) instanceof Bssid) || (items.get(position) instanceof Ssid )
                  || (items.get(position) instanceof Rssi)) {
            return NETWORK;
        }
        else
            return SENSORS;
    }

    public MyMonitorRecyclerViewAdapter(List<Object> myDataset, Context context, boolean wifi) {
        items = myDataset;
        if(wifi)
           this.getWifiData(context);
        else
            this.getSensorData(context);
    }

    private void getSensorData(Context context){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorModelLA = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorModelO = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(new SensorsListener(this), mSensorModelLA, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(new SensorsListener(this), mSensorModelO, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void getWifiData(Context context){
        WifiScanner w = new WifiScanner(context, this);
        w.scanWifiNetworks(context);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case HEADER:
            default:
                View header = inflater.inflate(R.layout.header_view, parent, false);
                viewHolder = new HeaderHolder(header);
                break;
            case NETWORK:
                View v2 = inflater.inflate(R.layout.wifi_view, parent, false);
                viewHolder = new WifiHolder(v2);
                break;
            case SENSORS:
                View sensor_view = inflater.inflate(R.layout.sensors_view, parent, false);
                viewHolder = new SensorsHolder(sensor_view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        switch (holder.getItemViewType()) {
            case HEADER:
                HeaderHolder hh = (HeaderHolder) holder;
                String h = (String) items.get(position);

                hh.getHeaderView1().setText(h);

                break;
            case NETWORK:
                WifiHolder wh = (WifiHolder) holder;
                if ( items.get(position) instanceof Bssid) {
                    wh.getWifiView1().setText(((Bssid) items.get(position)).getBssid());
                }
                else if (items.get(position) instanceof Ssid){
                    wh.getWifiView1().setText(((Ssid) items.get(position)).getSsid());
                }
                else if (items.get(position) instanceof Rssi){
                    wh.getWifiView1().setText(((Rssi) items.get(position)).getRssi());

                }
                break;
            case SENSORS:
                SensorsHolder sh = (SensorsHolder) holder;
                sh.getHeader().setText(((SensorModel) items.get(position)).getHeader());
                sh.getValues().setText(((SensorModel) items.get(position)).getValues());



        }
    }

        // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }


}
