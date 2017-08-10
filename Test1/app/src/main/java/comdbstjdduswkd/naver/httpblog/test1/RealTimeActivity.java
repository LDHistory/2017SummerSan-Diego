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

    View view;
    private FragmentManager manager;

    static double latitude, longitude;

    CO fragmentco;
    NO2 fragmentno2;
    O3 fragmento3;
    PM25 fragmentpm25;
    SO2 fragmentso2;
    TEMP fragmenttemp;

    MainActivity main;

    ArrayList<String> ArrList;
    String bluetoothname[];
    int count;
    double lat[], lgt[];

    private LineChart coChart, hChart;

    private GoogleMap googleMap = null;
    private MapView mapView = null;

    TextView heartText, CO, NO2, SO2, O3, PM25, TEMP, maxhert, minheart;
    ImageView heart, heartbit, coimage, no2image, so2image, o3image, pm25image, tempimage;
    GlideDrawableImageViewTarget heartTartget, heartBitget;

    int max = 0;
    int min = 999;

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        this.googleMap.clear();

        setmarker();

        if (location != null) {
            //현재위치의 위도 경도 가져옴
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            CircleOptions circle1KM = new CircleOptions().center(currentLocation) //원점
                    .radius(200)      //반지름 단위 : m
                    .strokeWidth(0f)  //선너비 0f : 선없음
                    .fillColor(Color.parseColor("#110000ff")); //배경색
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
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1));
        currentMarker = this.googleMap.addMarker(markerOptions);

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
    }

    public void setmarker() {
        if (main.readlocation != null) {
            Log.i("readMessage", "" + main.readlocation);

            ArrList = new ArrayList<String>();               //어레이 리스트 선언
            bluetoothname = new String[50];
            lat = new double[50];
            lgt = new double[50];

            JSONObject jObject = null;               //JSONData 문자열
            try {
                jObject = new JSONObject(main.readlocation);

                JSONObject bluetoothcount = jObject.getJSONObject("count");
                count = bluetoothcount.getInt("count(*)");

                JSONArray location = jObject.getJSONArray("user_data");
                for (int i = 0; i < location.length(); i++) {
                    ArrList.add(location.getJSONObject(i).getString("lname"));
                    ArrList.add(location.getJSONObject(i).getString("latitude"));
                    ArrList.add(location.getJSONObject(i).getString("longitude"));

                }
                for (int j = 0; j < location.length(); j++) {
                    Log.i("read", "" + String.valueOf(location.getJSONObject(j).getString("lname")));
                    bluetoothname[j] = String.valueOf(location.getJSONObject(j).getString("lname"));
                    Log.i("read", "" + String.valueOf(location.getJSONObject(j).getDouble("latitude")));
                    lat[j] = location.getJSONObject(j).getDouble("latitude");
                    Log.i("read", "" + String.valueOf(location.getJSONObject(j).getDouble("longitude")));
                    lgt[j] = location.getJSONObject(j).getDouble("longitude");
                }
                Log.i("read", "" + count);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int number = 0; number < count; number++) {
                // 1. 마커 옵션 설정 (만드는 과정)
                MarkerOptions makerOptions = new MarkerOptions();
                makerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
                        .position(new LatLng(lat[number], lgt[number]))
                        .title(bluetoothname[number]); // 타이틀.
                // 2. 마커 생성 (마커를 나타냄)
                this.googleMap.addMarker(makerOptions);
            }
        }
    }

    public void setHeart(int hr) {
        try {
            heartText.setText("" + hr);
            heart.setImageResource(R.drawable.human_nomal2);

            //set max, min
            if (max <= hr) {
                max = hr;
                maxhert.setText("" + max);
            }
            if (min >= hr) {
                min = hr;
                minheart.setText("" + min);
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

    public void addHEntry(Integer val) { //Set the heart data chart
        try {
            LineData data = hChart.getData();

            if (data != null) {

                ILineDataSet set = data.getDataSetByIndex(0);
                // set.addEntry(...); // can be called as well

                if (set == null) {
                    set = createSetH();
                    data.addDataSet(set);
                }

                //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
                if (heartText != null) {
                    data.addEntry(new Entry(set.getEntryCount(), val), 0);
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

    public void setAQI(JSONObject data) {
        try {
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

        coimage = (ImageView) view.findViewById(R.id.coimage);
        no2image = (ImageView) view.findViewById(R.id.noimage);
        so2image = (ImageView) view.findViewById(R.id.so2image);
        o3image = (ImageView) view.findViewById(R.id.o3image);
        pm25image = (ImageView) view.findViewById(R.id.pm25image);
        tempimage = (ImageView) view.findViewById(R.id.tempimage);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.getMapAsync(this);
        /* 자동완성기능 보류
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Location location = new Location("");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);

                setCurrentLocation(location, place.getName().toString(), place.getAddress().toString());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
*/
        //하단 TabHost
        TabHost tabHost = (TabHost) view.findViewById(R.id.tabHost2);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Map")
                .setContent(R.id.tab1).setIndicator(getString(R.string.tab1));
        tabHost.addTab(spec1);
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Graph")
                .setContent(R.id.tab2).setIndicator(getString(R.string.tab2));
        tabHost.addTab(spec2);

        //상단 TabHost
        TabHost tabHost1 = (TabHost) view.findViewById(R.id.tabHost1);
        tabHost1.setup();

        TabHost.TabSpec spec3 = tabHost1.newTabSpec("Realtime AQI")
                .setContent(R.id.tab3).setIndicator("Realtime AQI");
        tabHost1.addTab(spec3);
        TabHost.TabSpec spec5 = tabHost1.newTabSpec("Standard AQI")
                .setContent(R.id.tab5).setIndicator("Standard AQI");
        tabHost1.addTab(spec5);
        TabHost.TabSpec spec4 = tabHost1.newTabSpec("Heart")
                .setContent(R.id.tab4).setIndicator("Heart");
        tabHost1.addTab(spec4);

        //새로 추가된 부분
        heartText = (TextView) view.findViewById(R.id.heartValue);
        heart = (ImageView) view.findViewById(R.id.heart);
        heartbit = (ImageView) view.findViewById(R.id.heartbit);
        //GIF File Object
        heartTartget = new GlideDrawableImageViewTarget(heart);
        heartBitget = new GlideDrawableImageViewTarget(heartbit);

        heart.setImageResource(R.drawable.human_nomal2);
        Glide.with(this).load(R.raw.heart_stop).into(heartBitget);

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
        leftAxish.setAxisMinimum(50f);
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

    //sensor LineDateSet
    private LineDataSet createSetH() {
        LineDataSet set = new LineDataSet(null, "HeartRate Data");
        //set.setAxisDependency(AxisDependency.LEFT);
        set.setAxisDependency(AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
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
        //액티비티가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(getActivity().getApplicationContext());

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // OnMapReadyCallback implements 해야 mapView.getMapAsync(this); 사용가능. this 가 OnMapReadyCallback

        this.googleMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에 지도의 초기위치를 서울로 이동
        setCurrentLocation(null, "Unable to get location info", "Check location permissions and GPS activation");

        //나침반이 나타나도록 설정
        googleMap.getUiSettings().setCompassEnabled(true);
        // 매끄럽게 이동함
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //  API 23 이상이면 런타임 퍼미션 처리 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 사용권한체크
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                //사용권한이 없을경우
                //권한 재요청
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                //사용권한이 있는경우
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
