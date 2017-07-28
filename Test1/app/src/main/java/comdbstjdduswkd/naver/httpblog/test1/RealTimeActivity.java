package comdbstjdduswkd.naver.httpblog.test1;
import android.app.Fragment;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by USER on 2017-07-14.
 */

public class RealTimeActivity extends Fragment implements OnMapReadyCallback{
    View view;

    private LineChart coChart, hChart;
    private LineChart noChart;
    
    private GoogleMap googleMap = null;
    private MapView mapView = null;

    TextView heartText, CO, NO2, SO2, O3, PM25, TEMP, maxhert, minheart;
    ImageView heart, heartbit, hearticon;
    Handler handler;
    GlideDrawableImageViewTarget heartTartget, heartBitget;

    int max = 0;
    int min = 999;

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

    public void addHEntry(Integer val) {
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
    public void addEntry(JSONObject jsonObject) {
        try {
            LineData data = coChart.getData();
            if (data != null) {
                ILineDataSet set = data.getDataSetByIndex(0);
                // set.addEntry(...); // can be called as well
                if (set == null) {
                    set = createSet();
                    data.addDataSet(set);
                }
                //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
                if(CO != null) {
                    data.addEntry(new Entry(set.getEntryCount(), Float.parseFloat(jsonObject.getString("CO"))), 0);
                    data.notifyDataChanged();
                }
                // let the chart know it's data has changed
                coChart.notifyDataSetChanged();
                // limit the number of visible entries
                coChart.setVisibleXRangeMaximum(120);
                //mChart.setVisibleYRange(30, AxisDependency.LEFT);
                coChart.moveViewToX(data.getEntryCount());
            }
        }catch (Exception e){
            Log.e("setAQI","value error");
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.realtime_layout, container, false);

        maxhert = (TextView) view.findViewById(R.id.HMValue);
        minheart = (TextView) view.findViewById(R.id.MHValue);

        mapView = (MapView)view.findViewById(R.id.map);
        mapView.getMapAsync(this);

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
        NO2 = (TextView)view.findViewById(R.id.no2_text);
        O3 = (TextView)view.findViewById(R.id.o3_text);
        SO2 = (TextView)view.findViewById(R.id.so2_text);
        PM25 = (TextView)view.findViewById(R.id.pm25);
        TEMP = (TextView)view.findViewById(R.id.temp_text);

        coChart = (LineChart) view.findViewById(R.id.co_chart);
        hChart = (LineChart) view.findViewById(R.id.hart_chart);

        // enable description text
        coChart.getDescription().setEnabled(true);
        hChart.getDescription().setEnabled(true);

        // enable touch gestures
        coChart.setTouchEnabled(true);
        hChart.setTouchEnabled(true);

        // enable scaling and dragging
        coChart.setDragEnabled(true);
        coChart.setScaleEnabled(true);
        coChart.setDrawGridBackground(false);

        hChart.setDragEnabled(true);
        hChart.setScaleEnabled(true);
        hChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        coChart.setPinchZoom(true);
        hChart.setPinchZoom(true);

        // set an alternative background color
        coChart.setBackgroundColor(Color.LTGRAY);
        hChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        LineData datah = new LineData();
        data.setValueTextColor(Color.BLACK);
        datah.setValueTextColor(Color.BLACK);
        //add empty data
        coChart.setData(data);
        hChart.setData(datah);
        // get the legend (only possible after setting data)
        Legend l = coChart.getLegend();
        Legend lh = hChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);
        lh.setForm(Legend.LegendForm.LINE);
        lh.setTextColor(Color.WHITE);

        XAxis xl = coChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        //heart X
        XAxis xlh = hChart.getXAxis();
        xlh.setTextColor(Color.WHITE);
        xlh.setDrawGridLines(false);
        xlh.setAvoidFirstLastClipping(true);
        xlh.setEnabled(true);

        YAxis leftAxis = coChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(50f);
        leftAxis.setAxisMinimum(39f);
        leftAxis.setDrawGridLines(true);

        //heart Y
        YAxis leftAxish = hChart.getAxisLeft();
        leftAxish.setTextColor(Color.WHITE);
        leftAxish.setAxisMaximum(130f);
        leftAxish.setAxisMinimum(50f);
        leftAxish.setDrawGridLines(true);

        YAxis rightAxis = coChart.getAxisRight();
        YAxis rightAxish = hChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxish.setEnabled(false);
        return view;
    }
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "CO Data");
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng Atkinson = new LatLng(32.882415, -117.234817);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Atkinson);
        markerOptions.title("Q.I");
        markerOptions.snippet("Atkinson Hall");
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(Atkinson));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }
}
