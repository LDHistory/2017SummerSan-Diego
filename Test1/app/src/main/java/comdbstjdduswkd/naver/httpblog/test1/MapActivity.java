package comdbstjdduswkd.naver.httpblog.test1;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by USER on 2017-07-14.
 */

public class MapActivity extends Fragment {
    View view;
    Button bt1;
    TextView tv1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_layout, container, false);
        bt1 = (Button)view.findViewById(R.id.bt1);
        tv1 = (TextView)view.findViewById(R.id.tv1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv1.setText("sㅁㄴㅇㄻ낭럼닝ㄹ");
            }
        });
        return view;
    }


}
