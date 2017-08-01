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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
    FragmentManager manager;

    CO fragmentco;
    NO2 fragmentno2;
    O3 fragmento3;
    PM25 fragmentpm25;
    SO2 fragmentso2;
    TEMP fragmenttemp;

    private LineChart coChart, hChart;
    
    private GoogleMap googleMap = null;
    private MapView mapView = null;

    TextView heartText, CO, NO2, SO2, O3, PM25, TEMP, maxhert, minheart;
    ImageView heart, heartbit;
    GlideDrawableImageViewTarget heartTartget, heartBitget;

    int max = 0;
    int min = 999;

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if ( currentMarker != null ) currentMarker.remove();

        if ( location != null) {
            //현재위치의 위도 경도 가져옴
            LatLng currentLocation = new LatLng( location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            CircleOptions circle1KM = new CircleOptions().center(currentLocation) //원점
                    .radius(200)      //반지름 단위 : m
                    .strokeWidth(0f)  //선너비 0f : 선없음
                    .fillColor(Color.parseColor("#8800ffff")); //배경색
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
        CircleOptions circle1KM = new CircleOptions().center(DEFAULT_LOCATION) //원점
                .radius(200)      //반지름 단위 : m
                .strokeWidth(0f)  //선너비 0f : 선없음
                .fillColor(Color.parseColor("#880000ff")); //배경색
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1));
        currentMarker = this.googleMap.addMarker(markerOptions);
        this.googleMap.addCircle(circle1KM);


        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
    }

    public void setHeart(int bit){
        try{
            heartText.setText("" + bit);
            heart.setImageResource(R.drawable.human_nomal2);

            //set max, min
            if(max <= bit) {
                max = bit;
                maxhert.setText("" + max);
            }
            if(min >= bit){
                min = bit;
                minheart.setText("" + min);
            }


            //heartrate state image
            if(bit >= 100){
                heart.setImageResource(R.drawable.human_fast2);
                Glide.with(this).load(R.raw.heart_fast).into(heartBitget);
            }
            else if(bit >= 60){
                heart.setImageResource(R.drawable.human_nomal2);
                Glide.with(this).load(R.raw.heart_normal).into(heartBitget);
            }
            else {
                heart.setImageResource(R.drawable.human_slow2);
                Glide.with(this).load(R.raw.heart_normal).into(heartBitget);
            }
        }catch (Exception e){
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
                if(heartText != null) {
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
        }catch (Exception e){
            Log.e("setHeart","value error");
            e.printStackTrace();
        }
    }

    public void setAQI(JSONObject data){
        try{
            CO.setText(data.getString("CO"));
            NO2.setText(data.getString("NO2"));
            SO2.setText(data.getString("SO2"));
            O3.setText(data.getString("O3"));
            PM25.setText(data.getString("PM25"));
            TEMP.setText(data.getString("temp"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.realtime_layout, container, false);
        manager = getFragmentManager();

        fragmentco = new CO();
        fragmentno2 = new NO2();
        fragmento3 = new O3();
        fragmentpm25 = new PM25();
        fragmentso2 = new SO2();
        fragmenttemp = new TEMP();

        maxhert = (TextView) view.findViewById(R.id.HMValue);
        minheart = (TextView) view.findViewById(R.id.MHValue);

        mapView = (MapView)view.findViewById(R.id.map);
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
        TabHost tabHost = (TabHost)view.findViewById(R.id.tabHost2);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Map")
                .setContent(R.id.tab1).setIndicator(getString(R.string.tab1));
        tabHost.addTab(spec1);
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Graph")
                .setContent(R.id.tab2).setIndicator(getString(R.string.tab2));
        tabHost.addTab(spec2);

        //상단 TabHost
        TabHost tabHost1 = (TabHost)view.findViewById(R.id.tabHost1);
        tabHost1.setup();

        TabHost.TabSpec spec3 = tabHost1.newTabSpec("Air")
                .setContent(R.id.tab3).setIndicator("Air");
        tabHost1.addTab(spec3);
        TabHost.TabSpec spec4 = tabHost1.newTabSpec("Heart")
                .setContent(R.id.tab4).setIndicator("Heart");
        tabHost1.addTab(spec4);

        //새로 추가된 부분
        heartText = (TextView)view.findViewById(R.id.heartValue);
        heart =  (ImageView)view.findViewById(R.id.heart);
        heartbit = (ImageView)view.findViewById(R.id.heartbit);
        //GIF File Object
        heartTartget = new GlideDrawableImageViewTarget(heart);
        heartBitget = new GlideDrawableImageViewTarget(heartbit);

        heart.setImageResource(R.drawable.human_nomal2);
        Glide.with(this).load(R.raw.heart_stop).into(heartBitget);

        CO = (TextView)view.findViewById(R.id.co_text);
        CO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(0);
            }
        });

        NO2 = (TextView)view.findViewById(R.id.no2_text);
        NO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(1);
            }
        });

        O3 = (TextView)view.findViewById(R.id.o3_text);
        O3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(2);
            }
        });

        PM25 = (TextView)view.findViewById(R.id.pm25);
        PM25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(3);
            }
        });

        SO2 = (TextView)view.findViewById(R.id.so2_text);
        SO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(4);
            }
        });

        TEMP = (TextView)view.findViewById(R.id.temp_text);
        TEMP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
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
        if ( googleApiClient != null && googleApiClient.isConnected())
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
        if ( googleApiClient != null)
            googleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if ( googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //액티비티가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(getActivity().getApplicationContext());

        if(mapView != null)
        {
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

            if ( hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                //사용권한이 없을경우
                //권한 재요청
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                //사용권한이 있는경우
                if ( googleApiClient == null) {
                    buildGoogleApiClient();
                }

                if ( ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    googleMap.setMyLocationEnabled(true);
                }
            }
        } else {

            if ( googleApiClient == null) {
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
                //.addApi(Places.GEO_DATA_API)
                //.addApi(Places.PLACE_DETECTION_API)
                //.enableAutoManage(getActivity(), this)
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
        if ( !checkLocationServicesStatus()) {
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
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
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

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( ActivityCompat.checkSelfPermission(getActivity(),
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
        if ( cause ==  CAUSE_NETWORK_LOST )
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED )
            Log.e(TAG,"onConnectionSuspended():  Google Play services " +
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
        //searchCurrentPlaces();
    }
/*
    private void searchCurrentPlaces() {
        @SuppressWarnings("MissingPermission")
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(googleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>(){

            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                int i = 0;
                LikelyPlaceNames = new String[MAXENTRIES];
                LikelyAddresses = new String[MAXENTRIES];
                LikelyAttributions = new String[MAXENTRIES];
                LikelyLatLngs = new LatLng[MAXENTRIES];

                for(PlaceLikelihood placeLikelihood : placeLikelihoods) {
                    LikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                    LikelyAddresses[i] = (String) placeLikelihood.getPlace().getAddress();
                    LikelyAttributions[i] = (String) placeLikelihood.getPlace().getAttributions();
                    LikelyLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                    i++;
                    if(i > MAXENTRIES - 1 ) {
                        break;
                    }
                }

                placeLikelihoods.release();

                Location location = new Location("");
                location.setLatitude(LikelyLatLngs[0].latitude);
                location.setLongitude(LikelyLatLngs[0].longitude);

                setCurrentLocation(location, LikelyPlaceNames[0], LikelyAddresses[0]);
            }
        });

    }
*/
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();

        if ( googleApiClient != null ) {
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);

            if ( googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                googleApiClient.disconnect();
            }
        }
    }
}
