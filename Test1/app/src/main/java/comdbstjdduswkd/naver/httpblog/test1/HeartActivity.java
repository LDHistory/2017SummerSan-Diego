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

    public HeartActivity(){}

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View File Object
        //view = inflater.inflate(R.layout.activity_heart, container, false);

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
                    heart.setImageResource(R.drawable.human_fast2);
                else if(msg.what >= 50)
                    heart.setImageResource(R.drawable.human_nomal2);
                else
                    heart.setImageResource(R.drawable.human_slow2);
            }
        };

        //HeartBit GIF
        Glide.with(this).load(R.raw.heartbit).into(heartBitget);
        return view;
        //return inflater.inflate(R.layout.fragment_heart, container, false);
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
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        ChangeHeart.start();
    }
}
