package comdbstjdduswkd.naver.httpblog.test1.UserManagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import comdbstjdduswkd.naver.httpblog.test1.R;

public class ResetPassActivity extends AppCompatActivity {

    private TextView CheckCode, NewPassword, ConfirmPass;
    private Button Change;
    private  String code, pass, SendResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        CheckCode = (TextView)findViewById(R.id.checkcode);
        NewPassword = (TextView)findViewById(R.id.newpassword);
        ConfirmPass = (TextView)findViewById(R.id.confirmpass);

        Change = (Button)findViewById(R.id.change);

        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = CheckCode.getText().toString();
                pass = NewPassword.getText().toString();
                if (!CheckCode.getText().toString().equals("") && !NewPassword.getText().toString().equals("")
                        && !ConfirmPass.getText().toString().equals("")) {
                    if (NewPassword.getText().toString().equals(ConfirmPass.getText().toString())) {
                        if (!HttpIDCheck()) {
                            Intent main = new Intent(ResetPassActivity.this, LoginActivity.class);
                            startActivity(main);
                            Toast.makeText(ResetPassActivity.this, "Successfully change your password!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else
                        Toast.makeText(ResetPassActivity.this, "Please, Check your Confirm Password.", Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(ResetPassActivity.this, "Please, Write all information.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean HttpIDCheck() {
        boolean check = false;
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/slim-api/appreset-password-change");       // URL 설정
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
            buffer.append("token").append("=").append(code).append("&");                 // php 변수에 값 대입
            buffer.append("password").append("=").append(pass);

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
            SendResult = builder.toString();                       // 전송결과를 전역 변수에 저장
            try {
                JSONObject jsonObject = new JSONObject(SendResult);
                if (jsonObject.getString("status").equals("true")) {
                    check = true;
                }else
                    Toast.makeText(ResetPassActivity.this, "Please, Check your code and Password.", Toast.LENGTH_LONG).show();
                check = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } // try
        return check;
    }
}
