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
    float index, i_high, i_low, c_high, c_low;

    public float IndexCal(float c){
        index = ((i_high-i_low)/(c_high-c_low))*(c - c_low)+i_low;
        return index;
    }
    public float O3(float o3){
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
        return index;
    }

    public float PM25(float pm25){
        if( 0.0 <= pm25&&pm25 <= 12.0 ){
            c_low= (float) 0.0; c_high= (float) 12.0;
            i_low=0; i_high=50;
        }else if( 12.1 <= pm25&&pm25 <=35.4 ){
            c_low= (float) 12.1; c_high= (float) 35.4;
            i_low=51; i_high=100;
        }else if( 35.5 <= pm25&&pm25 <= 55.4 ){
            c_low= (float) 35.5; c_high= (float) 55.4;
            i_low=101; i_high=150;
        }else if( 55.5 <= pm25&&pm25 <= 150.4 ){
            c_low= (float) 55.5; c_high= (float) 150.4;
            i_low=151; i_high=200;
        }else if( 150.5 <= pm25&&pm25 <=250.4 ){
            c_low= (float) 150.5; c_high= (float) 250.4;
            i_low=201; i_high=300;
        }else if( 250.5 <= pm25 && pm25 <= 350.4){
            c_low= (float) 150.5; c_high= (float) 250.4;
            i_low=301; i_high=400;
        }else if( 350.5 <= pm25 && pm25 <= 500.4){
            c_low= (float) 350.5; c_high= (float) 500.4;
            i_low=401; i_high=500;
        }
        index = ((i_high-i_low)/(c_high-c_low))*(pm25 - c_low)+i_low;
        return index;
    }

    public float CO(float co){
        if( 0.0 <= co&&co <= 4.4 ){
            c_low= (float) 0.0; c_high= (float) 4.4;
            i_low=0; i_high=50;
        }else if( 4.5 <= co&&co <=9.4 ){
            c_low= (float) 4.5; c_high= (float) 9.4;
            i_low=51; i_high=100;
        }else if( 9.5 <= co&&co <= 12.4 ){
            c_low= (float) 9.5; c_high= (float) 12.4;
            i_low=101; i_high=150;
        }else if( 12.4 <= co&&co <= 15.4 ){
            c_low= (float) 12.4; c_high= (float) 15.4;
            i_low=151; i_high=200;
        }else if( 15.5 <= co&&co <=30.4 ){
            c_low= (float) 15.5; c_high= (float) 30.4;
            i_low=201; i_high=300;
        }else if( 30.5 <= co && co <= 40.4){
            c_low= (float) 30.5; c_high= (float) 40.4;
            i_low=301; i_high=400;
        }else if( 40.5 <= co && co <= 50.4){
            c_low= (float) 350.5; c_high= (float) 500.4;
            i_low=401; i_high=500;
        }
        index = ((i_high-i_low)/(c_high-c_low))*(co - c_low)+i_low;
        return index;
    }

    public float SO2(float so2){
        if( 0 <= so2&&so2 <= 35 ){
            c_low= 0; c_high= 35;
            i_low=0; i_high=50;
        }else if( 36 <= so2&&so2 <=75 ){
            c_low= 36; c_high= 75;
            i_low=51; i_high=100;
        }else if( 76 <= so2&&so2 <= 185 ){
            c_low= 76; c_high= 185;
            i_low=101; i_high=150;
        }else if( 186 <= so2&&so2 <= 304 ){
            c_low= 186; c_high= 304;
            i_low=151; i_high=200;
        }else if( 305 <= so2&&so2 <=604 ){
            c_low= 305; c_high= 604;
            i_low=201; i_high=300;
        }else if( 605 <= so2 && so2 <= 804){
            c_low= 605; c_high= 804;
            i_low=301; i_high=400;
        }else if( 805 <= so2 && so2 <= 1004){
            c_low= 805; c_high= 1004;
            i_low=401; i_high=500;
        }
        index = ((i_high-i_low)/(c_high-c_low))*(so2 - c_low)+i_low;
        return index;
    }

    public float NO2(float no2){
        if( 0 <= no2&&no2 <= 53 ){
            c_low= 0; c_high= 53;
            i_low=0; i_high=50;
        }else if( 54 <= no2&&no2 <=100 ){
            c_low= 54; c_high= 100;
            i_low=51; i_high=100;
        }else if( 101 <= no2&&no2 <= 360 ){
            c_low= 101; c_high= 360;
            i_low=101; i_high=150;
        }else if( 361 <= no2&&no2 <= 649 ){
            c_low= 361; c_high= 649;
            i_low=151; i_high=200;
        }else if( 650 <= no2&&no2 <=1249 ){
            c_low= 650; c_high= 1249;
            i_low=201; i_high=300;
        }else if( 1250 <= no2 && no2 <= 1649){
            c_low= 1250; c_high= 1649;
            i_low=301; i_high=400;
        }else if( 1650 <= no2 && no2 <= 2049){
            c_low= 1650; c_high= 2049;
            i_low=401; i_high=500;
        }
        index = ((i_high-i_low)/(c_high-c_low))*(no2 - c_low)+i_low;
        return index;
    }
}
