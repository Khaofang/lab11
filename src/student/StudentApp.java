package student;
import java.util.List;

/**
 * Display reminders of students having a birthday soon.
 * @author you
 */
public class StudentApp {

	/**
	 * Print the names (and birthdays) of students having a birtday in the
	 * specified month.
	 * @param students list of students
	 * @param month the month to use in selecting bithdays
	 */
	public void filterAndPrint( List<Student> students, int month ) {
		for(Student s : students ) {
			if (s.getBirthdate().getMonthValue() == month)
	                  System.out.println( s );
		}
	}
	
	public static void main(String[] args) {
		List<Student> students = Registrar.getInstance().getStudents();
		StudentApp app = new StudentApp();
		app.filterAndPrint(students, 5 /* may */);
	}
}
