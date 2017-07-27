package comdbstjdduswkd.naver.httpblog.test1.UserManagement;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import comdbstjdduswkd.naver.httpblog.test1.MainActivity;
import comdbstjdduswkd.naver.httpblog.test1.R;

public class LoginActivity extends AppCompatActivity {

    private Button reg;
    private Button reset;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //++++++++++++++++++++Button Object++++++++++++++++++++++++++
        reg = (Button)findViewById(R.id.register);
        reset = (Button)findViewById(R.id.pwreset);
        login = (Button)findViewById(R.id.login);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //++++++++++++++++++++OnclickListner+++++++++++++++++++++++++
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
                Intent login = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(login);
            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }
}
