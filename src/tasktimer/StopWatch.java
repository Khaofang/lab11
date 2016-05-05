package tasktimer;

/**
 * Stop watch that be used to find time of running some method or class.
 * @author Chayanin Punjakunaporn
 *
 */
public class StopWatch {
	/** Attribute */
	private long starttime, stoptime;
	private boolean running;
	
	/** Constructor */
	public StopWatch() {
		starttime = 0;
		stoptime = 0;
		running = false;
	}
	/** Start stop watching for giving start time */
	public void start() {
		running = true;
		starttime = System.nanoTime();
	}
	
	/** Stop stop watching for finding stop time */
	public void stop() {
		stoptime = System.nanoTime();
		running = false;
	}
	
	/** 
	 * Get time that be used in this running. 
	 * @return difference between start and stop time.
	 */
	public double getElapsed() {
		return stoptime-starttime;
	}
}
