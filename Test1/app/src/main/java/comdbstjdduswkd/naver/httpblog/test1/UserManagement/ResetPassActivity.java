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
                            HttpIDCheck();
                        }
                }else
                    Toast.makeText(ResetPassActivity.this, "Please, Write all information.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void HttpIDCheck() {
        try {
            //--------------------------
            //   set the URL and connect with server
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/slim-api/appreset-password-change");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
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
            buffer.append("token").append("=").append(code).append("&");
            buffer.append("password").append("=").append(pass);

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
            while ((str = reader.readLine()) != null) {         // It will be sent line by line from the server, so read it line by line.
                builder.append(str + "\n");                     // Add line separator for display in View.
            }
            SendResult = builder.toString();                   // Store transmission results in global variables.
            try {
                JSONObject jsonObject = new JSONObject(SendResult);
                if (jsonObject.getString("status").equals("true")) {
                    Intent main = new Intent(ResetPassActivity.this, LoginActivity.class);
                    startActivity(main);
                    Toast.makeText(ResetPassActivity.this, "Successfully change your password!", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(ResetPassActivity.this, "Please, Check your code and Password.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(ResetPassActivity.this, "Server connection error!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            Toast.makeText(ResetPassActivity.this, "Server connection error!", Toast.LENGTH_LONG).show();
            //
        } catch (IOException e) {
            Toast.makeText(ResetPassActivity.this, "Server connection error!", Toast.LENGTH_LONG).show();
        }
    }
}
