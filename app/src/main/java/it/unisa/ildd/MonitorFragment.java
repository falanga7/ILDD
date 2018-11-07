package it.unisa.ildd;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.unisa.ildd.dummy.DummyContent.DummyItem;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MonitorFragment extends Fragment {


    private int mColumnCount = 3;
    private OnListFragmentInteractionListener mListener;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MonitorFragment() {
    }


    public static MonitorFragment newInstance() {
        MonitorFragment fragment = new MonitorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_list, container, false);
        View wifi_view = view.findViewById(R.id.wifi_view);
        View sensors_view = view.findViewById(R.id.sensors_view);
//
//        // Set the adapter
//
        Context context_wifi = wifi_view.getContext();
        RecyclerView recyclerView_wifi = (RecyclerView) wifi_view;
        recyclerView_wifi.setLayoutManager(new GridLayoutManager(context_wifi, mColumnCount));
        recyclerView_wifi.setHasFixedSize(true);

        List <Object> items = new ArrayList<>();
        items.add(getResources().getString(R.string.bssid));
        items.add(getResources().getString(R.string.ssid));
        items.add(getResources().getString(R.string.rssi));
        recyclerView_wifi.setAdapter(new MyMonitorRecyclerViewAdapter(items, context_wifi, true));
        RecyclerView.ItemDecoration dividerItemDecorationWifi = new DividerItemDecorator(ContextCompat.getDrawable(context_wifi, R.drawable.divider), true);
        recyclerView_wifi.addItemDecoration(dividerItemDecorationWifi);
        List <Object> sensors_data = new ArrayList<>();
        SensorModel sensorTime = new SensorModel();
        sensorTime.setHeader("Time:");
        sensorTime.setValues(null);
        SensorModel sensorLinearAccelerometer = new SensorModel();
        sensorLinearAccelerometer.setHeader("Linear Acceleration:");
        sensorLinearAccelerometer.setValues(null);
        SensorModel sensorOrientation = new SensorModel();
        sensorOrientation.setHeader("Orientation:");
        sensorOrientation.setValues(null);
        sensors_data.add(sensorTime);
        sensors_data.add(sensorLinearAccelerometer);
        sensors_data.add(sensorOrientation);
        Context context_sensors = sensors_view.getContext();
        RecyclerView recyclerView_sensors = (RecyclerView) sensors_view;
        recyclerView_sensors.setLayoutManager(new LinearLayoutManager(context_sensors));
        recyclerView_sensors.setHasFixedSize(true);
        recyclerView_sensors.setAdapter(new MyMonitorRecyclerViewAdapter(sensors_data, context_sensors, false));
        RecyclerView.ItemDecoration dividerItemDecorationSensors = new DividerItemDecorator(ContextCompat.getDrawable(context_wifi, R.drawable.divider), false);
        recyclerView_sensors.addItemDecoration(dividerItemDecorationSensors);
        recyclerView_sensors.setNestedScrollingEnabled(false);
        recyclerView_wifi.setNestedScrollingEnabled(false);
        return view;
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
