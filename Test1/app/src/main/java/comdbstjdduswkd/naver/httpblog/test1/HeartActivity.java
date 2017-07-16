package comdbstjdduswkd.naver.httpblog.test1;

import android.app.Fragment;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class HeartActivity extends Fragment {
    TextView heartText;
    ImageView heart, heartbit;
    Handler handler;
    GlideDrawableImageViewTarget heartTartget, heartBitget;

    View view; //The varivable to use View before inflation

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_heart, container, false);
        heartText = (TextView)view.findViewById(R.id.heartValue);
        heart =  (ImageView)view.findViewById(R.id.heart);
        heartbit = (ImageView)view.findViewById(R.id.heartbit);

        //GIF File Object
        heartTartget = new GlideDrawableImageViewTarget(heart);
        heartBitget = new GlideDrawableImageViewTarget(heartbit);

        //handelr
        handler = new Handler(){
            public void handleMessage(android.os.Message msg){
                heartText.setText(""+msg.what);
                if(msg.what >= 70)
                    Glide.with(HeartActivity.this).load(R.raw.human_fast2).into(heartTartget);
                else if(msg.what >= 50)
                    Glide.with(HeartActivity.this).load(R.raw.human_nomal2).into(heartTartget);
                else
                    Glide.with(HeartActivity.this).load(R.raw.human_slow2).into(heartTartget);
            }
        };

        //HeartBit GIF
        Glide.with(this).load(R.raw.heartbit).into(heartBitget);
        return view;
    }
    public void onStart(){
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
