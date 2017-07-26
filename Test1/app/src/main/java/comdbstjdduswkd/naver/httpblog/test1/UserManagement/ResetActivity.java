package comdbstjdduswkd.naver.httpblog.test1.UserManagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import comdbstjdduswkd.naver.httpblog.test1.MainActivity;
import comdbstjdduswkd.naver.httpblog.test1.R;

public class ResetActivity extends AppCompatActivity {

    private Button resetSend, resetCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        //+++++++++++++++++++Button Object++++++++++++++++++++++
        resetSend = (Button)findViewById(R.id.resetSend);
        resetCancel = (Button)findViewById(R.id.resetCancel);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //++++++++++++++++++++OnclickListner+++++++++++++++++++++++++
        resetSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        resetCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resetCancel = new Intent(ResetActivity.this, MainActivity.class);
                startActivity(resetCancel);
            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }
}
