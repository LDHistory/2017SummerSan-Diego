package comdbstjdduswkd.naver.httpblog.test1;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;
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

import comdbstjdduswkd.naver.httpblog.test1.History.History_CO;
import comdbstjdduswkd.naver.httpblog.test1.History.History_NO2;
import comdbstjdduswkd.naver.httpblog.test1.History.History_O3;
import comdbstjdduswkd.naver.httpblog.test1.History.History_PM25;
import comdbstjdduswkd.naver.httpblog.test1.History.History_SO2;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HistoryActivity extends Fragment {
    View view;
    Button coBtn, no2Btn, so2Btn, o3Btn, pm25Btn;
    History_CO hisCO;
    History_NO2 hisNO2;
    History_SO2 hisSO2;
    History_O3 hisO3;
    History_PM25 hisPM25;
    FragmentManager manager;

    public LineChart chart;
    Calendar Begindate = Calendar.getInstance();
    Calendar Enddate = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener d1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Begindate.set(Calendar.YEAR, year);
            Begindate.set(Calendar.MONTH, monthOfYear);
            Begindate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };

    DatePickerDialog.OnDateSetListener d2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Enddate.set(Calendar.YEAR, year);
            Enddate.set(Calendar.MONTH, monthOfYear);
            Enddate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_history, container, false);
        manager = getFragmentManager();

        hisCO = new History_CO();
        hisNO2 = new History_NO2();
        hisSO2 = new History_SO2();
        hisO3 = new History_O3();
        hisPM25 = new History_PM25();

        //chart = (LineChart)view.findViewById(R.id.lineChart);
        coBtn = (Button)view.findViewById(R.id.coBtn);
        coBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(0);
            }
        });
        no2Btn = (Button)view.findViewById(R.id.no2Btn);
        no2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(1);
            }
        });
        so2Btn = (Button)view.findViewById(R.id.so2Btn);
        so2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(4);
            }
        });
        o3Btn = (Button)view.findViewById(R.id.o3Btn);
        o3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(2);
            }
        });
        pm25Btn = (Button)view.findViewById(R.id.pm25Btn);
        pm25Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(3);
            }
        });

        TabHost tabHost1 = (TabHost)view.findViewById(R.id.tabHost3);
        tabHost1.setup();
        TabHost.TabSpec spec11 = tabHost1.newTabSpec("Air")
                .setContent(R.id.tab11).setIndicator("Air");
        tabHost1.addTab(spec11);
        TabHost.TabSpec spec22 = tabHost1.newTabSpec("Heart")
                .setContent(R.id.tab22).setIndicator("Heart");
        tabHost1.addTab(spec22);

        SimpleDateFormat SimFormat = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currtime = dateFormat.format(calendar.getTime()); //현재시간을 문자열로 받아온다.

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, -7);
        Date date = calendar2.getTime();
        String lastday = new SimpleDateFormat("yyyy-MM-dd").format(date); //7일전 날짜

        TextView tv = (TextView)view.findViewById(R.id.textView5);
        tv.setText("\n"+lastday+" ~ "+currtime+"\nThis is result for a week.");

        //Fragment initialization
        changeFragment(4);
        changeFragment(3);
        changeFragment(2);
        changeFragment(1);
        changeFragment(0);
        hideFragment();
        return view;
    }
    public void hideFragment(){
        if(manager.findFragmentByTag("h_no2") != null && manager.findFragmentByTag("h_o3") != null
                && manager.findFragmentByTag("h_pm25") != null && manager.findFragmentByTag("h_so2") != null) {
            manager.beginTransaction().hide(manager.findFragmentByTag("h_no2")).commit();
            manager.beginTransaction().hide(manager.findFragmentByTag("h_o3")).commit();
            manager.beginTransaction().hide(manager.findFragmentByTag("h_pm25")).commit();
            manager.beginTransaction().hide(manager.findFragmentByTag("h_so2")).commit();
        }else{
            Log.e("에러다 씨발","ㅇ");
        }
    }
    public void changeFragment(int fNum) {
        switch (fNum) {
            case 0:
                if (manager.findFragmentByTag("h_co") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("h_co")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.frame, hisCO, "h_co").commit();
                }
                if (manager.findFragmentByTag("h_no2") != null && manager.findFragmentByTag("h_o3") != null
                        && manager.findFragmentByTag("h_pm25") != null && manager.findFragmentByTag("h_so2") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_no2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_o3")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_pm25")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_so2")).commit();
                }
                break;
            case 1:
                if (manager.findFragmentByTag("h_no2") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("h_no2")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.frame, hisNO2, "h_no2").commit();
                }
                if (manager.findFragmentByTag("h_co") != null && manager.findFragmentByTag("h_o3") != null
                        && manager.findFragmentByTag("h_pm25") != null && manager.findFragmentByTag("h_so2") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_co")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_o3")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_pm25")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_so2")).commit();
                }
                break;
            case 2:
                if (manager.findFragmentByTag("h_o3") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("h_o3")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.frame, hisO3, "h_o3").commit();
                }
                if (manager.findFragmentByTag("h_co") != null && manager.findFragmentByTag("h_no2") != null
                        && manager.findFragmentByTag("h_pm25") != null && manager.findFragmentByTag("h_so2") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_co")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_no2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_pm25")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_so2")).commit();
                }
                break;
            case 3:
                if (manager.findFragmentByTag("h_pm25") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("h_pm25")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.frame, hisPM25, "h_pm25").commit();
                }
                if (manager.findFragmentByTag("h_co") != null && manager.findFragmentByTag("h_no2") != null
                        && manager.findFragmentByTag("h_o3") != null && manager.findFragmentByTag("h_so2") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_co")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_no2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_o3")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_so2")).commit();
                }
                break;
            case 4:
                if (manager.findFragmentByTag("h_so2") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("h_so2")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.frame, hisSO2, "h_so2").commit();
                }
                if (manager.findFragmentByTag("h_co") != null && manager.findFragmentByTag("h_no2") != null
                        && manager.findFragmentByTag("h_o3") != null && manager.findFragmentByTag("h_pm25") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_co")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_no2")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_o3")).commit();
                    manager.beginTransaction().hide(manager.findFragmentByTag("h_pm25")).commit();
                }
                break;
        }
    }
}
