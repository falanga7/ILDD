package it.unisa.ildd;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class WifiHolder extends RecyclerView.ViewHolder {

    public TextView getWifiView1() {
        return wifiView1;
    }

    public void setWifiView1(TextView wifiView1) {
        this.wifiView1 = wifiView1;
    }

    private TextView wifiView1;


    public WifiHolder(View v) {
        super(v);
        wifiView1 = (TextView) v.findViewById(R.id.itemView);


    }



}
