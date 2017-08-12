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
            //   set the URL and connect with server
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/slim-api/appuser-info");       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // connect to server
            //--------------------------
            //   Set the transfer mode (basical settings)
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST

            // Tell the server to process it the same way that the value passed from the Web to <Form>.
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //--------------------------
            //   Data transfer to server
            //--------------------------

            StringBuffer buffer = new StringBuffer();
            buffer.append("email").append("=").append(ID).append("&");       // Assign values to php variables
            buffer.append("password").append("=").append(PASS);             // Do not put '$' in front of php variable.

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
            while ((str = reader.readLine()) != null) {       // It will be sent line by line from the server, so read it line by line.
                builder.append(str + "\n");                    // Add line separator for display in View.
            }
            status = builder.toString();                       // Store transmission results in global variables.
            Log.e("status : ",""+status);
            JSONObject jsonObject = new JSONObject(status);
            if(jsonObject.getString("status").equals("true")){
                usernum = jsonObject.getInt("num");
                Log.e("User number : ", "" + usernum);
                Intent startup = new Intent(LoginActivity.this, MainActivity.class);
                startup.putExtra("ID",ID);
                startActivity(startup);
                Log.e("Login result : ", "success");
            }else if(jsonObject.getString("status").equals("false")){
                Toast.makeText(this, "Check your ID or Password !", Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){
            Toast.makeText(this, "Server connection error!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
