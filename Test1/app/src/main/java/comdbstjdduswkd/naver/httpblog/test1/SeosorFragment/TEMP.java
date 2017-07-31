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

public class TEMP extends Fragment {
    private static LineChart tempChart;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_temp, container, false);

        tempChart = (LineChart) view.findViewById(R.id.temp);

        // enable description text
        tempChart.getDescription().setEnabled(true);

        // enable touch gestures
        tempChart.setTouchEnabled(true);

        // enable scaling and dragging
        tempChart.setDragEnabled(true);
        tempChart.setScaleEnabled(true);
        tempChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        tempChart.setPinchZoom(true);

        // set an alternative background color
        tempChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        //add empty data
        tempChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = tempChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = tempChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = tempChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(300f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = tempChart.getAxisRight();
        rightAxis.setEnabled(false);
        return view;
    }

    private LineDataSet createSetTEMP() {
        LineDataSet set = new LineDataSet(null, "TEMP Data");
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

    public void addEntryTEMP(JSONObject jsonObject) {
        try {
            LineData dataco = tempChart.getData();
            if (dataco != null) {
                ILineDataSet setco = dataco.getDataSetByIndex(0);

                // set.addEntry(...); // can be called as well
                if (setco == null) {
                    setco = createSetTEMP();
                    dataco.addDataSet(setco);
                }

                dataco.addEntry(new Entry(setco.getEntryCount(), Float.parseFloat(jsonObject.getString("temp"))), 0);
                dataco.notifyDataChanged();

                // let the chart know it's data has changed
                tempChart.notifyDataSetChanged();
                // limit the number of visible entries
                tempChart.setVisibleXRangeMaximum(20);
                //mChart.setVisibleYRange(30, AxisDependency.LEFT);
                tempChart.moveViewToX(dataco.getEntryCount());
            }
        }catch (Exception e){
            Log.e("CO","value error");
            e.printStackTrace();
        }
    }
}
