package comdbstjdduswkd.naver.httpblog.test1.History;

import android.app.Fragment;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import comdbstjdduswkd.naver.httpblog.test1.HistoryActivity;
import comdbstjdduswkd.naver.httpblog.test1.R;

import static comdbstjdduswkd.naver.httpblog.test1.HistoryActivity.tv;
import static comdbstjdduswkd.naver.httpblog.test1.UserManagement.LoginActivity.usernum;

/**
 * Created by USER on 2017-08-09.
 */

public class History_PM25 extends Fragment {
    View view;
    private static LineChart his_pm25Chart;
    String queryResult;
    ArrayList<Float> avgPM25;
    ArrayList<String> xVals;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_his_pm25, container, false);
        try {
            avgPM25 = new ArrayList<>();
            his_pm25Chart = (LineChart) view.findViewById(R.id.his_pm25_chart);
            XAxis xA = his_pm25Chart.getXAxis();
            xA.setPosition(XAxis.XAxisPosition.BOTTOM);

            SimpleDateFormat SimFormat = new SimpleDateFormat("MM-dd");
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currtime = dateFormat.format(calendar.getTime()); //The current time is received as a string.

            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.DAY_OF_MONTH, -7);
            Date date = calendar2.getTime();
            String lastday = new SimpleDateFormat("yyyy-MM-dd").format(date); //Date 7 days ago

            //Set Name of X values
            xVals = new ArrayList<String>();
            xVals.clear();
            try {
                for (int i = 0; i < 7; i++) {
                    xVals.add(SimFormat.format(calendar2.getTime()));
                    calendar2.add(Calendar.DAY_OF_MONTH, 1);
                }
                xVals.add(SimFormat.format(calendar2.getTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            final HashMap<Integer, String> numMap = new HashMap<>();
            numMap.put(1, xVals.get(0));
            numMap.put(2, xVals.get(1));
            numMap.put(3, xVals.get(2));
            numMap.put(4, xVals.get(3));
            numMap.put(5, xVals.get(4));
            numMap.put(6, xVals.get(5));
            numMap.put(7, xVals.get(6));

            XAxis xAxis = his_pm25Chart.getXAxis();
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return numMap.get((int) value);
                }
            });
            ArrayList<Entry> list = new ArrayList<Entry>();

            try {
                Log.e("URL접속", "");
                //--------------------------
                //   Set the transfer mode (basical settings)
                //--------------------------
                URL url = new URL("http://teamb-iot.calit2.net/slim-api/history-air-show/" + usernum);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                //--------------------------
                //   Set the transfer mode (basical settings)
                //--------------------------
                http.setDefaultUseCaches(false);
                http.setDoInput(true);
                http.setRequestMethod("GET");

                // Tell the server to process it the same way that the value passed from the Web to <Form>.
                http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                //--------------------------
                //   Data received from server
                //--------------------------
                InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuilder builder = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {       // It will be sent line by line from the server, so read it line by line.
                    builder.append(str + "\n");                    // Add line separator for display in View.
                }
                queryResult = builder.toString();                 // Store transmission results in global variables.
                try {
                    JSONArray jsonArray = new JSONArray(queryResult);
                    for (int i = 0; i < 7; i++) {
                        avgPM25.add(Float.parseFloat(jsonArray.getJSONObject(i).getString("pm25")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                //
            } catch (IOException e) {
                //
            }

            list.add(new Entry(1, avgPM25.get(0)));
            list.add(new Entry(2, avgPM25.get(1)));
            list.add(new Entry(3, avgPM25.get(2)));
            list.add(new Entry(4, avgPM25.get(3)));
            list.add(new Entry(5, avgPM25.get(4)));
            list.add(new Entry(6, avgPM25.get(5)));
            list.add(new Entry(7, avgPM25.get(6)));

            LineDataSet setComp1 = new LineDataSet(list, "PM25");
            setComp1.setLineWidth(2f);
            setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp1.setColor(Color.parseColor("#1DDB16"));
            setComp1.setValueTextColor(Color.parseColor("#1DDB16"));
            setComp1.setFillColor(Color.parseColor("#1DDB16"));
            setComp1.setCircleColor(Color.parseColor("#1DDB16"));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

            dataSets.add(setComp1); ////파랑선을 그려준다.

            LineData data = new LineData(dataSets);
            his_pm25Chart.setData(data); //set data
            his_pm25Chart.invalidate(); //refresh data
        }catch (Exception e){
            tv.setText("Can't Received from server");
        }
        return view;
    }
}