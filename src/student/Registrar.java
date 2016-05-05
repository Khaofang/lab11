package student;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * The Registrar maintains a List of students.
 * This class is a Singleton.
 *
 */
public class Registrar {
	private static final String STUDENT_URL = "http://se.cpe.ku.ac.th/students.csv";
	// separator character in the student csv file
	private static final String SEPARATOR = ",";
	private static Registrar registrar = null;
	private List<Student> students;
	
	
	private Registrar() {
		students = new ArrayList<Student>();
		// read the student date and create students
		InputStream in = openInputStream(STUDENT_URL);
		Scanner iter = new Scanner(in);
		while( iter.hasNextLine() ) {
			String line = iter.nextLine();
			// Line should contain 4 values separated by comma
			String [] args = line.split(SEPARATOR);
			if (args.length != 4) {
				Logger.getLogger( this.getClass().getName() ).warning("invalid student data: "+line);
				continue;
			}
			students.add( new Student(args[0], args[1], args[2], args[3]) );
		}
		iter.close();
	}
	
	/**
	 * Get the single instance of this class.
	 * @return the Registrar 
	 */
	public static Registrar getInstance() {
		if (registrar == null) synchronized(Registrar.class) {
			if (registrar == null) registrar = new Registrar();
		}
		return registrar;
	}

	public List<Student> getStudents() {
		return students;
	}
	
	
	/**
	 * Create an InputStream for reading from a URL.
	 * @param urlstring the URL to read from
	 * @return an InputStream connected to the URL.
	 * @throws MalformedURLException if parameter is not a valid URL
	 * @throws IOException if any problem reading from the URL
	 */
	private static InputStream openInputStream(String urlstring) {
		// open URL for student data and create a buffered reader
		InputStream in = null;
		try {
			URL url = new URL(urlstring);
			in = url.openStream();
		} catch (MalformedURLException e) {
			System.err.println("Invalid URL: " + urlstring);
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		return in;
	}
	
	/**
	 * Test getting students from the registrar.
	 * @param args
	 */
	public static void main( String[] args) {
		Registrar regis = Registrar.getInstance();
		regis.getStudents().forEach( System.out::println );
		
	}
}
