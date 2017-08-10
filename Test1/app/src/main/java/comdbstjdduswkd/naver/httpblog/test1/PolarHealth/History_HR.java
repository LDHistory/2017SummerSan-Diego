package comdbstjdduswkd.naver.httpblog.test1.PolarHealth;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import comdbstjdduswkd.naver.httpblog.test1.R;

import static comdbstjdduswkd.naver.httpblog.test1.UserManagement.LoginActivity.usernum;

/**
 * Created by USER on 2017-08-09.
 */

public class History_HR extends Fragment {
    private static LineChart his_hrChart;
    View view;
    String queryResult;
    ArrayList<Integer> avgHR;
    ArrayList<Integer> maxHR;
    ArrayList<Integer> minHR;
    ArrayList<String> xVals;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_his_hr, container, false);
        avgHR = new ArrayList<>();
        maxHR = new ArrayList<>();
        minHR = new ArrayList<>();
        his_hrChart = (LineChart) view.findViewById(R.id.his_hr_chart);
        XAxis xA = his_hrChart.getXAxis();
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
        numMap.put(1,xVals.get(0));
        numMap.put(2,xVals.get(1));
        numMap.put(3,xVals.get(2));
        numMap.put(4,xVals.get(3));
        numMap.put(5,xVals.get(4));
        numMap.put(6,xVals.get(5));
        numMap.put(7,xVals.get(6));

        XAxis xAxis = his_hrChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return numMap.get((int)value);
            }
        });

        try {
            Log.e("URL접속","");
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/slim-api/air-data-show/"+usernum);       // URL 설정
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
                Log.e("asdf",""+jsonArray.get(0).toString());
                for(int i=0;i<7;i++) {
                    avgHR.add(Integer.parseInt(jsonArray.getJSONObject(i).getString("avghr")));
                    maxHR.add(Integer.parseInt(jsonArray.getJSONObject(i).getString("maxhr")));
                    minHR.add(Integer.parseInt(jsonArray.getJSONObject(i).getString("minhr")));
                    Log.e("avgHR","index : "+i+"  value : "+avgHR.get(i));
                    Log.e("maxHR","index : "+i+"  value : "+maxHR.get(i));
                    Log.e("minHR","index : "+i+"  value : "+minHR.get(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } // try

        ArrayList<Entry> listHRavg = new ArrayList<Entry>(); //avgHR list
        ArrayList<Entry> listHRmax = new ArrayList<Entry>(); //maxHR list
        ArrayList<Entry> listHRmin = new ArrayList<Entry>(); //minHR list

        try {
            for (int i = 0; i < 7; i++) {
                listHRavg.add(new Entry(i + 1, avgHR.get(i)));
                listHRmax.add(new Entry(i + 1, maxHR.get(i)));
                listHRmin.add(new Entry(i + 1, minHR.get(i)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        LineDataSet setComp1 = new LineDataSet(listHRavg, "HR AVG"); //avgHR lineSet
        setComp1.setLineWidth(2f);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(Color.GREEN);
        setComp1.setValueTextColor(Color.GREEN);
        setComp1.setFillColor(Color.GREEN);
        setComp1.setCircleColor(Color.GREEN);

        LineDataSet setComp2 = new LineDataSet(listHRmax, "HR MAX"); //avgHR lineSet
        setComp2.setLineWidth(2f);
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setColor(Color.RED);
        setComp2.setValueTextColor(Color.RED);
        setComp2.setFillColor(Color.RED);
        setComp2.setCircleColor(Color.RED);

        LineDataSet setComp3 = new LineDataSet(listHRmin, "HR MIN"); //avgHR lineSet
        setComp3.setLineWidth(2f);
        setComp3.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp3.setColor(Color.BLUE);
        setComp3.setValueTextColor(Color.BLUE);
        setComp3.setFillColor(Color.BLUE);
        setComp3.setCircleColor(Color.BLUE);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        dataSets.add(setComp1);
        dataSets.add(setComp2);
        dataSets.add(setComp3);

        LineData data = new LineData(dataSets);
        his_hrChart.setData(data); //set data
        his_hrChart.invalidate(); //refresh data

        return view;
    }
}
