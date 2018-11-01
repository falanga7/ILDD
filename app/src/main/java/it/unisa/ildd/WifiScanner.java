package it.unisa.ildd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class WifiScanner {
    WifiManager w;
    MyMonitorRecyclerViewAdapter adapter;
    public WifiScanner(Context c, MyMonitorRecyclerViewAdapter a){
        w = (WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        adapter = a;
    }



    public void scanWifiNetworks(Context c){


        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        c.registerReceiver(wifiScanReceiver, intentFilter);
        w.startScan();

    }

    private void scanSuccess() {
        List<ScanResult> list = w.getScanResults();
        w.startScan();
        List<Object> header = new ArrayList<>();
        header.add(adapter.items.get(0));
        header.add(adapter.items.get(1));
        header.add(adapter.items.get(2));
        adapter.items.clear();
        adapter.items.addAll(header);

        for (ScanResult sr : list) {
            Bssid bssid = new Bssid();
            Ssid ssid = new Ssid();
            Rssi rssi = new Rssi();
            bssid.setBssid(sr.BSSID.substring(0, sr.BSSID.length() - 2) + "xx");
            ssid.setSsid(sr.SSID);
            rssi.setRssi(Integer.toString(sr.level));
            adapter.items.add(bssid);
            adapter.items.add(ssid);
            adapter.items.add(rssi);

        }
        adapter.notifyDataSetChanged();
    }
}
