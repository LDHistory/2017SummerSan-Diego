package comdbstjdduswkd.naver.httpblog.test1.History;

import android.app.Fragment;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Date;

import comdbstjdduswkd.naver.httpblog.test1.R;

/**
 * Created by USER on 2017-08-09.
 */

public class History_NO2 extends Fragment {
    View view;
    private static LineChart his_no2Chart;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_his_no2, container, false);

        his_no2Chart = (LineChart) view.findViewById(R.id.his_no2_chart);
        XAxis xA = his_no2Chart.getXAxis();
        xA.setPosition(XAxis.XAxisPosition.BOTTOM);

        SimpleDateFormat SimFormat = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currtime = dateFormat.format(calendar.getTime()); //현재시간을 문자열로 받아온다.

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, -7);
        Date date = calendar2.getTime();
        String lastday = new SimpleDateFormat("yyyy-MM-dd").format(date); //7일전 날짜

        ArrayList<Entry> list = new ArrayList<Entry>();
        list.add(new Entry(8,30));
        list.add(new Entry(10,20));
        list.add(new Entry(12,230));
        list.add(new Entry(13,14));
        list.add(new Entry(14,10));
        list.add(new Entry(15,44));
        list.add(new Entry(16,2));

        LineDataSet setComp1 = new LineDataSet(list, "NO2");
        setComp1.setLineWidth(2f);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(Color.parseColor("#FF7012"));
        setComp1.setValueTextColor(Color.parseColor("#FF7012"));
        setComp1.setFillColor(Color.parseColor("#FF7012"));
        setComp1.setCircleColor(Color.parseColor("#FF7012"));

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        dataSets.add(setComp1); ////파랑선을 그려준다.

        //Set Name of X values
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.clear();
        try {
            for (int i = 0; i < 7; i++) {
                xVals.add(lastday.substring(5, lastday.length()));
                xVals.add(SimFormat.format(calendar2.getTime()));

                String x = "" + xVals.get(0) + "";
                calendar2.add(Calendar.DAY_OF_MONTH, 1);
            }
            xVals.add(SimFormat.format(calendar2.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LineData data = new LineData(dataSets);
        his_no2Chart.setData(data); //set data
        his_no2Chart.invalidate(); //refresh data

        return view;
    }
}


