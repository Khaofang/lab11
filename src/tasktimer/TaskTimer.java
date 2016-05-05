package tasktimer;

import static java.lang.System.out;
import java.util.Scanner;
import java.io.*;
import java.util.function.IntConsumer;
import java.util.function.Consumer;
import java.util.concurrent.atomic.*;  // hack, using AtomicInteger as accumulator

/**
 * Time how long it takes to perform some tasks
 * using different programming constructs.
 * 
 * TODO Improve this code by restructuring it to eliminate duplicate code.
 */
public class TaskTimer
{
	// Limit number of words read.  Otherwise, the next task could be very sloooow.
    static final int MAXCOUNT = 50_000;
    
	/**
     * Process all the words in a file using Scanner to read and parse input.
     * Display summary statistics and elapsed time.
     */
	static class Task1 implements Runnable {		
		private Scanner in;
		
		public Task1() {
			in = new Scanner(Dictionary.getWordsAsStream());
		}
		
		public void run() {
	        // perform the task
	        int count = 0;
	        long totalsize = 0;
	        while(in.hasNext()) {
	            String word = in.next();
	            totalsize += word.length();
	            count++;
	        }
	        double averageLength = ((double)totalsize)/(count>0 ? count : 1);
	        out.printf("Average length of %,d words is %.2f\n", count, averageLength);
		}
		
		@Override
		public String toString() {
			return "read words using Scanner and a while loop";
		}
    }
    
    /**
     * Process all the words in a file (one word per line) using BufferedReader
     * and the readLine() method.  readLine() returns null when there is no more input.
     * Display summary statistics and elapsed time.
     */
	static class Task2 implements Runnable {
		private BufferedReader br;

		public Task2() {
			br = null;
	        try {
	            br = new BufferedReader( new InputStreamReader(Dictionary.getWordsAsStream()) );
	        } catch (Exception ex) {
	            out.println("Could not open dictionary: "+ex.getMessage());
	            return;
	        }
		}
		
		public void run() {
	        try {
	            int count = 0;
	            long totalsize = 0;
	            String word = null;
	            while( (word=br.readLine()) != null ) {
	                totalsize += word.length();
	                count++;
	            }
	            double averageLength = ((double)totalsize)/(count>0 ? count : 1);
	            out.printf("Average length of %,d words is %.2f\n", count, averageLength);  
	        } catch(IOException ioe) {
	            out.println(ioe);
	            return;
	        } finally {
	            try { br.close(); } catch (Exception ex) { /* ignore it */ }
	        }
		}
		
		@Override
		public String toString() {
			return "read words using BufferedReader.readLine() with a loop";
		}
	}
    
    /**
     * Process all the words in a file (one word per line) using BufferedReader
     * and the lines() method which creates a Stream of Strings (one item per line).  
     * Then use the stream to compute summary statistics.
     * In a lambda you cannot access a local variable unless it is final,
     * so (as a cludge) we use an attribute for the count.
     * When this method is rewritten as a Runnable, it can be a non-static attribute
     * of the runnable.
     * Display summary statistics and elapsed time.
     */
    static class Task3 implements Runnable {
        BufferedReader br;
        
        public Task3() {
        	br = null;
        	try {
                br = new BufferedReader( new InputStreamReader(Dictionary.getWordsAsStream()) );
            } catch (Exception ex) {
                out.println("Could not open dictionary: "+ex.getMessage());
                return;
            }
        }
        
        public void run() {
            long totalsize = 0;
            long count = 0;
            // This code uses Java's IntStream.average() method.
            // But there is no way to also get the count of items.
            // averageLength = br.lines().mapToInt( (word) -> word.length() )
            //                         .average().getAsDouble();
            // So instead we write out own IntConsumer to count and average the stream,
            // and use our IntConsumer to "consume" the stream.
            IntCounter counter = new IntCounter();
            br.lines().mapToInt( word -> word.length() ).forEach( counter );
            // close the input
            try {
                br.close();
            } catch(IOException ex) { /* ignore it */ }
            out.printf("Average length of %,d words is %.2f\n",
                    counter.getCount(), counter.average() );
        }
        
        public String toString() {
        	return "read words using BufferedReader and Stream";
        }
    }
    
    /**
     * Process all the words in a file (one word per line) using BufferedReader
     * and the lines() method which creates a Stream of Strings (one item per line).  
     * Then use the stream to compute summary statistics.
     * This is same as task3, except we use a Collector instead of Consumer.
     */
    static class Task4 implements Runnable {
    	private BufferedReader br;
    	
