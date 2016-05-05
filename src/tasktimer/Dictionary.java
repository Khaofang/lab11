package tasktimer;

import java.io.*;

/** 
 * Dictionary that return input stream from wordlist.txt
 * @author Chayanin Punjakunaporn
 */
public class Dictionary {
	
	/**
	 * Get input stream
	 * @return input stream from wordlist.txt
	 */
	public static InputStream getWordsAsStream() {
		return TaskTimer.class.getClassLoader().getResourceAsStream("wordlist.txt");
	}
}
