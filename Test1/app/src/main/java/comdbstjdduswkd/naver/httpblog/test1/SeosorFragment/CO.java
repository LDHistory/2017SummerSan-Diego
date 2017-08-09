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

public class CO extends Fragment {
    private static LineChart coChart;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_co, container, false);

        coChart = (LineChart) view.findViewById(R.id.co);

        // enable description text
        coChart.getDescription().setEnabled(true);

        // enable touch gestures
        coChart.setTouchEnabled(true);

        // enable scaling and dragging
        coChart.setDragEnabled(true);
        coChart.setScaleEnabled(true);
        coChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        coChart.setPinchZoom(true);

        // set an alternative background color
        coChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        //add empty data
        coChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = coChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = coChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = coChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(55f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = coChart.getAxisRight();
        rightAxis.setEnabled(false);
        return view;
    }

    private LineDataSet createSetCO() {
        LineDataSet set = new LineDataSet(null, "CO Data");
        set.setColor(Color.parseColor("#A566FF"));
        set.setValueTextColor(Color.parseColor("#A566FF"));
        set.setFillColor(Color.parseColor("#A566FF"));
        set.setCircleColor(Color.parseColor("#A566FF"));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    public void addEntryCO(JSONObject jsonObject) {
        try {
            LineData dataco = coChart.getData();
            if (dataco != null) {
                ILineDataSet setco = dataco.getDataSetByIndex(0);

                // set.addEntry(...); // can be called as well
                if (setco == null) {
                    setco = createSetCO();
                    dataco.addDataSet(setco);
                }
                dataco.addEntry(new Entry(setco.getEntryCount(), Float.parseFloat(jsonObject.getString("CO"))), 0);
                if(dataco.getEntryCount()>10){
                    dataco.removeDataSet(0);
                }
                Log.e("Entry Count",""+dataco.getEntryCount());
                dataco.notifyDataChanged();

                // let the chart know it's data has changed
                coChart.notifyDataSetChanged();
                // limit the number of visible entries
                coChart.setVisibleXRangeMaximum(20);
                //mChart.setVisibleYRange(30, AxisDependency.LEFT);
                coChart.moveViewToX(dataco.getEntryCount());
            }
        }catch (Exception e){
            Log.e("CO","value error");
            e.printStackTrace();
        }
    }

}
