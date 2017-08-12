package comdbstjdduswkd.naver.httpblog.test1.UserManagement;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import comdbstjdduswkd.naver.httpblog.test1.MainActivity;
import comdbstjdduswkd.naver.httpblog.test1.R;

public class ResetActivity extends AppCompatActivity {

    private Button resetSend, resetCancel;
    private TextView emailText;
    String IDcheckResult, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        //Check policy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //+++++++++++++++++++Button Object++++++++++++++++++++++
        resetSend = (Button) findViewById(R.id.okbtn);
        resetCancel = (Button) findViewById(R.id.resetCancel);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //+++++++++++++++++++Button Object++++++++++++++++++++++
        emailText = (TextView) findViewById(R.id.emailtext);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //++++++++++++++++++++OnclickListner+++++++++++++++++++++++++
        resetSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailText.getText().toString();
                if(!email.equals(""))
                    HttpIDCheck();
                else
                    Toast.makeText(ResetActivity.this, "Please, Write your email.", Toast.LENGTH_LONG).show();
            }
        });

        resetCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }

    public void HttpIDCheck() {
        try {
            //--------------------------
            //   set the URL and connect with server
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/slim-api/appreset-password");       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   Set the transfer mode (basical settings)
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");

            // Tell the server to process it the same way that the value passed from the Web to <Form>.
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //--------------------------
            //   Data send to server. (URL Tag protocol)
            //--------------------------
            StringBuffer buffer = new StringBuffer();
            buffer.append("email").append("=").append(email);

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            //--------------------------
            //   Data received from server
            //--------------------------
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {        // It will be sent line by line from the server, so read it line by line.
                builder.append(str + "\n");                     // Add line separator for display in View.
            }
            IDcheckResult = builder.toString();                // Store transmission results in global variables.
            try {
                JSONObject jsonObject = new JSONObject(IDcheckResult);
                if (jsonObject.getString("status").equals("true")) {
                    Intent setpass = new Intent(ResetActivity.this, ResetPassActivity.class);
                    startActivity(setpass);
                    finish();
                }else if(jsonObject.getString("status").equals("false")){
                    Toast.makeText(ResetActivity.this, "This email doesn't exist.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }
}
