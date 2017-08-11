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
import org.json.JSONObject;

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

public class History_CO extends Fragment {
    private static LineChart his_coChart;
    View view;
    String queryResult;
    ArrayList<Float> avgCO;
    ArrayList<String> xVals;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_his_co, container, false);
        try {
            avgCO = new ArrayList<>();
            his_coChart = (LineChart) view.findViewById(R.id.his_co_chart);
            XAxis xA = his_coChart.getXAxis();
            xA.setPosition(XAxis.XAxisPosition.BOTTOM);

            SimpleDateFormat SimFormat = new SimpleDateFormat("MM-dd");
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currtime = dateFormat.format(calendar.getTime()); //현재시간을 문자열로 받아온다.

            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.DAY_OF_MONTH, -7);
            Date date = calendar2.getTime();
            String lastday = new SimpleDateFormat("yyyy-MM-dd").format(date); //7일전 날짜

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

            XAxis xAxis = his_coChart.getXAxis();
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
                //   URL 설정하고 접속하기
                //--------------------------
                URL url = new URL("http://teamb-iot.calit2.net/slim-api/air-data-show/" + usernum);       // URL 설정
                HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
                //--------------------------
                //   전송 모드 설정 - 기본적인 설정이다
                //--------------------------
                http.setDefaultUseCaches(false);
                http.setDoInput(true);                         // 서버에서 읽기 모드 지정
                http.setRequestMethod("GET");

                // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
                http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                //--------------------------
                //   서버에서 전송받기
                //--------------------------
                InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuilder builder = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                    builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
                }
                queryResult = builder.toString();                       // 전송결과를 전역 변수에 저장
                try {
                    JSONArray jsonArray = new JSONArray(queryResult);
                    Log.e("asdf", "" + jsonArray.get(0).toString());
                    for (int i = 0; i < 7; i++) {
                        avgCO.add(Float.parseFloat(jsonArray.getJSONObject(i).getString("co")));
                        Log.e("avgCo", "index : " + i + "  value : " + avgCO.get(i));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                //
            } catch (IOException e) {
                //
            } // try

            list.add(new Entry(1, avgCO.get(0)));
            list.add(new Entry(2, avgCO.get(1)));
            list.add(new Entry(3, avgCO.get(2)));
            list.add(new Entry(4, avgCO.get(3)));
            list.add(new Entry(5, avgCO.get(4)));
            list.add(new Entry(6, avgCO.get(5)));
            list.add(new Entry(7, avgCO.get(6)));

            LineDataSet setComp1 = new LineDataSet(list, "CO");
            setComp1.setLineWidth(2f);
            setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp1.setColor(Color.parseColor("#A566FF"));
            setComp1.setValueTextColor(Color.parseColor("#A566FF"));
            setComp1.setFillColor(Color.parseColor("#A566FF"));
            setComp1.setCircleColor(Color.parseColor("#A566FF"));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

            dataSets.add(setComp1); ////파랑선을 그려준다.

            LineData data = new LineData(dataSets);
            his_coChart.setData(data); //set data
            his_coChart.invalidate(); //refresh data
        }catch (Exception e){
            tv.setText("Can't Received from server");
        }
        return view;
    }
}
