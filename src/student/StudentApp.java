package student;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Display reminders of students having a birthday soon.
 * 
 * @author Chayanin Punjakunaporn
 */
public class StudentApp {

	/**
	 * Print the names (and birthdays) of students having a birthday in the specified month.
	 * 
	 * @param students list of students
	 * @param filter that test that who of student has birthday in the month of today
	 */
	public void filterAndPrint(List<Student> students, Predicate<Student> filter, Consumer<Student> action, Comparator<Student> byBirthday) {
		students.stream().filter(filter).sorted(byBirthday).forEach(action);
	}

	/**
	 * Run this class. Calling filterAndPrint is example, so predicate,
	 * consumer, and comparator that be used all are ready.
	 */
	public static void main(String[] args) {
		List<Student> students = Registrar.getInstance().getStudents();
		StudentApp app = new StudentApp();
		Predicate<Student> checkBirthMonth = (s) -> (s.getBirthdate().getMonthValue() == LocalDate.now().getMonthValue());
		Predicate<Student> checkBirthdayIn2Week = (s) -> (s.getBirthdate().getDayOfYear() - LocalDate.now().getDayOfYear() <= 14
															&& s.getBirthdate().getDayOfYear() - LocalDate.now().getDayOfYear() >= 0);
		Consumer<Student> studentConsumer = (s) -> System.out.println(s.toString() + " will have birthday on "
													+ s.getBirthdate().getDayOfMonth() + " "
													+ s.getBirthdate().getMonth().toString());
		Comparator<Student> byName = (a, b) -> a.toString().compareTo(b.toString());
		Comparator<Student> byBirthday = (a, b) -> (a.getBirthdate().getDayOfYear() - b.getBirthdate().getDayOfYear());

		app.filterAndPrint(students, checkBirthMonth, studentConsumer, byBirthday);
	}
}
