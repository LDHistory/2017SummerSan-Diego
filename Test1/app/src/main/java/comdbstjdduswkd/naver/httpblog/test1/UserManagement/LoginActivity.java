package comdbstjdduswkd.naver.httpblog.test1.UserManagement;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import comdbstjdduswkd.naver.httpblog.test1.MainActivity;
import comdbstjdduswkd.naver.httpblog.test1.R;

public class LoginActivity extends AppCompatActivity {

    private Button reg;
    private Button reset;
    private Button login;
    private TextView id, pass;
    private CheckBox save;
    private String ID,PASS, status;

    public static int usernum;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sharedpreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        //++++++++++++++++++++Button Object++++++++++++++++++++++++++
        reg = (Button)findViewById(R.id.register);
        reset = (Button)findViewById(R.id.pwreset);
        login = (Button)findViewById(R.id.login);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        id = (TextView)findViewById(R.id.email);
        pass = (TextView)findViewById(R.id.password);

        save = (CheckBox)findViewById(R.id.saveaccount);

        if(sharedpreferences.getBoolean("CHECK", false)){
            id.setText(sharedpreferences.getString("ID", ""));
            pass.setText(sharedpreferences.getString("PASS",""));
            save.setChecked(true);
        }

        //++++++++++++++++++++OnclickListner+++++++++++++++++++++++++
        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText().toString().equals("Input your E-mail"))
                    id.setText("");
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(reg);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reset = new Intent(LoginActivity.this, ResetActivity.class);
                startActivity(reset);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(save.isChecked()) {
                    ID = id.getText().toString();
                    PASS = pass.getText().toString();
                    editor.putString("ID", ID);
                    editor.putString("PASS", PASS);
                    editor.putBoolean("CHECK", true);
                    editor.commit();
                    if(ID.equals("") || PASS.equals("")){
                        Toast.makeText(LoginActivity.this, "Fill out your ID or Password!", Toast.LENGTH_SHORT).show();
                    }else {
                        HttpPostData();
                    }
                }else {
                    editor.clear();
                    editor.commit();
                    ID = id.getText().toString();
                    PASS = pass.getText().toString();
                    if(ID.equals("") || PASS.equals("")){
                        Toast.makeText(LoginActivity.this, "Fill out your ID or Password!", Toast.LENGTH_SHORT).show();
                    }else {
                        HttpPostData();
                    }
                }
            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }
    public void HttpPostData() {
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/slim-api/appuser-info");       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // connect to server
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
            buffer.append("email").append("=").append(ID).append("&");       // php 변수에 값 대입
            buffer.append("password").append("=").append(PASS); // php 변수 앞에 '$' 붙이지 않는다

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
            status = builder.toString();                       // 전송결과를 전역 변수에 저장
            Log.e("ㅅㅅㅅㅅ",""+status);
            JSONObject jsonObject = new JSONObject(status);
            if(jsonObject.getString("status").equals("true")){
                usernum = jsonObject.getInt("num");
                Log.e("test", "" + usernum);
                Intent startup = new Intent(LoginActivity.this, MainActivity.class);
                startup.putExtra("ID",ID);
                startActivity(startup);
                Log.e("test", "success");
            }else if(jsonObject.getString("status").equals("false")){
                Toast.makeText(this, "Check your ID or Password !", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
            //((TextView)(findViewById(R.id.text_result))).setText(myResult);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        } catch(Exception e){
            Toast.makeText(this, "Server connection error!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
