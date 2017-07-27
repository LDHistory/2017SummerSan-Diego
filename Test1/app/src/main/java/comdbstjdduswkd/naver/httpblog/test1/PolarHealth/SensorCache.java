package comdbstjdduswkd.naver.httpblog.test1.PolarHealth;

import comdbstjdduswkd.naver.httpblog.test1.BioHarnessSessionData;

public class SensorCache { //for Polar Connection code
	public BioHarnessSessionData bioHarnessSessionData = new BioHarnessSessionData();
	private static SensorCache instance = new SensorCache();
	public static SensorCache getInstance() {
		return instance;
	}
}
