package it.unisa.ildd;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;


import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Menu menu;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private static final int PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1002;
    private Image image;
    private WifiManager w;
    private ArrayList<NetworkRecord> networkRecordArrayList;
    private static FileWriter file;
    private static ArrayList<ILDDData> detectedData;
    private static Bitmap imageBM;
    private static String filename;
    public static boolean zoomMode;
    private boolean inflated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zoomMode = false;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        }
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }

        File folder = new File(Environment.getExternalStorageDirectory() + "/ILDD");
        if (!folder.exists())
            folder.mkdir();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                onPrepareOptionsMenu(menu);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int item = mViewPager.getCurrentItem();
    try {
        if (item == 1)
            menu.setGroupVisible(R.id.main_menu_group, false);
        else
            menu.setGroupVisible(R.id.main_menu_group, true);

    }
    catch(NullPointerException ex){

    }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        ImageView img= (ImageView) findViewById(R.id.image_view);
        if(image != null) {
            imageBM = BitmapFactory.decodeFile(image.getPath());
        }
        SensorManager mSensorManager;
        LinearAcceleration linearAcceleration = new LinearAcceleration();
        OrientationAPR orientationAPR = new OrientationAPR();
        SensorMapListener sensorMapListener = new SensorMapListener(linearAcceleration, orientationAPR);
        mSensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        networkRecordArrayList = new ArrayList<>();
        w = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    if(networkRecordArrayList.size() != 0){
                        networkRecordArrayList.clear();
                    }
                    List<ScanResult> list = w.getScanResults();
                    w.startScan();
                    for (ScanResult sr : list) {
                        NetworkRecord nr = new NetworkRecord();
                        nr.setBssid(sr.BSSID.substring(0, sr.BSSID.length() - 2) + "xx");
                        nr.setSsid(sr.SSID);
                        nr.setRssi(Integer.toString(sr.level));
                        networkRecordArrayList.add(nr);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
        w.startScan();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_choose_map) {
            this.imagePicker();
        }
        if (id == R.id.action_zoom_mode && item.getTitle().equals(getResources().getString(R.string.action_zoom_mode_off))){
            item.setTitle(getResources().getString(R.string.action_zoom_mode_on));
            zoomMode = true;
        }
        else if (id == R.id.action_zoom_mode && item.getTitle().equals(getResources().getString(R.string.action_zoom_mode_on))){
            item.setTitle(getResources().getString(R.string.action_zoom_mode_off));
            zoomMode = false;
        }
        if (id == R.id.action_record && item.getTitle().equals("Record")){

            detectedData = new ArrayList<>();

            if(img.getDrawable().getConstantState() == getResources().getDrawable(android.R.drawable.ic_menu_gallery).getConstantState()){
                this.imagePicker();
                return true;
            }
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            String folderName = image.getName()+"_"+formattedDate;
            File folder = new File(Environment.getExternalStorageDirectory() + "/ILDD/"+folderName);
            if (!folder.exists())
                folder.mkdir();
            filename = Environment.getExternalStorageDirectory() + "/ILDD/"+folderName+"/data.csv";
            try {
                file = new FileWriter(filename);
                file.append(Integer.toString(imageBM.getWidth()));
                file.append(", ");
                file.append(Integer.toString(imageBM.getHeight()));
                file.append("\n");

            }
            catch (Exception e){

            }


            Sensor mSensorModelLA = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            Sensor mSensorModelO = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

            mSensorManager.registerListener(sensorMapListener, mSensorModelLA, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(sensorMapListener, mSensorModelO, SensorManager.SENSOR_DELAY_NORMAL);
            item.setTitle("Stop");
            Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
            ImageTouchListener itl= new ImageTouchListener(this.getApplicationContext(), bitmap, detectedData, linearAcceleration, orientationAPR, networkRecordArrayList);

            img.setOnTouchListener(itl);
        }
        else if(id == R.id.action_record && item.getTitle().equals("Stop"))
        {
            item.setTitle("Record");
            img.setOnTouchListener(null);
            img.setImageBitmap(imageBM);
            mSensorManager.unregisterListener(sensorMapListener);
            try {
                for(ILDDData data : detectedData) {
                    for (NetworkRecord nr : data.getNetworkRecordArrayList()) {
                        file.append(Long.toString(data.getCurrentMs()));
                        file.append(", ");
                        file.append(nr.getBssid());
                        file.append(", ");
                        file.append(nr.getSsid());
                        file.append(", ");
                        file.append(nr.getRssi());
                        file.append(", ");
                        file.append(Float.toString(data.getPosition().getX()));
                        file.append(", ");
                        file.append(Float.toString(data.getPosition().getY()));
                        file.append(", ");
                        file.append(Float.toString(data.getLinearAcceleration().getX()));
                        file.append(", ");
                        file.append(Float.toString(data.getLinearAcceleration().getY()));
                        file.append(", ");
                        file.append(Float.toString(data.getLinearAcceleration().getZ()));
                        file.append(", ");
                        file.append(Float.toString(data.getOrientationAPR().getAzimuth()));
                        file.append(", ");
                        file.append(Float.toString(data.getOrientationAPR().getPitch()));
                        file.append(", ");
                        file.append(Float.toString(data.getOrientationAPR().getRoll()));
                        file.append("\n");
                    }
                }

                file.close();
                MediaScannerConnection.scanFile(getApplicationContext(), new String[] { filename }, null, new MediaScannerConnection.OnScanCompletedListener() {

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        // TODO Auto-generated method stub

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        return super.onOptionsItemSelected(item);
    }
    public void imagePicker(){
        ImagePicker.create(this).folderMode(true).returnMode(ReturnMode.ALL).single()
                .toolbarFolderTitle("Choose your folder:").theme(R.style.ImagePickerTheme) // Activity or Fragment
                .toolbarImageTitle("Choose your map:").start();
    }

    public void checkIfMapExists(View v){
        if(((ImageView)v).getDrawable().getConstantState() == getResources().getDrawable(android.R.drawable.ic_menu_gallery).getConstantState()){
            this.imagePicker();
        }

    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            image = ImagePicker.getFirstImageOrNull(data);
            ImageView img= (ImageView) findViewById(R.id.image_view);

            img.setImageBitmap(BitmapFactory.decodeFile(image.getPath()));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0)
                return MapFragment.newInstance(position + 1);

            else
                return MonitorFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}
