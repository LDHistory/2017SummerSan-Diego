package comdbstjdduswkd.naver.httpblog.test1.UserManagement;


import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import comdbstjdduswkd.naver.httpblog.test1.MainActivity;
import comdbstjdduswkd.naver.httpblog.test1.R;

public class RegActivity extends AppCompatActivity {

    private Button RegSend, Regcancel;
    private EditText emailbox;
    private TextView checkView;
    boolean checkflag;
    String email, pw, chkpw, fname, lname, IDcheckResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkflag = false;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_reg);

        //+++++++++++++++++++Button Object++++++++++++++++++++++
        RegSend = (Button)findViewById(R.id.regsubmit);
        Regcancel = (Button)findViewById(R.id.regCancel);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++
        emailbox = (EditText)findViewById(R.id.inputemail);
        checkView = (TextView)findViewById(R.id.checkView);
        //++++++++++++++++++++OnclickListner+++++++++++++++++++++++++
        emailbox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    email = ((EditText)findViewById(R.id.inputemail)).getText().toString();
                    if(!email.equals("")){
                        if(!checkEmailFormat(email)){
                            checkView.setText("Please check E-mail format");
                            checkView.setTextColor(Color.parseColor("#FF0000"));
                            checkflag = false;
                        }else {
                            HttpIDCheck();
                        }
                    }else {
                        Toast.makeText(RegActivity.this, "Please check your E-mail", Toast.LENGTH_LONG).show();
                        checkflag = false;
                    }
                }
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
                if(pw.equals(chkpw) && checkflag && !pw.equals("") && !fname.equals("") && !lname.equals("")){
                    HttpPostData();   // communicate with Web server transfer & receive response
                }else if(!pw.equals(chkpw)){
                    Toast.makeText(RegActivity.this, "Please check your password", Toast.LENGTH_LONG).show();
                }else if(!checkflag){
                    Toast.makeText(RegActivity.this, "Please check your E-mail", Toast.LENGTH_LONG).show();
                }else if(email.equals("") || pw.equals("") || fname.equals("") || !lname.equals("")){
                    Toast.makeText(RegActivity.this, "Please enter without empty spaces.", Toast.LENGTH_LONG).show();
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
    public void HttpIDCheck(){
        try {
            //--------------------------
            //   set the URL and connect with server
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/slim-api/check-email");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            //--------------------------
            //   Set the transfer mode (basical settings)
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");         // The transmission method is POST

            // Tell the server to process it the same way that the value passed from the Web to <Form>.
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //--------------------------
            //   Sending values to the server. (URL Tag protocol)
            //--------------------------
            StringBuffer buffer = new StringBuffer();
            buffer.append("email").append("=").append(email);                 // Assign values to php variables.

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            //--------------------------
            //    Data received from server
            //--------------------------
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {       // It will be sent line by line from the server, so read it line by line.
                builder.append(str + "\n");                    // Add line separator for display in View.
            }
            IDcheckResult = builder.toString();              // Store transmission results in global variables.
            try {
                JSONObject jsonObject = new JSONObject(IDcheckResult);
                if(jsonObject.getString("status").equals("true")){
                    checkView.setTextColor(Color.parseColor("#1DDB16"));
                    checkView.setText("Available E-mail :)");
                    checkflag=true;
                }else if(jsonObject.getString("status").equals("false")){
                    checkView.setTextColor(Color.parseColor("#FF0000"));
                    checkView.setText("Duplicate! please enter another E-mail");
                    checkflag=false;
                }
                //((TextView)(findViewById(R.id.text_result))).setText(myResult);
            }catch (JSONException e){
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } // try
    }

    public void HttpPostData() {
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/slim-api/app-user-signup");       // URL 설정
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
            //   서버로 값 전송 (URL Tag protocol)
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
            //regResult = builder.toString();                       // 전송결과를 전역 변수에 저장
            //((TextView)(findViewById(R.id.text_result))).setText(myResult);
            Toast.makeText(RegActivity.this,"Successfully registered your account! :)",Toast.LENGTH_LONG).show();
            finish();
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } // try
    }

    public static boolean checkEmailFormat(String email){
        String format = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(format);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
    }
}
