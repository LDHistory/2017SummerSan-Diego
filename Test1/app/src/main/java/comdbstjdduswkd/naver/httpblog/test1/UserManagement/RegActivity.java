package comdbstjdduswkd.naver.httpblog.test1.UserManagement;


import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import comdbstjdduswkd.naver.httpblog.test1.MainActivity;
import comdbstjdduswkd.naver.httpblog.test1.R;

public class RegActivity extends AppCompatActivity {

    private Button RegSend, Regcancel, chkBtn;
    //private Spinner Year, Month, Day;
    String email, pw, chkpw, fname, lname, regResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_reg);

        //+++++++++++++++++++Button Object++++++++++++++++++++++
        chkBtn = (Button)findViewById(R.id.chkBtn);
        RegSend = (Button)findViewById(R.id.regsubmit);
        Regcancel = (Button)findViewById(R.id.regCancel);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++
        /*
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
        */
        //++++++++++++++++++++OnclickListner+++++++++++++++++++++++++
        chkBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });
        RegSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = ((EditText)findViewById(R.id.inputemail)).getText().toString();
                pw = ((EditText)findViewById(R.id.inputpw)).getText().toString();
                chkpw = ((EditText)findViewById(R.id.inputchkpw)).getText().toString();
                fname = ((EditText)findViewById(R.id.inputLname)).getText().toString();
                lname = ((EditText)findViewById(R.id.inputFname)).getText().toString();
                if(pw.equals(chkpw)){
                    HttpPostData();   // communicate with Web server transfer & receive response
                }else{
                    Toast.makeText(RegActivity.this, "Please check your password", Toast.LENGTH_LONG).show();
                }
            }
        });

        Regcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }
    public void HttpPostData() {
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/app_user_information_command.php");       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST

            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //--------------------------
            //   서버로 값 전송
            //--------------------------
            StringBuffer buffer = new StringBuffer();
            buffer.append("email").append("=").append(email).append("&");                 // php 변수에 값 대입
            buffer.append("password").append("=").append(pw).append("&");   // php 변수 앞에 '$' 붙이지 않는다
            buffer.append("fname").append("=").append(fname).append("&");           // 변수 구분은 '&' 사용
            buffer.append("lname").append("=").append(lname);

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
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
            regResult = builder.toString();                       // 전송결과를 전역 변수에 저장
            //((TextView)(findViewById(R.id.text_result))).setText(myResult);
            Toast.makeText(this, "Successful sign up! :) \n"+regResult,Toast.LENGTH_LONG).show();
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } // try
    }
}
