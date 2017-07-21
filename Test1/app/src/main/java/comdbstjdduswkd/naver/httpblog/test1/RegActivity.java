package comdbstjdduswkd.naver.httpblog.test1;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class RegActivity extends AppCompatActivity {

    private Button RegSend, Regcancel;
    private Spinner Year, Month, Day;
    String[] textMonth, textYear, textday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        //+++++++++++++++++++Button Object++++++++++++++++++++++
        RegSend = (Button)findViewById(R.id.regsubmit);
        Regcancel = (Button)findViewById(R.id.regCancel);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //+++++++++++++++++++Spinner Object++++++++++++++++++++++
        Year = (Spinner)findViewById(R.id.year);
        Month  = (Spinner)findViewById(R.id.month);
        Day = (Spinner)findViewById(R.id.day);

        final ArrayList<Integer> year = new ArrayList<>();
        for(int i = 2016; i <= 2020; i++)
            year.add(i);

        final ArrayList<Integer> month = new ArrayList<>();
        for(int j = 1; j <=12; j++)
            month.add(j);

        final ArrayList<Integer> day = new ArrayList<>();
        for(int k = 1; k <= 31; k++)
            day.add(k);

        ArrayAdapter arrayYear, arrayMonth, arrayDay;
        arrayYear = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, year);
        arrayMonth = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, month);
        arrayDay = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, day);

        Year.setAdapter(arrayYear);
        Month.setAdapter(arrayMonth);
        Day.setAdapter(arrayDay);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //++++++++++++++++++++OnclickListner+++++++++++++++++++++++++
        RegSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Regcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regCancel = new Intent(RegActivity.this, MainActivity.class);
                startActivity(regCancel);
            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }
}
