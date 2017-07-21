package comdbstjdduswkd.naver.httpblog.test1;
import android.app.Fragment;
import android.graphics.Color;
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
import android.widget.TabHost;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;

import static comdbstjdduswkd.naver.httpblog.test1.R.id.map;


/**
 * Created by USER on 2017-07-14.
 */

public class RealTimeActivity extends Fragment implements OnMapReadyCallback {
    View view;
    LineChart mchart;
    private MapView mapView = null;
    TextView CO, NO2, SO2, O3, PM25, TEMP;

    Bundle bundle = new Bundle();
    String id;

    public void setCo(String co){
        if(CO!=null)
            CO.setText(co);
        else
            Log.e("RealTimeActivity", "setCo CO: "+CO);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.realtime_layout, container, false);

        CO = (TextView)view.findViewById(R.id.co_text);
        NO2 = (TextView)view.findViewById(R.id.no2_text);
        SO2 = (TextView)view.findViewById(R.id.so2_text);
        O3 = (TextView)view.findViewById(R.id.o3_text);
        PM25 = (TextView)view.findViewById(R.id.pm25);
        TEMP = (TextView)view.findViewById(R.id.temp_text);

        mapView = (MapView)view.findViewById(map);
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

        mchart = (LineChart)view.findViewById(R.id.map_chart);
        SimpleDateFormat SimFormat = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currtime = dateFormat.format(calendar.getTime()); //현재시간을 문자열로 받아온다.

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, -7);
        Date date = calendar2.getTime();
        String lastday = new SimpleDateFormat("yyyy-MM-dd").format(date); //7일전 날짜

        ArrayList<Entry> valsComp[] = new ArrayList[3]; //파랑 값 담는 리스트
        valsComp[0] = new ArrayList<Entry>();valsComp[1] = new ArrayList<Entry>();valsComp[2] = new ArrayList<Entry>();
        ////파랑값 설정
        valsComp[0].add(new Entry(100.0f, 0));
        valsComp[0].add(new Entry(50.0f, 1));
        valsComp[0].add(new Entry(75.0f, 2));
        valsComp[0].add(new Entry(50.0f, 3));
        valsComp[0].add(new Entry(50.0f, 4));
        valsComp[0].add(new Entry(150.0f, 5));
        valsComp[0].add(new Entry(90.0f, 6));

        valsComp[1].add(new Entry(10.0f, 0));
        valsComp[1].add(new Entry(53.0f, 1));
        valsComp[1].add(new Entry(7.0f, 2));
        valsComp[1].add(new Entry(159.0f, 3));
        valsComp[1].add(new Entry(50.0f, 4));
        valsComp[1].add(new Entry(80.0f, 5));
        valsComp[1].add(new Entry(70.0f, 6));

        valsComp[2].add(new Entry(90.0f, 0));
        valsComp[2].add(new Entry(110.0f, 1));
        valsComp[2].add(new Entry(156.0f, 2));
        valsComp[2].add(new Entry(16.0f, 3));
        valsComp[2].add(new Entry(45.0f, 4));
        valsComp[2].add(new Entry(65.0f, 5));
        valsComp[2].add(new Entry(98.0f, 6));

        LineDataSet setComp1 = new LineDataSet(valsComp[0], "CO"); //파랑
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT); //파랑

        LineDataSet setComp2 = new LineDataSet(valsComp[1], "SO2"); //빨강
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT ); //빨강
        setComp2.setColor(Color.RED);setComp2.setValueTextColor(Color.RED);
        setComp2.setFillColor(Color.RED);setComp2.setCircleColor(Color.RED);

        LineDataSet setComp3 = new LineDataSet(valsComp[2], "NO2"); //빨강
        setComp3.setAxisDependency(YAxis.AxisDependency.LEFT ); //빨강
        setComp3.setColor(Color.GREEN);setComp3.setValueTextColor(Color.GREEN);
        setComp3.setFillColor(Color.GREEN);setComp3.setCircleColor(Color.GREEN);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1); ////파랑선을 그려준다.
        dataSets.add(setComp2); //빨강
        dataSets.add(setComp3); //초록

        //Set Name of X values
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.clear();
        try {
            for (int i = 0; i < 7; i++) {
                //xVals.add(lastday.substring(5, lastday.length()));
                xVals.add(SimFormat.format(calendar2.getTime()));

                String x = "" + xVals.get(0) + "";
                calendar2.add(Calendar.DAY_OF_MONTH, 1);
            }
            xVals.add(SimFormat.format(calendar2.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LineData data = new LineData(xVals, dataSets);

        //데이터 설정 및 새로고침
        mchart.setData(data);
        mchart.invalidate();
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
}
