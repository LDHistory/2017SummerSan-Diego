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

public class O3 extends Fragment {
    private static LineChart o3Chart;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_o3, container, false);

        o3Chart = (LineChart) view.findViewById(R.id.o3);

        // enable description text
        o3Chart.getDescription().setEnabled(true);

        // enable touch gestures
        o3Chart.setTouchEnabled(true);

        // enable scaling and dragging
        o3Chart.setDragEnabled(true);
        o3Chart.setScaleEnabled(true);
        o3Chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        o3Chart.setPinchZoom(true);

        // set an alternative background color
        o3Chart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        //add empty data
        o3Chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = o3Chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = o3Chart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = o3Chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(605f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = o3Chart.getAxisRight();
        rightAxis.setEnabled(false);
        return view;
    }

    private LineDataSet createSetO3() {
        LineDataSet set = new LineDataSet(null, "O3 Data");
        set.setColor(Color.parseColor("#FFE400"));
        set.setValueTextColor(Color.parseColor("#FFE400"));
        set.setFillColor(Color.parseColor("#FFE400"));
        set.setCircleColor(Color.parseColor("#FFE400"));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    public void addEntryO3(JSONObject jsonObject) {
        try {
            LineData dataco = o3Chart.getData();
            if (dataco != null) {
                ILineDataSet setco = dataco.getDataSetByIndex(0);

                // set.addEntry(...); // can be called as well
                if (setco == null) {
                    setco = createSetO3();
                    dataco.addDataSet(setco);
                }

                dataco.addEntry(new Entry(setco.getEntryCount(), Float.parseFloat(jsonObject.getString("O3"))), 0);
                dataco.notifyDataChanged();

                // let the chart know it's data has changed
                o3Chart.notifyDataSetChanged();
                // limit the number of visible entries
                o3Chart.setVisibleXRangeMaximum(20);
                //mChart.setVisibleYRange(30, AxisDependency.LEFT);
                o3Chart.moveViewToX(dataco.getEntryCount());
            }
        }catch (Exception e){
            Log.e("O3","value error");
            e.printStackTrace();
        }
    }
}
