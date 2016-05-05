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
    private static String DICTIONARY = "wordlist.txt";
    /**
     * Process all the words in a file using Scanner to read and parse input.
     * Display summary statistics and elapsed time.
     */
    public static void task1() {
        // initialize: open the words file as InputStream
        InputStream instream = TaskTimer.class.getClassLoader().getResourceAsStream(DICTIONARY);
        Scanner in = new Scanner(instream);
        
        out.println("Starting task: read words using Scanner and a while loop");
        long starttime = System.nanoTime();
        // perform the task
        int count = 0;
        long totalsize = 0;
        while(in.hasNext()) {
            String word = in.next();
            totalsize += word.length();
            count++;
        }
        double averageLength = ((double)totalsize)/(count>0 ? count : 1);
        long stoptime = System.nanoTime();
        out.printf("Average length of %,d words is %.2f\n", count, averageLength);
        out.printf("Elapsed time is %f sec\n",(stoptime - starttime)*1.0E-9 );
    }
    
    /**
     * Process all the words in a file (one word per line) using BufferedReader
     * and the readLine() method.  readLine() returns null when there is no more input.
     * Display summary statistics and elapsed time.
     */
    public static void task2( ) {
        // initialize: open the words file as InputStream
        InputStream instream = TaskTimer.class.getClassLoader().getResourceAsStream(DICTIONARY);
        BufferedReader br = null;
        try {
            br = new BufferedReader( new InputStreamReader(instream) );
        } catch (Exception ex) {
            out.println("Could not open dictionary: "+ex.getMessage());
            return;
        }
        out.println("Starting task: read words using BufferedReader.readLine() with a loop");
        long starttime = System.nanoTime();
        
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
        long stoptime = System.nanoTime();
        out.printf("Elapsed time is %f sec\n",(stoptime - starttime)*1.0E-9 );
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
    public static void task3( ) {
        // initialize: open the words file as InputStream
        InputStream instream = TaskTimer.class.getClassLoader().getResourceAsStream(DICTIONARY);
        BufferedReader br = null;
        try {
            br = new BufferedReader( new InputStreamReader(instream) );
        } catch (Exception ex) {
            out.println("Could not open dictionary: "+ex.getMessage());
            return;
        }
        
        out.println("Starting task: read words using BufferedReader and Stream");
        long starttime = System.nanoTime();
        
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
            
        long stoptime = System.nanoTime();
        out.printf("Elapsed time is %f sec\n",(stoptime - starttime)*1.0E-9 );
        
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
    
    /**
     * Process all the words in a file (one word per line) using BufferedReader
     * and the lines() method which creates a Stream of Strings (one item per line).  
     * Then use the stream to compute summary statistics.
     * This is same as task3, except we use a Collector instead of Consumer.
     */
    public static void task4( ) {
        // initialize
        InputStream instream = TaskTimer.class.getClassLoader().getResourceAsStream(DICTIONARY);
        BufferedReader br = null;
        try {
            br = new BufferedReader( new InputStreamReader(instream) );
        } catch (Exception ex) {
            out.println("Could not open dictionary: "+ex.getMessage());
            return;
        }
        
        out.println("Starting task: read words using BufferedReader and Stream with Collector");
        long starttime = System.nanoTime();
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
            
        long stoptime = System.nanoTime();
        out.printf("Elapsed time is %f sec\n",(stoptime - starttime)*1.0E-9 );  
    }
    
    // Limit number of words read.  Otherwise, the next task could be very sloooow.
    static final int MAXCOUNT = 50_000;
    
    /** 
     * Append all the words from the dictionary to a String.
     * This shows why you should be careful about using "string1"+"string2".
     */
    public static void task5( ) {
        // initialize
        InputStream instream = TaskTimer.class.getClassLoader().getResourceAsStream(DICTIONARY);
        BufferedReader br = null;
        try {
            br = new BufferedReader( new InputStreamReader(instream) );
        } catch (Exception ex) {
            out.println("Could not open dictionary: "+ex.getMessage());
            return;
        }
        
        out.println("Starting task: append "+MAXCOUNT+" words to a String using +");
        long starttime = System.nanoTime();
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
        long stoptime = System.nanoTime();
        out.printf("Elapsed time is %f sec\n",(stoptime - starttime)*1.0E-9 );
    }
    
    /** 
     * Append all the words from the dictionary to a StringBuilder.
     * Compare how long this takes to appending to String.
     */
    public static void task6( ) {
        // initialize
        InputStream instream = TaskTimer.class.getClassLoader().getResourceAsStream(DICTIONARY);
        BufferedReader br = null;
        try {
            br = new BufferedReader( new InputStreamReader(instream) );
        } catch (Exception ex) {
            out.println("Could not open dictionary: "+ex.getMessage() );
            return;
        }
        
        out.println("Starting task: append "+MAXCOUNT+" words to a StringBuilder");
        long starttime = System.nanoTime();
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
        long stoptime = System.nanoTime();
        out.printf("Elapsed time is %f sec\n",(stoptime - starttime)*1.0E-9 );
    }
        
        
    /** Run all the tasks. */
    public static void main(String [] args) {
        task1();
        task2();
        task3();
        task4();
        task5();
        task6();
    }
    
}
