package comdbstjdduswkd.naver.httpblog.test1;

/**
 * Created by USER on 2017-08-06.
 */

public class AQIcalculate {

    private static final String GOOD = "good";
    private static final String MODERATE = "moderate";
    private static final String UNHEALTHY_FOR_SENSITIVE_GROUPS = "unheathy1";
    private static final String UNHEALTHY = "unhealthy2";
    private static final String VERY_UNHEALTHY = "vunhealthy";
    private static final String HARZARDOUS = "harzardous";
    double index, i_high, i_low, c_high, c_low;

    public double IndexCal(double c){
        index = ((i_high-i_low)/(c_high-c_low))*(c - c_low)+i_low;
        return index;
    }
    public void O3(double o3){
        if( 0 <= o3&&o3 <= 54 ){
            c_low=0; c_high=54;
            i_low=0; i_high=50;
        }else if( 55 <= o3&&o3 <=70 ){
            c_low=55; c_high=70;
            i_low=51; i_high=100;
        }else if( 71 <= o3&&o3 <= 85 ){
            c_low=71; c_high=85;
            i_low=101; i_high=150;
        }else if( 86 <= o3&&o3 <= 105 ){
            c_low=86; c_high=105;
            i_low=151; i_high=200;
        }else if( 106 <= o3&&o3 <=200 ){
            c_low=106; c_high=200;
            i_low=201; i_high=300;
        }
        index = ((i_high-i_low)/(c_high-c_low))*(o3 - c_low)+i_low;
        return ;
    }
}
