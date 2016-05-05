package student;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
	private void setBirthdate(String datestr) {
		try {
			birthdate = LocalDate.parse(datestr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch(DateTimeParseException e) {
			System.out.println("Can't parse String of birthdate to LocalDate. It format is invalid from dd/MM/yyyy!");
		}
	}
	
	@Override
	public String toString() {
		return firstname+" "+lastname;
	}
}
