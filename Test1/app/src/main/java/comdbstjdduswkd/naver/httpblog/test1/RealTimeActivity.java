package comdbstjdduswkd.naver.httpblog.test1;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by USER on 2017-07-14.
 */

public class RealTimeActivity extends Fragment implements OnMapReadyCallback{
    View view;
    private LineChart mChart;
    private GoogleMap googleMap = null;
    private MapView mapView = null;

    TextView heartText, CO, NO2, SO2, O3, PM25, TEMP;
    ImageView heart, heartbit;
    Handler handler;
    GlideDrawableImageViewTarget heartTartget, heartBitget;

    public void setAQI(String values){
        try {
            String messageArray[] = null;
            messageArray = values.split(";");
            if (CO != null && NO2 != null && SO2 != null && O3 != null && PM25 != null && TEMP != null) {
                CO.setText(messageArray[0]);
                NO2.setText(messageArray[1]);
                SO2.setText(messageArray[2]);
                O3.setText(messageArray[3]);
                PM25.setText(messageArray[4]);
                TEMP.setText(messageArray[5]);
            } else
                Log.e("RealTimeActivity", "setCo CO: " + CO);
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

        mapView = (MapView)view.findViewById(R.id.map);
        mapView.getMapAsync(this);

        TabHost tabHost = (TabHost)view.findViewById(R.id.tabHost2);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Map")
                .setContent(R.id.tab1).setIndicator(getString(R.string.tab1));
        tabHost.addTab(spec1);
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Graph")
                .setContent(R.id.tab2).setIndicator(getString(R.string.tab2));
        tabHost.addTab(spec2);

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

        CO = (TextView)view.findViewById(R.id.co_text);
        NO2 = (TextView)view.findViewById(R.id.no2_text);
        O3 = (TextView)view.findViewById(R.id.o3_text);
        SO2 = (TextView)view.findViewById(R.id.so2_text);
        PM25 = (TextView)view.findViewById(R.id.pm25);
        TEMP = (TextView)view.findViewById(R.id.temp_text);

        //handelr
        handler = new Handler(){
            public void handleMessage(android.os.Message msg){
                heartText.setText(""+msg.what);
                if(msg.what >= 70)
                    heart.setImageResource(R.drawable.human_fast2);
                else if(msg.what >= 50)
                    heart.setImageResource(R.drawable.human_nomal2);
                else
                    heart.setImageResource(R.drawable.human_slow2);
            }
        };

        //HeartBit GIF
        Glide.with(this).load(R.raw.heartbit).into(heartBitget);

        mChart = (LineChart) view.findViewById(R.id.map_chart);
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        //add empty data
        mChart.setData(data);
// get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        /*
        ArrayList<Entry> valsComp[] = new ArrayList[3]; //파랑 값 담는 리스트
        valsComp[0] = new ArrayList<Entry>();
        ////파랑값 설정
        valsComp[0].add(new Entry(100.0f, 0));
        valsComp[0].add(new Entry(50.0f, 1));
        valsComp[0].add(new Entry(75.0f, 2));
        valsComp[0].add(new Entry(50.0f, 3));
        valsComp[0].add(new Entry(50.0f, 4));
        valsComp[0].add(new Entry(150.0f, 5));
        valsComp[0].add(new Entry(90.0f, 6));

        LineDataSet setComp1 = new LineDataSet(valsComp[0], "CO"); //파랑
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT); //파랑

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1); ////파랑선을 그려준다.

        //데이터 설정 및 새로고침
        mChart.setData(data);
        mChart.invalidate();
*/
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        //새로 추가된 부분
        Thread ChangeHeart = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        for(int i = 0; i < 100; i++) {
                            handler.sendEmptyMessage(i);
                            Thread.sleep(100);
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        ChangeHeart.start();

        mapView.onStart();
        //새로 추가된 부분
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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
    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
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
    public void addEntry(String val) {
        try {
            String messageArray[] = null;
            messageArray = val.split(";");
            LineData data = mChart.getData();

            if (data != null) {

                ILineDataSet set = data.getDataSetByIndex(0);
                // set.addEntry(...); // can be called as well

                if (set == null) {
                    set = createSet();
                    data.addDataSet(set);
                }

                data.addEntry(new Entry(set.getEntryCount(), Integer.parseInt(messageArray[0])), 0);
                //data.addEntry(new Entry(set.getEntryCount(), 10), 0);
                data.notifyDataChanged();

                // let the chart know it's data has changed
                mChart.notifyDataSetChanged();

                // limit the number of visible entries
                mChart.setVisibleXRangeMaximum(120);
                //mChart.setVisibleYRange(30, AxisDependency.LEFT);

                // move to the latest entry
                mChart.moveViewToX(data.getYValCount());

                // this automatically refreshes the chart (calls invalidate())
                mChart.moveViewTo(data.getXValCount()-7, 55f,
                        AxisDependency.LEFT);
            }
        }catch (Exception e){
            Log.e("setAQI","value error");
            e.printStackTrace();
        }

    }
}
