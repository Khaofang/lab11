package tasktimer;

public class StopWatch {
	private long starttime, stoptime;
	private boolean running;
	
	public StopWatch() {
		starttime = 0;
		stoptime = 0;
		running = false;
	}
	
	public void start() {
		running = true;
		starttime = System.nanoTime();
	}
	
	public void stop() {
		stoptime = System.nanoTime();
		running = false;
	}
	
	public double getElapsed() {
		return stoptime-starttime;
	}
}
