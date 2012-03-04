package sender;

public class MyTimer {
	
	long expiers;
	long delay;
	
	public MyTimer(Long delay) {
		this.delay = delay;
	}
	
	public void start() {
		expiers = delay + System.currentTimeMillis();
	}
	
	public boolean isExpired() {
		return expiers < System.currentTimeMillis();
	}
}
