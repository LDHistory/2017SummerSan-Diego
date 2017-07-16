package comdbstjdduswkd.naver.httpblog.test1;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.felipecsl.gifimageview.library.GifImageView;

import org.w3c.dom.Text;

public class AnimActivity extends AppCompatActivity {
    TextView heartText;
    ImageView heart, heartbit;
    Handler handler;
    GlideDrawableImageViewTarget heartTartget, heartBitget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);

        heartText = (TextView)findViewById(R.id.heartValue);
        heart =  (ImageView)findViewById(R.id.heart);
        heartbit = (ImageView)findViewById(R.id.heartbit);

        //GIF File Object
        heartTartget = new GlideDrawableImageViewTarget(heart);
        heartBitget = new GlideDrawableImageViewTarget(heartbit);

        //handelr
        handler = new Handler(){
            public void handleMessage(android.os.Message msg){
                heartText.setText(""+msg.what);
                if(msg.what >= 70)
                    Glide.with(AnimActivity.this).load(R.raw.human_fast2).into(heartTartget);
                else if(msg.what >= 50)
                    Glide.with(AnimActivity.this).load(R.raw.human_nomal2).into(heartTartget);
                else
                    Glide.with(AnimActivity.this).load(R.raw.human_slow2).into(heartTartget);
            }
        };

        //HeartBit GIF
        Glide.with(this).load(R.raw.heartbit).into(heartBitget);
    }

    protected void onStart(){
        super.onStart();
        Thread ChangeHeart = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        for(int i = 0; i < 100; i++) {
                            handler.sendEmptyMessage(i);
                            Thread.sleep(100);
                        }
                    }catch (Throwable t){

                    }
                }
            }
        });
        ChangeHeart.start();
    }
}
