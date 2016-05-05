package student;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * A student with a name, student id, and birthday.
 * 
 * @author jim
 */
public class Student {
	private String id;
	private String firstname, lastname;
	private LocalDate birthdate;
	
	/**
	 * Initialize attributes of a new student
	 * @param id
	 * @param firstname
	 * @param lastname
	 * @param birthdate
	 */
	public Student(String id, String firstname, String lastname,
			String birthday) {
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		// date should be in the format "dd/MM/yyyy"
		setBirthdate(birthday);
	}

	/**
	 * Get the student's first name.
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * Get the student's last name.
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * Get the student's birth date.
	 * @return the birthdate as a LocalDate instance.
	 */
	public LocalDate getBirthdate() {
		return birthdate;
	}

	/**
	 * Get the student's ID.
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * A hacky method for setting the birthdate using String.
	 * @param datestr is a String containing birthday in the format "dd/mm/yyyy".
	 * @throws IllegalArgumentException if datestr is not in the correct format.
	 */
	//TODO Improve this code. Let LocalDate and DateTimeFormatter parse
	// the string for you.  Eliminate the use of Scanner!
	private void setBirthdate(String datestr) {
		Scanner scanner = new Scanner(datestr);
		scanner.useDelimiter("/");
		if (!scanner.hasNextInt())
			throw new IllegalArgumentException("date string must have format dd/mm/yyyy");
		int day = scanner.nextInt();
		if (!scanner.hasNextInt())
			throw new IllegalArgumentException("date string must have format dd/mm/yyyy");
		int month = scanner.nextInt();
		if (!scanner.hasNextInt())
			throw new IllegalArgumentException("date string must have format dd/mm/yyyy");
		int year = scanner.nextInt();
		if (scanner.hasNext())
			throw new IllegalArgumentException("date string must have format dd/mm/yyyy");
		birthdate = LocalDate.of(year, month, day);
	}
	
	@Override
	public String toString() {
		return firstname+" "+lastname;
	}
}
