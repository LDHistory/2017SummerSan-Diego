package comdbstjdduswkd.naver.httpblog.test1.UserManagement;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import comdbstjdduswkd.naver.httpblog.test1.MainActivity;
import comdbstjdduswkd.naver.httpblog.test1.R;

public class LoginActivity extends AppCompatActivity {

    private Button reg;
    private Button reset;
    private Button login;
    private TextView id, pass;
    private CheckBox save;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                    Intent login = new Intent(LoginActivity.this, MainActivity.class);
                    String ID = id.getText().toString();
                    String PASS = pass.getText().toString();
                    editor.putString("ID", ID);
                    editor.putString("PASS", PASS);
                    editor.putBoolean("CHECK", true);
                    editor.commit();
                    startActivity(login);
                }else {
                    editor.clear();
                    editor.commit();
                    Intent login = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(login);
                }
            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }
}