    	public Task4() {
    		br = null;
            try {
                br = new BufferedReader( new InputStreamReader(Dictionary.getWordsAsStream()) );
            } catch (Exception ex) {
                out.println("Could not open dictionary: "+ex.getMessage());
                return;
            }
    	}
    	
    	public void run() {
            // We want the Consumer to add to the count and total length,
            // but a Lambda can only access local variables (from surrounding scope) if
            // they are final.  That means, we can't use an int, long, or double variable. 
            // So, use AtomicInteger and AtomicLong, which are mutable objects.
            final AtomicLong total = new AtomicLong();
            final AtomicInteger counter = new AtomicInteger();
            //TODO Use a Collector instead of Consumer
            Consumer<String> consumer = new Consumer<String>() {
                public void accept(String word) {
                    total.getAndAdd( word.length() );
                    counter.incrementAndGet();
                }
            };
                    
            br.lines().forEach( consumer );  // Ha! No loop.
            // close the input
            try { br.close(); } catch(IOException ex) { /* ignore it */ }
            
            int count = counter.intValue();
            double averageLength = (count > 0) ? total.doubleValue()/count : 0.0;
            out.printf("Average length of %,d words is %.2f\n", count, averageLength );
    	}
    	
    	public String toString() {
    		return "read words using BufferedReader and Stream with Collector";
    	}
    }
    
    /** 
     * Append all the words from the dictionary to a String.
     * This shows why you should be careful about using "string1"+"string2".
     */
    static class Task5 implements Runnable {
        private BufferedReader br;
        
        public Task5() {
        	br = null;
        	try {
                br = new BufferedReader( new InputStreamReader(Dictionary.getWordsAsStream()) );
            } catch (Exception ex) {
                out.println("Could not open dictionary: "+ex.getMessage());
                return;
            }
        }
        
        public void run() {
        	String result = "";
            String word = null;
            int count = 0;
            try {
                while( (word=br.readLine()) != null && count < MAXCOUNT) {
                    result = result + word;
                    count++;
                }
            } catch(IOException ioe) { System.out.println( ioe.getMessage() ); }
            System.out.printf("Done appending %d words to string.\n", count);
        }
        
        public String toString() {
        	return "append "+MAXCOUNT+" words to a String using +";
        }
    }
    
    /** 
     * Append all the words from the dictionary to a StringBuilder.
     * Compare how long this takes to appending to String.
     */
    static class Task6 {
        private BufferedReader br;
        
        public Task6() {
        	br = null;
        	try {
                br = new BufferedReader( new InputStreamReader(Dictionary.getWordsAsStream()) );
            } catch (Exception ex) {
                out.println("Could not open dictionary: "+ex.getMessage() );
                return;
            }		
        }
        
        public void run() {
        	StringBuilder result = new StringBuilder();
            String word = null;
            int count = 0;
            try {
                while( (word=br.readLine()) != null  && count < MAXCOUNT) {
                    result.append(word);
                    count++;
                }
            } catch(IOException ioe) { System.out.println( ioe.getMessage() ); }
            System.out.printf("Done appending %d words to StringBuilder.\n", count);
        }
        
        public String toString() {
        	return "append "+MAXCOUNT+" words to a StringBuilder";
        }
    }
    
    /** 
     * Define a customer Consumer class that computes <b>both</b> the average 
     * and count of values.
     * An IntConsumer is a special Consumer interface the has an 'int' parameter 
     * in accept().
     */
    static class IntCounter implements IntConsumer {
        // count the values
        public int count = 0;
        // total of the values
        private long total = 0;
        /** accept consumes an int. In this method, count the value and add it to total. */
        public void accept(int value) { count++; total += value; }
        /** Get the average of all the values consumed. */
        public double average() { 
            return (count>0) ? ((double)total)/count : 0.0;
        }
        public int getCount() { return count; }
    }

    public static void execAndPrint(Runnable task) {
    	out.println("Starting task: "+task.toString());
    	long starttime, stoptime;
    	starttime = System.nanoTime();
    	task.run();
    	stoptime = System.nanoTime();
    	out.printf("Elapsed time is %f sec\n",(stoptime - starttime)*1.0E-9 );
    }
        
        
    /** Run all the tasks. */
    public static void main(String [] args) {
        //task1();
    	execAndPrint(new Task1());
        execAndPrint(new Task2());
        execAndPrint(new Task3());
        execAndPrint(new Task4());
        execAndPrint(new Task5());
        task6();
    }
}
