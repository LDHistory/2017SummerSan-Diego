package comdbstjdduswkd.naver.httpblog.test1.SeosorFragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import comdbstjdduswkd.naver.httpblog.test1.R;

/**
 * Created by DONGHEE on 2017-07-30.
 */

public class NO2 extends Fragment {
    private static LineChart no2Chart;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_no2, container, false);

        no2Chart = (LineChart) view.findViewById(R.id.no2);

        // enable description text
        no2Chart.getDescription().setEnabled(true);

        // enable touch gestures
        no2Chart.setTouchEnabled(true);

        // enable scaling and dragging
        no2Chart.setDragEnabled(true);
        no2Chart.setScaleEnabled(true);
        no2Chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        no2Chart.setPinchZoom(true);

        // set an alternative background color
        no2Chart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        //add empty data
        no2Chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = no2Chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = no2Chart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = no2Chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(2050f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = no2Chart.getAxisRight();

        rightAxis.setEnabled(false);
        return view;
    }

    private LineDataSet createSetNO2() {
        LineDataSet set = new LineDataSet(null, "NO2 Data");
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

    public void addEntryNO2(JSONObject jsonObject) {
        try {
            LineData dataco = no2Chart.getData();

            if (dataco != null) {
                ILineDataSet setco = dataco.getDataSetByIndex(0);

                // set.addEntry(...); // can be called as well
                if (setco == null) {
                    setco = createSetNO2();
                    dataco.addDataSet(setco);
                }

                dataco.addEntry(new Entry(setco.getEntryCount(), Float.parseFloat(jsonObject.getString("NO2"))), 0);
                dataco.notifyDataChanged();

                // let the chart know it's data has changed
                no2Chart.notifyDataSetChanged();
                // limit the number of visible entries
                no2Chart.setVisibleXRangeMaximum(20);
                //mChart.setVisibleYRange(30, AxisDependency.LEFT);
                no2Chart.moveViewToX(dataco.getEntryCount());
            }
        }catch (Exception e){
            Log.e("NO2","value error");
            e.printStackTrace();
        }
    }
}
