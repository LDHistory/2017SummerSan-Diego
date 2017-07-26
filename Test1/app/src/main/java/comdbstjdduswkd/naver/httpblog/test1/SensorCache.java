package comdbstjdduswkd.naver.httpblog.test1;

/**
 * Created by DONGHEE on 2017-07-25.
 */

public class SensorCache {
    public BioHarnessSessionData bioHarnessSessionData = new BioHarnessSessionData();
    private static SensorCache instance = new SensorCache();
    public static SensorCache getInstance(){
        return instance;
    }
}
