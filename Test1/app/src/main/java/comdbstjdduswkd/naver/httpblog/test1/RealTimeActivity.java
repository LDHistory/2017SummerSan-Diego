package comdbstjdduswkd.naver.httpblog.test1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
/*
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.CO;
import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.NO2;
import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.O3;
import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.PM25;
import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.SO2;
import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.TEMP;


/**
 * Created by USER on 2017-07-14.
 */

public class RealTimeActivity extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    ///////////////////////////////////////////////////////////////////////////////////////
    //          To set the real time location, Declare this value                       //
    /////////////////////////////////////////////////////////////////////////////////////
    private static final LatLng DEFAULT_LOCATION = new LatLng(32.882415, -117.234817);
    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 15000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 15000;

    private GoogleApiClient googleApiClient = null;
    private Marker currentMarker = null;

    private final static int MAXENTRIES = 5;
    private String[] LikelyPlaceNames = null;
    private String[] LikelyAddresses = null;
    private String[] LikelyAttributions = null;
    private LatLng[] LikelyLatLngs = null;
    /////////////////////////////////////////////////////////////////////////////////////

    View view;
    private FragmentManager manager;

    static double latitude, longitude;

    ///////////////////////////////////////////////////////////////
    //To use another class method and value, Declare the instance//
    //////////////////////////////////////////////////////////////
    CO fragmentco;
    NO2 fragmentno2;
    O3 fragmento3;
    PM25 fragmentpm25;
    SO2 fragmentso2;
    TEMP fragmenttemp;

    MainActivity main;
    //////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////
    // To save the udoo board location, Declare the array//
    ////////////////////////////////////////////////////////
    String bluetoothname[];
    int count;
    double lat[], lgt[], d_no2[], d_so2[], d_o3[], d_co[], d_pm25[];
    LatLng otherLocation[];
    CircleOptions otherCircle[];
    ////////////////////////////////////////////////////////

    //To use the chart of the heart information...
    private LineChart hChart;

    private GoogleMap googleMap = null;
    private MapView mapView = null;
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //                                    Initialize the value                                     //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    TextView heartText, CO, NO2, SO2, O3, PM25, TEMP, maxhert, minheart, maxrr, minrr;
    ImageView heart, heartbit, coimage, no2image, so2image, o3image, pm25image, tempimage;
    GlideDrawableImageViewTarget heartTartget, heartBitget;
    ///////////////////////////////////////////////////////////////////////////////////////////////

    //Initialize the value of heart rate to use max and min value.
    int heartmax = 0;
    int heartmin = 999;
    int rrmax = 0;
    int rrmin = 999;

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        this.googleMap.clear();

        setmarker();

        if (location != null) {
            //Get the value of the current latitude
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            CircleOptions circle1KM = new CircleOptions().center(currentLocation) //Circle point
                    .radius(200)      // Radius unit : m
                    .strokeWidth(0f)
                    .fillColor(Color.parseColor("#110000ff")); //Background
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1));
            currentMarker = this.googleMap.addMarker(markerOptions);
            this.googleMap.addCircle(circle1KM);
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1));
        currentMarker = this.googleMap.addMarker(markerOptions);

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
    }

    //To see the sensor location, This method must be called.
    public void setmarker() {
        if (main.readlocation != null) {
            Log.i("readMessage", "" + main.readlocation);

            JSONObject jObject = null;
            try {
                jObject = new JSONObject(main.readlocation);

                //Count is the number of human who connect with the bluetooth
                JSONObject bluetoothcount = jObject.getJSONObject("count");
                count = bluetoothcount.getInt("count(*)");

                JSONArray location = jObject.getJSONArray("user_data");

                //Print mark and circle
                for (int j = 0; j < location.length(); j++) {
                    bluetoothname[j] = String.valueOf(location.getJSONObject(j).getString("MAC"));
                    lat[j] = location.getJSONObject(j).getDouble("latitude");
                    lgt[j] = location.getJSONObject(j).getDouble("longitude");
                    otherLocation[j] = new LatLng(lat[j], lgt[j]);
                    otherCircle[j] = new CircleOptions().center(otherLocation[j]).radius(200).strokeWidth(0f).fillColor(Color.parseColor("#5551F05D"));
                    this.googleMap.addCircle(otherCircle[j]);
                    d_co[j] = location.getJSONObject(j).getDouble("CO");
                    d_so2[j] = location.getJSONObject(j).getDouble("SO2");
                    d_no2[j] = location.getJSONObject(j).getDouble("NO2");
                    d_o3[j] = location.getJSONObject(j).getDouble("O3");
                    d_pm25[j] = location.getJSONObject(j).getDouble("PM25");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Repeat as many people as possible.
            for (int number = 0; number < count; number++) {
                // Make a option of mark
                MarkerOptions makerOptions = new MarkerOptions();
                makerOptions
                        .position(new LatLng(lat[number], lgt[number]))
                        .title(bluetoothname[number]); // Title name
                // Print mark
                this.googleMap.addMarker(makerOptions);
            }
        }
    }

    //This method is function which print he rr max and min value.
    public void setHeartrr(int rr){
        //set rr max, min
        if (rrmax < rr){
            rrmax = rr;
            maxrr.setText("" + rrmax);
        }
        if(rrmin >= rr){
            rrmin = rr;
            minrr.setText("" + rrmin);
        }
    }

    //This method is function which print he hr max and min value.
    public void setHeart(int hr) {
        try {
            heartText.setText("" + hr);
            heart.setImageResource(R.drawable.human_nomal2);

            //set heart max, min
            if (heartmax <= hr) {
                heartmax = hr;
                maxhert.setText("" + heartmax);
            }
            if (heartmin >= hr) {
                heartmin = hr;
                minheart.setText("" + heartmin);
            }

            //heartrate state image
            if (hr >= 100) {
                heart.setImageResource(R.drawable.human_fast2);
                Glide.with(this).load(R.raw.heart_fast).into(heartBitget);
            } else if (hr >= 60) {
                heart.setImageResource(R.drawable.human_nomal2);
                Glide.with(this).load(R.raw.heart_normal).into(heartBitget);
            } else {
                heart.setImageResource(R.drawable.human_slow2);
                Glide.with(this).load(R.raw.heart_normal).into(heartBitget);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addHEntry(Integer val, Integer rrVal) { //Set the heart data chart
        try {
            LineData data = hChart.getData();

            if (data != null) {

                ILineDataSet set = data.getDataSetByIndex(0);
                ILineDataSet setrr = data.getDataSetByIndex(1);

                if (set == null) {
                    set = createSetH();
                    setrr = createSetRR();
                    data.addDataSet(set);
                    data.addDataSet(setrr);
                }

                if (heartText != null) {
                    data.addEntry(new Entry(set.getEntryCount(), val), 0);
                    data.addEntry(new Entry(set.getEntryCount(), rrVal), 1);
                    data.notifyDataChanged();
                }

                // let the chart know it's data has changed
                hChart.notifyDataSetChanged();

                // limit the number of visible entries
                hChart.setVisibleXRangeMaximum(20);
                //mChart.setVisibleYRange(30, AxisDependency.LEFT);

                hChart.moveViewToX(data.getEntryCount());
            }
        } catch (Exception e) {
            Log.e("setHeart", "value error");
            e.printStackTrace();
        }
    }

    //To change the AQI image, then this method will be call.
    public void setAQI(JSONObject data) {
        try {
            //AQI index of CO value
            CO.setText(data.getString("CO"));
            if (Float.parseFloat(data.getString("CO")) >= 0 && Float.parseFloat(data.getString("CO")) <= 4.4)
                coimage.setImageResource(R.drawable.co_good);
            else if (Float.parseFloat(data.getString("CO")) >= 4.5 && Float.parseFloat(data.getString("CO")) <= 9.4)
                coimage.setImageResource(R.drawable.co_moderate);
            else if (Float.parseFloat(data.getString("CO")) >= 9.5 && Float.parseFloat(data.getString("CO")) <= 12.4)
                coimage.setImageResource(R.drawable.co_usg);
            else if (Float.parseFloat(data.getString("CO")) >= 12.5 && Float.parseFloat(data.getString("CO")) <= 15.4)
                coimage.setImageResource(R.drawable.co_unhealthy);
            else if (Float.parseFloat(data.getString("CO")) >= 15.5 && Float.parseFloat(data.getString("CO")) <= 30.4)
                coimage.setImageResource(R.drawable.co_vu);
            else if (Float.parseFloat(data.getString("CO")) >= 30.5)
                coimage.setImageResource(R.drawable.co_hazardous);

            //AQI index of NO2 value
            NO2.setText(data.getString("NO2"));
            if (Float.parseFloat(data.getString("NO2")) >= 0 && Float.parseFloat(data.getString("NO2")) <= 53)
                no2image.setImageResource(R.drawable.no2_good);
            else if (Float.parseFloat(data.getString("NO2")) >= 54 && Float.parseFloat(data.getString("NO2")) <= 100)
                no2image.setImageResource(R.drawable.no2_moderate);
            else if (Float.parseFloat(data.getString("NO2")) >= 101 && Float.parseFloat(data.getString("NO2")) <= 360)
                no2image.setImageResource(R.drawable.no2_usg);
            else if (Float.parseFloat(data.getString("NO2")) >= 361 && Float.parseFloat(data.getString("NO2")) <= 649)
                no2image.setImageResource(R.drawable.no2_unhealthy);
            else if (Float.parseFloat(data.getString("NO2")) >= 650 && Float.parseFloat(data.getString("NO2")) <= 1249)
                no2image.setImageResource(R.drawable.no2_vu);
            else if (Float.parseFloat(data.getString("NO2")) >= 1250)
                no2image.setImageResource(R.drawable.no2_hazardous);

            //AQI index of SO2 value
            SO2.setText(data.getString("SO2"));
            if (Float.parseFloat(data.getString("SO2")) >= 0 && Float.parseFloat(data.getString("SO2")) <= 35)
                so2image.setImageResource(R.drawable.so2_good);
            else if (Float.parseFloat(data.getString("SO2")) >= 36 && Float.parseFloat(data.getString("SO2")) <= 75)
                so2image.setImageResource(R.drawable.so2_moderate);
            else if (Float.parseFloat(data.getString("SO2")) >= 76 && Float.parseFloat(data.getString("SO2")) <= 185)
                so2image.setImageResource(R.drawable.so2_usg);
            else if (Float.parseFloat(data.getString("SO2")) >= 186 && Float.parseFloat(data.getString("SO2")) <= 304)
                so2image.setImageResource(R.drawable.so2_unhealthy);
            else if (Float.parseFloat(data.getString("SO2")) >= 305 && Float.parseFloat(data.getString("SO2")) <= 604)
                so2image.setImageResource(R.drawable.so2_vu);
            else if (Float.parseFloat(data.getString("SO2")) >= 605)
                so2image.setImageResource(R.drawable.so2_hazardous);

            //AQI index of O3 value
            O3.setText(data.getString("O3"));
            if (Float.parseFloat(data.getString("O3")) >= 0 && Float.parseFloat(data.getString("O3")) <= 54)
                o3image.setImageResource(R.drawable.o3_good);
            else if (Float.parseFloat(data.getString("O3")) >= 55 && Float.parseFloat(data.getString("O3")) <= 70)
                o3image.setImageResource(R.drawable.o3_moderate);
            else if (Float.parseFloat(data.getString("O3")) >= 71 && Float.parseFloat(data.getString("O3")) <= 85)
                o3image.setImageResource(R.drawable.o3_usg);
            else if (Float.parseFloat(data.getString("O3")) >= 86 && Float.parseFloat(data.getString("O3")) <= 105)
                o3image.setImageResource(R.drawable.o3_unhealthy);
            else if (Float.parseFloat(data.getString("O3")) >= 106 && Float.parseFloat(data.getString("O3")) <= 200)
                o3image.setImageResource(R.drawable.o3_vu);
            else if (Float.parseFloat(data.getString("O3")) >= 201)
                o3image.setImageResource(R.drawable.o3_hazardous);

            //AQI index of PM25 value
            PM25.setText(data.getString("PM25"));
            if (Float.parseFloat(data.getString("PM25")) >= 0 && Float.parseFloat(data.getString("PM25")) <= 12)
                pm25image.setImageResource(R.drawable.pm25_good);
            else if (Float.parseFloat(data.getString("PM25")) >= 12.1 && Float.parseFloat(data.getString("PM25")) <= 35.4)
                pm25image.setImageResource(R.drawable.pm25_moderate);
            else if (Float.parseFloat(data.getString("PM25")) >= 35.5 && Float.parseFloat(data.getString("PM25")) <= 55.4)
                pm25image.setImageResource(R.drawable.pm25_usg);
            else if (Float.parseFloat(data.getString("PM25")) >= 55.5 && Float.parseFloat(data.getString("PM25")) <= 150.4)
                pm25image.setImageResource(R.drawable.pm25_unhealthy);
            else if (Float.parseFloat(data.getString("PM25")) >= 150.5 && Float.parseFloat(data.getString("PM25")) <= 250.4)
                pm25image.setImageResource(R.drawable.pm25_vu);
            else if (Float.parseFloat(data.getString("PM25")) >= 250.5)
                pm25image.setImageResource(R.drawable.pm25_hazardous);

            TEMP.setText(data.getString("temp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.realtime_layout, container, false);
        manager = getFragmentManager();
        main = new MainActivity();

        fragmentco = new CO();
        fragmentno2 = new NO2();
        fragmento3 = new O3();
        fragmentpm25 = new PM25();
        fragmentso2 = new SO2();
        fragmenttemp = new TEMP();

        maxhert = (TextView) view.findViewById(R.id.HMValue);
        minheart = (TextView) view.findViewById(R.id.MHValue);
        maxrr = (TextView) view.findViewById(R.id.RMValue);
        minrr = (TextView) view.findViewById(R.id.MRValue);

        coimage = (ImageView) view.findViewById(R.id.coimage);
        no2image = (ImageView) view.findViewById(R.id.noimage);
        so2image = (ImageView) view.findViewById(R.id.so2image);
        o3image = (ImageView) view.findViewById(R.id.o3image);
        pm25image = (ImageView) view.findViewById(R.id.pm25image);
        tempimage = (ImageView) view.findViewById(R.id.tempimage);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.getMapAsync(this);

        bluetoothname = new String[50];
        lat = new double[50]; lgt = new double[50];
        d_co = new double[50]; d_no2 = new double[50]; d_o3 = new double[50];
        d_so2 = new double[50]; d_pm25 = new double[50];
        otherLocation = new LatLng[50];
        otherCircle = new CircleOptions[50];

        //Under the taphost
        TabHost tabHost = (TabHost) view.findViewById(R.id.tabHost2);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Map")
                .setContent(R.id.tab1).setIndicator(getString(R.string.tab1));
        tabHost.addTab(spec1);
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Graph")
                .setContent(R.id.tab2).setIndicator(getString(R.string.tab2));
        tabHost.addTab(spec2);

        //Top the tabHost
        TabHost tabHost1 = (TabHost) view.findViewById(R.id.tabHost1);
        tabHost1.setup();

        TabHost.TabSpec spec3 = tabHost1.newTabSpec("Instantanelus Data")
                .setContent(R.id.tab3).setIndicator("Instantanelus Data");
        tabHost1.addTab(spec3);
        TabHost.TabSpec spec5 = tabHost1.newTabSpec("Standard AQI")
                .setContent(R.id.tab5).setIndicator("Standard AQI");
        tabHost1.addTab(spec5);
        TabHost.TabSpec spec4 = tabHost1.newTabSpec("Heart")
                .setContent(R.id.tab4).setIndicator("Heart");
        tabHost1.addTab(spec4);

        heartText = (TextView) view.findViewById(R.id.heartValue);
        heart = (ImageView) view.findViewById(R.id.heart);
        heartbit = (ImageView) view.findViewById(R.id.heartbit);
        //GIF File Object
        heartTartget = new GlideDrawableImageViewTarget(heart);
        heartBitget = new GlideDrawableImageViewTarget(heartbit);

        heart.setImageResource(R.drawable.human_nomal2);
        Glide.with(this).load(R.raw.heart_stop).into(heartBitget);

        /////////////////////////////////////////////////////////////////////////
        //  if user click the each air data text, then change the text color.  //
        ////////////////////////////////////////////////////////////////////////
        CO = (TextView) view.findViewById(R.id.co_text);
        CO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CO.setTextColor(Color.RED);
                NO2.setTextColor(Color.BLACK);
                O3.setTextColor(Color.BLACK);
                PM25.setTextColor(Color.BLACK);
                SO2.setTextColor(Color.BLACK);
                TEMP.setTextColor(Color.BLACK);
                paintCircle_CO();
                changeFragment(0);

            }
        });

        NO2 = (TextView) view.findViewById(R.id.no2_text);
        NO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CO.setTextColor(Color.BLACK);
                NO2.setTextColor(Color.RED);
                O3.setTextColor(Color.BLACK);
                PM25.setTextColor(Color.BLACK);
                SO2.setTextColor(Color.BLACK);
                TEMP.setTextColor(Color.BLACK);
                paintCircle_NO2();
                changeFragment(1);
            }
        });

        O3 = (TextView) view.findViewById(R.id.o3_text);
        O3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CO.setTextColor(Color.BLACK);
                NO2.setTextColor(Color.BLACK);
                O3.setTextColor(Color.RED);
                PM25.setTextColor(Color.BLACK);
                SO2.setTextColor(Color.BLACK);
                TEMP.setTextColor(Color.BLACK);
                paintCircle_O3();
                changeFragment(2);
            }
        });

        PM25 = (TextView) view.findViewById(R.id.pm25);
        PM25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CO.setTextColor(Color.BLACK);
                NO2.setTextColor(Color.BLACK);
                O3.setTextColor(Color.BLACK);
                PM25.setTextColor(Color.RED);
                SO2.setTextColor(Color.BLACK);
                TEMP.setTextColor(Color.BLACK);
                paintCircle_PM25();
                changeFragment(3);
            }
        });

        SO2 = (TextView) view.findViewById(R.id.so2_text);
        SO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CO.setTextColor(Color.BLACK);
                NO2.setTextColor(Color.BLACK);
                O3.setTextColor(Color.BLACK);
                PM25.setTextColor(Color.BLACK);
                SO2.setTextColor(Color.RED);
                TEMP.setTextColor(Color.BLACK);
                paintCircle_SO2();
                changeFragment(4);
            }
        });

        TEMP = (TextView) view.findViewById(R.id.temp_text);
        TEMP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CO.setTextColor(Color.BLACK);
                NO2.setTextColor(Color.BLACK);
                O3.setTextColor(Color.BLACK);
                PM25.setTextColor(Color.BLACK);
                SO2.setTextColor(Color.BLACK);
                TEMP.setTextColor(Color.RED);
                changeFragment(5);
            }
        });
        ////////////////////////////////////////////////////////////////////////

        hChart = (LineChart) view.findViewById(R.id.hart_chart);

        // enable description text
        hChart.getDescription().setEnabled(true);

        // enable touch gestures
        hChart.setTouchEnabled(true);

        // enable scaling and dragging
        hChart.setDragEnabled(true);
        hChart.setScaleEnabled(true);
        hChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        hChart.setPinchZoom(true);

        // set an alternative background color
        hChart.setBackgroundColor(Color.LTGRAY);

        LineData datah = new LineData();
        datah.setValueTextColor(Color.BLACK);
        //add empty data
        hChart.setData(datah);
        // get the legend (only possible after setting data)
        Legend lh = hChart.getLegend();

        // modify the legend ...
        lh.setForm(Legend.LegendForm.LINE);
        lh.setTextColor(Color.WHITE);

        //heart X
        XAxis xlh = hChart.getXAxis();
        xlh.setTextColor(Color.WHITE);
        xlh.setDrawGridLines(false);
        xlh.setAvoidFirstLastClipping(true);
        xlh.setEnabled(true);

        //heart Y
        YAxis leftAxish = hChart.getAxisLeft();
        leftAxish.setTextColor(Color.WHITE);
        leftAxish.setAxisMaximum(130f);
        leftAxish.setAxisMinimum(0f);
        leftAxish.setDrawGridLines(true);

        YAxis rightAxish = hChart.getAxisRight();
        rightAxish.setEnabled(false);

        //Fragment initialization
        changeFragment(5);
        changeFragment(4);
        changeFragment(3);
        changeFragment(2);
        changeFragment(1);
        changeFragment(0);
        return view;
    }

    private void paintCircle_CO() {
        for(int i=0;i<count-1;i++){
            if(0<=d_co[i] && d_co[i]<4.5)
                otherCircle[i] = new CircleOptions().center(otherLocation[i]).radius(200).strokeWidth(0f).fillColor(Color.parseColor("#5551F05D"));
                //otherCircle[j] = new CircleOptions().center(otherLocation[j]).radius(200).strokeWidth(0f).fillColor(Color.parseColor("#5551F05D"));
                //otherCircle[i].fillColor(Color.parseColor("#51F05D"));
            else if(4.5<=d_co[i] && d_co[i]<9.5)
                otherCircle[i].fillColor(Color.parseColor("#EBF458"));
            else if(9.5<=d_co[i] && d_co[i]<12.5)
                otherCircle[i].fillColor(Color.parseColor("#F4BC57"));
            else if(12.5<=d_co[i] && d_co[i]<15.5)
                otherCircle[i].fillColor(Color.parseColor("#FE5656"));
            else if(15.5<=d_co[i] && d_co[i]<30.5)
                otherCircle[i].fillColor(Color.parseColor("#F45BDD"));
            else
                otherCircle[i].fillColor(Color.parseColor("#F4578A"));
        }
    }

    private void paintCircle_NO2() {
        for(int i=0;i<count-1;i++){
            if(0<=d_no2[i] && d_no2[i]<54)
                otherCircle[i] = new CircleOptions().center(otherLocation[i]).radius(200).strokeWidth(0f).fillColor(Color.parseColor("#5551F05D"));
                //otherCircle[i].fillColor(Color.parseColor("#51F05D"));
            else if(54<=d_no2[i] && d_no2[i]<101)
                otherCircle[i] = new CircleOptions().center(otherLocation[i]).radius(200).strokeWidth(0f).fillColor(Color.parseColor("#55EBF458"));
                //otherCircle[i].fillColor(Color.parseColor("#EBF458"));
            else if(101<=d_no2[i] && d_no2[i]<361)
                otherCircle[i] = new CircleOptions().center(otherLocation[i]).radius(200).strokeWidth(0f).fillColor(Color.parseColor("#55F4BC57"));
                //otherCircle[i].fillColor(Color.parseColor("#F4BC57"));
            else if(361<=d_no2[i] && d_no2[i]<650)
                otherCircle[i] = new CircleOptions().center(otherLocation[i]).radius(200).strokeWidth(0f).fillColor(Color.parseColor("#55FE5656"));
                //otherCircle[i].fillColor(Color.parseColor("#FE5656"));
            else if(650<=d_no2[i] && d_no2[i]<1250)
                otherCircle[i] = new CircleOptions().center(otherLocation[i]).radius(200).strokeWidth(0f).fillColor(Color.parseColor("#55F45BDD"));
                //otherCircle[i].fillColor(Color.parseColor("#F45BDD"));
            else
                otherCircle[i] = new CircleOptions().center(otherLocation[i]).radius(200).strokeWidth(0f).fillColor(Color.parseColor("#55F4578A"));
                //otherCircle[i].fillColor(Color.parseColor("#F4578A"));
        }
    }

    private void paintCircle_SO2() {
        for(int i=0;i<count-1;i++){
            if(0<=d_so2[i] && d_so2[i]<36)
                otherCircle[i].fillColor(Color.parseColor("#51F05D"));
            else if(36<=d_so2[i] && d_so2[i]<76)
                otherCircle[i].fillColor(Color.parseColor("#EBF458"));
            else if(76<=d_so2[i] && d_so2[i]<186)
                otherCircle[i].fillColor(Color.parseColor("#F4BC57"));
            else if(186<=d_so2[i] && d_so2[i]<305)
                otherCircle[i].fillColor(Color.parseColor("#FE5656"));
            else if(305<=d_so2[i] && d_so2[i]<605)
                otherCircle[i].fillColor(Color.parseColor("#F45BDD"));
            else
                otherCircle[i].fillColor(Color.parseColor("#F4578A"));
        }
    }

    private void paintCircle_O3() {
        for(int i=0;i<count-1;i++){
            if(0<=d_o3[i] && d_o3[i]<55)
                otherCircle[i].fillColor(Color.parseColor("#51F05D"));
            else if(55<=d_o3[i] && d_o3[i]<71)
                otherCircle[i].fillColor(Color.parseColor("#EBF458"));
            else if(71<=d_o3[i] && d_o3[i]<86)
                otherCircle[i].fillColor(Color.parseColor("#F4BC57"));
            else if(86<=d_o3[i] && d_o3[i]<106)
                otherCircle[i].fillColor(Color.parseColor("#FE5656"));
            else if(106<=d_o3[i] && d_o3[i]<201)
                otherCircle[i].fillColor(Color.parseColor("#F45BDD"));
            else
                otherCircle[i].fillColor(Color.parseColor("#F4578A"));
        }
    }

    private void paintCircle_PM25() {
        for(int i=0;i<count-1;i++){
            if(0<=d_o3[i] && d_o3[i]<12)
                otherCircle[i].fillColor(Color.parseColor("#51F05D"));
            else if(55<=d_o3[i] && d_o3[i]<35.5)
                otherCircle[i].fillColor(Color.parseColor("#EBF458"));
            else if(71<=d_o3[i] && d_o3[i]<55.5)
                otherCircle[i].fillColor(Color.parseColor("#F4BC57"));
            else if(86<=d_o3[i] && d_o3[i]<150.5)
                otherCircle[i].fillColor(Color.parseColor("#FE5656"));
            else if(106<=d_o3[i] && d_o3[i]<250.5)
                otherCircle[i].fillColor(Color.parseColor("#F45BDD"));
            else
                otherCircle[i].fillColor(Color.parseColor("#F4578A"));
        }
    }
    //sensor LineDateSet
    private LineDataSet createSetH() {
        LineDataSet set = new LineDataSet(null, "HeartRate Data");
        set.setAxisDependency(AxisDependency.LEFT);
        set.setColor(Color.RED);
        set.setCircleColor(Color.RED);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.RED);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(12f);
        set.setDrawValues(false);
        return set;
    }
    private LineDataSet createSetRR() {
        LineDataSet set = new LineDataSet(null, "Rest Heart Rate Data");
        set.setAxisDependency(AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(12f);
        set.setDrawValues(false);
        return set;
    }
    public void changeFragment(int fNum) {
        switch (fNum) {
            case 0:
                if (manager.findFragmentByTag("co") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("co")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.tab2, fragmentco, "co").commit();
                }
                if (manager.findFragmentByTag("no2") != null && manager.findFragmentByTag("o3") != null
                        && manager.findFragmentByTag("pm25") != null && manager.findFragmentByTag("so2") != null
                        && manager.findFragmentByTag("temp") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("no2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("o3")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("pm25")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("so2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("temp")).commit();
                }
                break;
            case 1:
                if (manager.findFragmentByTag("no2") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("no2")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.tab2, fragmentno2, "no2").commit();
                }
                if (manager.findFragmentByTag("co") != null && manager.findFragmentByTag("o3") != null
                        && manager.findFragmentByTag("pm25") != null && manager.findFragmentByTag("so2") != null
                        && manager.findFragmentByTag("temp") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("co")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("o3")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("pm25")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("so2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("temp")).commit();
                }
                break;
            case 2:
                if (manager.findFragmentByTag("o3") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("o3")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.tab2, fragmento3, "o3").commit();
                }
                if (manager.findFragmentByTag("co") != null && manager.findFragmentByTag("no2") != null
                        && manager.findFragmentByTag("pm25") != null && manager.findFragmentByTag("so2") != null
                        && manager.findFragmentByTag("temp") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("co")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("no2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("pm25")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("so2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("temp")).commit();
                }
                break;
            case 3:
                if (manager.findFragmentByTag("pm25") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("pm25")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.tab2, fragmentpm25, "pm25").commit();
                }
                if (manager.findFragmentByTag("co") != null && manager.findFragmentByTag("no2") != null
                        && manager.findFragmentByTag("o3") != null && manager.findFragmentByTag("so2") != null
                        && manager.findFragmentByTag("temp") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("co")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("no2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("o3")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("so2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("temp")).commit();
                }
                break;
            case 4:
                if (manager.findFragmentByTag("so2") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("so2")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.tab2, fragmentso2, "so2").commit();
                }
                if (manager.findFragmentByTag("co") != null && manager.findFragmentByTag("no2") != null
                        && manager.findFragmentByTag("o3") != null && manager.findFragmentByTag("pm25") != null
                        && manager.findFragmentByTag("temp") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("co")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("no2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("o3")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("pm25")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("temp")).commit();
                }
                break;
            case 5:
                if (manager.findFragmentByTag("temp") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("temp")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.tab2, fragmenttemp, "temp").commit();
                }
                if (manager.findFragmentByTag("co") != null && manager.findFragmentByTag("no2") != null
                        && manager.findFragmentByTag("o3") != null && manager.findFragmentByTag("pm25") != null
                        && manager.findFragmentByTag("so2") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("co")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("no2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("o3")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("pm25")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("so2")).commit();
                }
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if (googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mapView.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (googleApiClient != null)
            googleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity().getApplicationContext());

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // OnMapReadyCallback implements 해야 mapView.getMapAsync(this); 사용가능. this 가 OnMapReadyCallback

        this.googleMap = googleMap;

        setCurrentLocation(null, "Unable to get location info", "Check location permissions and GPS activation");

        //Make compass appear
        googleMap.getUiSettings().setCompassEnabled(true);
        //Moved smoothly
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Requires runtime permission processing if API 23 or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Check permissions
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                //If you do not have permissions
                //Authorization again
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                //If you have permission
                if (googleApiClient == null) {
                    buildGoogleApiClient();
                }

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            }
        } else {

            if (googleApiClient == null) {
                buildGoogleApiClient();
            }

            googleMap.setMyLocationEnabled(true);
        }


    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage((FragmentActivity) getActivity(), this)
                .build();
        googleApiClient.connect();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!checkLocationServicesStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Disable Location Services");
            builder.setMessage("Location services are required to use the app.\n" +
                    "Please correct your location settings.");
            builder.setCancelable(true);
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent callGPSSettingIntent =
                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi
                        .requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        } else {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(googleApiClient, locationRequest, this);

            this.googleMap.getUiSettings().setCompassEnabled(true);
            this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Location location = new Location("");
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude((DEFAULT_LOCATION.longitude));

        setCurrentLocation(location, "Unable to get location info",
                "Check location permissions and GPS activation");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged call..");
        searchCurrentPlaces();
    }

    private void searchCurrentPlaces() {
        @SuppressWarnings("MissingPermission")
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(googleApiClient, null);
        try {
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {

                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                    int i = 0;
                    LikelyPlaceNames = new String[MAXENTRIES];
                    LikelyAddresses = new String[MAXENTRIES];
                    LikelyAttributions = new String[MAXENTRIES];
                    LikelyLatLngs = new LatLng[MAXENTRIES];

                    for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                        LikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                        LikelyAddresses[i] = (String) placeLikelihood.getPlace().getAddress();
                        LikelyAttributions[i] = (String) placeLikelihood.getPlace().getAttributions();
                        LikelyLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                        i++;
                        if (i > MAXENTRIES - 1) {
                            break;
                        }
                    }

                    placeLikelihoods.release();

                    Location location = new Location("");
                    location.setLatitude(LikelyLatLngs[0].latitude);
                    location.setLongitude(LikelyLatLngs[0].longitude);

                    //To send the user location, Save the user location in the value
                    latitude = LikelyLatLngs[0].latitude;
                    longitude = LikelyLatLngs[0].longitude;

                    setCurrentLocation(location, LikelyPlaceNames[0], LikelyAddresses[0]);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();

        if (googleApiClient != null) {
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);

            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                googleApiClient.disconnect();
            }
        }
    }
}
