/*
 * Name:  Umidjon Muzrapov, Hamad Marhoon, Abdullah Alkhamis, Yahya Al Malallah
 * Assignment: Program 4
 * Instructor: Prof. Lester I. McCann
 * TAs: Zhenyu Qi and Danial Bazmandeh
 * Due Date: 12/05/2023
 * Version: 12/05/2023
 * 
 * -----------------------------------------
 * 
 * Description: This program is a database-deriven information management system 
 * for fitness center. Specifically, it enables the user to add or delete a member,
 * add or delete a course, and add,update, or delete a course package.
 * 
 * To compile and execute this program in lectura:
 * 
 * 1) Run 'source add_class_path' which is included to
 * the solution. It adds the Oracle JDBC driver to your CLASSPATH environment variable.
 * 2) Compile this java files:
 * 	    javac DBClient.java
 * 		javac Main java
 * 3) Run the program:
 * 		java Prog3.java <oracle username> <oracle password>
 * 
 * -----------------------------------------
 * Operational Requirements: Java 16.
 * 
 * -----------------------------------------
 * Known bugs or incomplete parts: None is known.
 */

import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.Scanner;

/**
 * Class name: Main Name: Umidjon Muzrapov, Hamad Marhoon, Abdullah Alkhamis,
 * Yahya Al Malallah Dependencies: java.sql.* java.util.* DBClient.java
 * 
 * Inherits from: None Interfaces: None ----------------------------------------
 * 
 * Purpose: This class aims to execute the program, interact with the user,
 * allow manage the gym, answer 4 queries.
 * ----------------------------------------
 * 
 * Constructor: The default constructor
 * 
 * Class methods: void main(String[] args): the beginning point of the program.
 * 
 * Instance methods: None
 */
public class Main {

	/**
	 * Method main
	 *
	 * Purpose: This is the entry point of the program. It coordinates the tasks to
	 * accomplish the purpose of this program.
	 *
	 * Pre-condition: The user either pass two command-line arguments (their
	 * username and password to Oracle) or does not pass anything.
	 *
	 * Relations are populated and data is well-formatted.
	 *
	 * Oracle JDBC driver is added to the CLASSPATH: source add_class_path
	 *
	 * Post-condition: None.
	 *
	 * @param args (Into this method) command-line arguments. Might be the username
	 *             and password to Oracle.
	 */
	public static void main(String[] args) {
		// Create a DBClient instance
		DBClient client = new DBClient(args);
		try (Scanner scanner = new Scanner(System.in)) {
			boolean running = true;
			while (running) {
				System.out.println("\n--- Main Menu ---");
				System.out.println("1. Record Operations");
				System.out.println("2. Query Database");
				System.out.println("3. Exit");
				System.out.print("Select an option: ");

				int choice = scanner.nextInt();
				switch (choice) {
				case 1:
					handleRecordOperations(scanner, client);
					break;
				case 2:
					handleQueries(scanner, client);
					break;
				case 3:
					running = false;
					System.out.println("Exiting program.");
					break;
				default:
					System.out.println("Invalid option. Please try again.");
				}
			}
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}

	/**
	 * Method handleRecordOperations
	 * 
	 * Purpose: This method coordinates methods related to CRUD operations --
	 * update, delete, and add.
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * Post-condition: None
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void handleRecordOperations(Scanner scanner, DBClient client) {
		System.out.println("\n--- Record Operations ---");
		System.out.println("1. Add/Delete Member");
		System.out.println("2. Add/Delete Course");
		System.out.println("3. Add/Update/Delete Course Package");
		System.out.print("Select an operation: ");

		int operation = scanner.nextInt();
		scanner.nextLine(); // Consume the remaining newline

		switch (operation) {
		case 1:
			handleMemberOperations(scanner, client);
			break;
		case 2:
			handleCourseOperations(scanner, client);
			break;
		case 3:
			handleCoursePackageOperations(scanner, client);
			break;
		default:
			System.out.println("Invalid operation. Please try again.");
		}
	}

	/**
	 * Method handleMemberOperations
	 * 
	 * Purpose: The method coordinate the methods that handle CRUD on member:
	 * addition, deletion.
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * 
	 * Post-condition: None
	 * 
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void handleMemberOperations(Scanner scanner, DBClient client) {
		System.out.println("\n--- Member Operations ---");
		System.out.println("1. Add Member");
		System.out.println("2. Delete Member");
		System.out.print("Select an option: ");

		int option = scanner.nextInt();
		scanner.nextLine(); // Consume newline

		switch (option) {
		case 1:
			addMember(scanner, client);
			break;
		case 2:
			deleteMember(scanner, client);
			break;
		default:
			System.out.println("Invalid option. Please try again.");
		}
	}

	/**
	 * Method addMember
	 * 
	 * Purpose: This method adds a member to a relation Member and adds package.
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * 
	 * Post-condition: A new row is added.
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void addMember(Scanner scanner, DBClient client) {
		System.out.println("Adding a new member.");
		System.out.print("Enter member's first name: ");
		String firstName = scanner.nextLine();

		System.out.print("Enter member's last name: ");
		String lastName = scanner.nextLine();

		System.out.print("Enter member's phone number: ");
		String phone = scanner.nextLine();

		// Call DBClient method to add member
		String result = client.addMember(firstName, lastName, phone);
		System.out.println("\n" + result);

		if (result.startsWith("Member added successfully")) {
			// Extract member number
			String memberNumberStr = result.substring(result.lastIndexOf(":") + 1).trim();
			int memberNumber = Integer.parseInt(memberNumberStr);
			System.out.println("\nSelect a package to purchase: ");
			List<String[]> packages = client.listAllPackagesAndCourses();
			int counter = 1;
			for (String[] pkg : packages) {
				System.out.println(counter + ". " + pkg[0] + " | Price: " + pkg[1] + "\nCourses: " + pkg[2] + "\n");
				counter++;
			}
			// Prompt the user to select a package
			System.out.print("\nPlease enter the number of the package you wish to purchase: ");
			int packageChoice = scanner.nextInt();
			String transaction = client.packagePurchased(packages.get(packageChoice - 1)[0],
					packages.get(packageChoice - 1)[1], memberNumber);
			System.out.println("\n" + transaction);
		}
	}

	/**
	 * Method deleteMember
	 * 
	 * Purpose: This member deletes a member from a relation Member.
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * 
	 * Post-condition: A tuple is removed.
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void deleteMember(Scanner scanner, DBClient client) {
		System.out.println("Deleting a member.");
		System.out.print("Enter member's number: ");
		// get member ID whom you want to remove
		int memberNumber = scanner.nextInt();
		// call a method to remove the user
		String result = client.deleteMember(memberNumber);
		System.out.println(result);
	}

	/**
	 * Method habdleQueries
	 * 
	 * Purpose: This method coordinates methods that handle queries.
	 * 
	 *
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * 
	 * Post-condition: None
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void handleQueries(Scanner scanner, DBClient client) {
		System.out.println("\n--- Queries ---");
		System.out.println("1. List members with negative balance");
		System.out.println("2. Check a member's class schedule for November");
		System.out.println("3. View trainers' working hours for December");
		System.out.println("4. Check a member's all courses and schedule");
		System.out.print("Select a query: ");

		int query = scanner.nextInt();
		scanner.nextLine(); // Consume the remaining newline

		switch (query) {
		case 1:
			List<String[]> queryOneResults = client.queryOne();
			if (queryOneResults.size() == 0) {
				System.out.println("\nThere are no current members with negative balance.");
			} else {
				System.out.println("\nMembers with negative balance:");
				for (String[] member : queryOneResults) {
					System.out.println("Name: " + member[0] + " | Phone: " + member[1] + " | Balance: " + member[2]);
				}
			}
			break;
		case 2:
			System.out.print("Enter member number: ");
			int memberNumber = scanner.nextInt();
			scanner.nextLine();
			List<String[]> queryTwoResult = client.queryTwo(memberNumber);
			if (queryTwoResult.size() == 0) {
				System.out.println("\nThe member has no classes in November.");
			} else {
				System.out.println("\nMember's November schedule:");
				for (String[] schedule : queryTwoResult) {
					System.out.println("Course: " + schedule[0] + " | Day: " + schedule[3] + " | Time: " + schedule[4]
							+ ":" + schedule[5] + " | Duration: " + schedule[6] + " minutes");
				}
			}
			break;
		case 3:
			List<String[]> queryThreeResults = client.queryThree();
			if (queryThreeResults.size() == 0) {
				System.out.println("\nNo trainers work during December.");
			} else {
				System.out.println("\nTrainers December schedule:");
				for (String[] trainer : queryThreeResults) {
					String formattedTime = String.format("%02d:%02d", Integer.parseInt(trainer[3]),
							Integer.parseInt(trainer[4]));
					System.out.println("Trainer number: " + trainer[0] + " | Trainer name: " + trainer[1]
							+ " | Start time on " + trainer[2] + ": " + formattedTime + " | Duration: " + trainer[5]
							+ " minutes | Total Hours in December: " + trainer[6]);
				}
			}
			break;
		case 4:
			// Call method to handle custom query
			System.out.println("Enter the firsname");
			String fname = scanner.nextLine();
			System.out.println("Enter the lastname");
			String lname = scanner.nextLine();
			try {
				client.printCourseSchedule(fname, lname);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			System.out.println("Invalid query. Please try again.");
		}
	}

	/**
	 * Method handleCourseOperations
	 * 
	 * Purpose: This method coordinate methods that modify course -- addition and
	 * deletion.
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * 
	 * Post-condition: None
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void handleCourseOperations(Scanner scanner, DBClient client) {
		System.out.println("\n--- Course Operations ---");
		System.out.println("1. Add Course");
		System.out.println("2. Delete Course");
		System.out.print("Select an option: ");

		int option = scanner.nextInt();
		scanner.nextLine(); // Consume newline

		switch (option) {
		case 1:
			addCourse(scanner, client);
			break;
		case 2:
			deleteCourse(scanner, client);
			break;
		default:
			System.out.println("Invalid option. Please try again.");
		}
	}

	/**
	 * Method addCourse
	 * 
	 * Purpose: This method a tuple course to Course relation. It also adds
	 * schedules for the course, and assigns the trainer.
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * 
	 * Post-condition: a new tuple and schedule tuples are inserted.
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void addCourse(Scanner scanner, DBClient client) {
		System.out.println("Adding a new course.");
		System.out.print("Enter course name: ");
		String courseName = scanner.nextLine();
		System.out.print("Enter maximum number of course participants: ");
		int maxParticipants = scanner.nextInt();
		scanner.nextLine();

		System.out.print("Enter current number of course participants: ");
		int curParticipants = scanner.nextInt();
		scanner.nextLine();

		System.out.print("Enter course start date (yyyy-mm-dd): ");
		Date startDate = Date.valueOf(scanner.nextLine());
		System.out.print("Enter course end date (yyyy-mm-dd): ");
		Date endDate = Date.valueOf(scanner.nextLine());
		List<List<Integer>> schedules = getCourseSchedule();

		// Call DBClient method to add the course
		client.addCourse(courseName, maxParticipants, curParticipants, startDate, endDate, schedules);
	}

	/**
	 * Method deleteCourse
	 * 
	 * Purpose: This methods deletes a course from Course relation.
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * 
	 * Post-condition: a tuple is removed.
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void deleteCourse(Scanner scanner, DBClient client) {
		System.out.println("Deleting a course.");
		System.out.print("Enter course name: ");
		String courseName = scanner.nextLine();
		System.out.print("Enter course start date: ");
		Date startDate = Date.valueOf(scanner.nextLine());

		client.deleteCourse(courseName, startDate);
	}

	/**
	 * Method handleCoursePackageOperations
	 * 
	 * 
	 * Purpose: This method handle operations that handle coursePackages -- adding a
	 * course package, update and deletion.
	 * 
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * 
	 * Post-condition: None
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void handleCoursePackageOperations(Scanner scanner, DBClient client) {
		System.out.println("\n--- Course Package Operations ---");
		System.out.println("1. Add Course Package");
		System.out.println("2. Update Course Package");
		System.out.println("3. Delete Course Package");
		System.out.print("Select an option: ");

		int option = scanner.nextInt();
		scanner.nextLine(); // Consume newline

		switch (option) {
		case 1:
			addCoursePackage(scanner, client);
			break;
		case 2:
			updateCoursePackage(scanner, client);
			break;
		case 3:
			deleteCoursePackage(scanner, client);
			break;
		default:
			System.out.println("Invalid option. Please try again.");
		}
	}

	/**
	 * Method addCoursePackage
	 * 
	 * Purpose: This method add a tuple that represents 'Every course is a part of
	 * zero or more relationships'. When adding a course package, the system lists
	 * all available courses that have not yet ended, allowing the admin to select
	 * which to include.
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * 
	 * Post-condition: a tuple is added.
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void addCoursePackage(Scanner scanner, DBClient client) {
		System.out.println("Adding a new course package.");
		System.out.print("Enter the name of the course package to add: ");
		String packageName = scanner.nextLine();

		System.out.print("Enter the price of the course package: ");
		int packagePrice = scanner.nextInt();
		scanner.nextLine(); // Consume the newline left-over

		List<String[]> allCourses = client.listOngoingCourses();
		if (allCourses.isEmpty()) {
			System.out.println("There are no ongoing courses to add to the package.");
			return;
		}

		List<String[]> selectedCourses = new ArrayList<>();
		System.out.println("Select courses to add to the package (Enter the number of the course, 'done' to finish):");
		for (int i = 0; i < allCourses.size(); i++) {
			String[] course = allCourses.get(i);
			System.out.println((i + 1) + ". " + course[0] + " - Start Date: " + course[1]);
		}

		String input;
		while (!(input = scanner.nextLine()).equalsIgnoreCase("done")) {
			try {
				int courseIndex = Integer.parseInt(input) - 1;
				if (courseIndex >= 0 && courseIndex < allCourses.size()) {
					String[] selectedCourse = allCourses.get(courseIndex);
					if (!selectedCourses.contains(selectedCourse)) {
						selectedCourses.add(selectedCourse);
						System.out.println(selectedCourse[0] + " added.");
					} else {
						System.out.println(selectedCourse[0] + " has already been added.");
					}
				} else {
					System.out.println("Invalid course number. Please enter a valid number.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a valid number.");
			}
			System.out.println("Enter next course number or 'done':");
		}

		if (selectedCourses.isEmpty()) {
			System.out.println("No courses were selected.");
		} else {
			if (!client.addPackage(packageName, packagePrice)) {
				System.out.println("Failed to add package: " + packageName);
				return;
			}
			boolean result = client.addCoursePackage(packageName, selectedCourses);
			if (result) {
				System.out.println("Course package added successfully.");
			} else {
				System.out.println("Failed to add course package.");
			}
		}
	}

	/**
	 * Method updateCoursePackage
	 * 
	 * Purpose: This method updates a tuple that represents 'Every course is a part
	 * of zero or more relationships'.
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * 
	 * Post-condition: a tuple is update.
	 * 
	 * @param scanner input stream
	 * @param client  a client connected to dbms.
	 */
	private static void updateCoursePackage(Scanner scanner, DBClient client) {
		System.out.println("Select a course package to update:");
		List<String> packages = client.listAllPackages();
		for (int i = 0; i < packages.size(); i++) {
			System.out.println((i + 1) + ". " + packages.get(i));
		}

		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume the newline left-over
		if (choice < 1 || choice > packages.size()) {
			System.out.println("Invalid choice. Operation cancelled.");
			return;
		}

		String packageName = packages.get(choice - 1);
		editSelectedPackage(scanner, client, packageName);
	}

	/**
	 * Method editSelectedPackage
	 * 
	 * Purpose: This method provides options to edit a selected course package. It
	 * allows the admin to add or remove courses from the specified package, thereby
	 * updating its composition. The method presents a menu for these operations and
	 * delegates the task to the respective methods based on the admin's choice.
	 *
	 * Pre-condition: Connection to the DBMS has been successfully established. The
	 * specified package name must correspond to an existing course package.
	 *
	 * Post-condition: The selected course package is modified based on the admin's
	 * choices. This could involve adding new courses to the package or removing
	 * existing ones. The state of the package in the database is updated
	 * accordingly.
	 *
	 * @param scanner     An instance of Scanner for capturing user input.
	 * @param client      An instance of DBClient, representing a client connected
	 *                    to the DBMS.
	 * @param packageName The name of the course package to be edited.
	 */
	private static void editSelectedPackage(Scanner scanner, DBClient client, String packageName) {
		System.out.println("Editing package: " + packageName);
		System.out.println("1. Add a course");
		System.out.println("2. Remove a course");

		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume the newline left-over

		switch (choice) {
		case 1:
			// Call method to add a course to this package
			addCourseToPackage(scanner, client, packageName);
			break;
		case 2:
			// Call method to remove a course from this package
			removeCourseFromPackage(scanner, client, packageName);
			break;
		default:
			System.out.println("Invalid option. Operation cancelled.");
			break;
		}
	}

	/**
	 * Method addCourseToPackage
	 * 
	 * Purpose: This method allows the admin to add a new course to an existing
	 * course package. It presents a list of all ongoing courses and prompts the
	 * admin to select one to add to the specified package. The method includes a
	 * check to prevent duplication by verifying if the selected course is already
	 * part of the package.
	 * 
	 * Pre-condition: Connection to the DBMS must be successfully established. The
	 * method assumes that the package name provided corresponds to an existing
	 * course package. Also, there should be ongoing courses available for
	 * selection.
	 * 
	 * Post-condition: If the selected course is not already in the package, it is
	 * added, and its details are updated in the database. If the course is already
	 * in the package, the method informs the user and no addition is made.
	 * 
	 * @param scanner     An instance of Scanner to capture user input.
	 * @param client      An instance of DBClient, representing a client connected
	 *                    to the DBMS.
	 * @param packageName The name of the course package to which the course is
	 *                    being added.
	 */
	private static void addCourseToPackage(Scanner scanner, DBClient client, String packageName) {
		System.out.println("Select a course to add to the package '" + packageName + "':");
		List<String[]> allCourses = client.listOngoingCourses();
		for (int i = 0; i < allCourses.size(); i++) {
			String[] course = allCourses.get(i);
			System.out.println((i + 1) + ". " + course[0] + " (Start Date: " + course[1] + ")");
		}

		System.out.print("Enter the number of the course to add: ");
		int courseIndex = scanner.nextInt() - 1;
		scanner.nextLine(); // Consume the newline left-over

		if (courseIndex >= 0 && courseIndex < allCourses.size()) {
			String[] selectedCourse = allCourses.get(courseIndex);

			// Check if the course is already in the package
			if (client.isCourseInPackage(packageName, selectedCourse[0], selectedCourse[1])) {
				System.out.println("This course is already in the package.");
				return;
			}

			// Proceed to add course
			if (client.addCourseToPackage(packageName, selectedCourse[0], selectedCourse[1])) {
				System.out.println("Course added successfully to the package.");
			} else {
				System.out.println("Failed to add course to the package.");
			}
		} else {
			System.out.println("Invalid course selection.");
		}
	}

	/**
	 * Method removeCourseFromPackage
	 * 
	 * Purpose: This method facilitates the removal of a specific course from an
	 * existing course package. The admin is presented with a list of all courses
	 * currently in the selected package and can choose one to be removed. This
	 * method is responsible for handling the user's selection and initiating the
	 * removal process of the selected course from the package.
	 * 
	 * Pre-condition: Connection to the DBMS must be active and stable. The
	 * packageName provided should correspond to an existing course package in the
	 * database. The method assumes that the package contains at least one course
	 * that can be removed.
	 * 
	 * Post-condition: If the user's selection is valid, the specified course is
	 * removed from the course package. The database is updated to reflect this
	 * change. If the course selection is invalid or the removal process encounters
	 * an issue, an appropriate message is displayed.
	 * 
	 * @param scanner     An instance of Scanner to capture user input.
	 * @param client      An instance of DBClient, representing a client connected
	 *                    to the DBMS.
	 * @param packageName The name of the course package from which a course is
	 *                    being removed.
	 */
	private static void removeCourseFromPackage(Scanner scanner, DBClient client, String packageName) {
		System.out.println("Select a course to remove from the package '" + packageName + "':");
		List<String[]> packageCourses = client.listCoursesInPackage(packageName);
		for (int i = 0; i < packageCourses.size(); i++) {
			String[] course = packageCourses.get(i);
			System.out.println((i + 1) + ". " + course[0] + " (Start Date: " + course[1] + ")");
		}

		System.out.print("Enter the number of the course to remove: ");
		int courseIndex = scanner.nextInt() - 1;
		scanner.nextLine(); // Consume the newline left-over

		if (courseIndex >= 0 && courseIndex < packageCourses.size()) {
			String[] selectedCourse = packageCourses.get(courseIndex);
			if (client.removeCourseFromPackage(packageName, selectedCourse[0], selectedCourse[1])) {
				System.out.println("Course removed successfully from the package.");
			} else {
				System.out.println("Failed to remove course from the package.");
			}
		} else {
			System.out.println("Invalid course selection.");
		}
	}

	/**
	 * Method deleteCoursePackage
	 * 
	 * Purpose: This method allows an admin to delete an entire course package from
	 * the system. It first displays a list of all existing course packages and then
	 * prompts the admin to select one for deletion. Upon selection, the method
	 * proceeds to delete the chosen package along with any associated course
	 * entries in the package from the database.
	 * 
	 * Pre-condition: Connection to the DBMS must be established and functional. The
	 * list of packages presented to the admin is assumed to be current and
	 * accurate. The admin is expected to make a valid selection from the list.
	 * 
	 * Post-condition: If a valid package is selected, it is removed from the
	 * database, including all associations with courses. If the selection is
	 * invalid or the deletion process encounters an error, an appropriate error
	 * message is displayed, and no changes are made to the database.
	 * 
	 * @param scanner An instance of Scanner to capture user input.
	 * @param client  An instance of DBClient, representing a client connected to
	 *                the DBMS.
	 */
	private static void deleteCoursePackage(Scanner scanner, DBClient client) {
		System.out.println("Select a course package to delete:");
		List<String> packages = client.listAllPackages();
		for (int i = 0; i < packages.size(); i++) {
			System.out.println((i + 1) + ". " + packages.get(i));
		}

		int choice = scanner.nextInt();
		scanner.nextLine(); // Consume the newline left-over
		if (choice < 1 || choice > packages.size()) {
			System.out.println("Invalid choice. Operation cancelled.");
			return;
		}

		String packageName = packages.get(choice - 1);
		if (client.deleteCoursePackage(packageName)) {
			System.out.println("Package '" + packageName + "' deleted successfully.");
		} else {
			System.out.println("Failed to delete package.");
		}
	}

	/**
	 * Method getCoruseSchedule
	 * 
	 * Purpose: This methods gets a set of schedule(s) for the course. Expected
	 * format Day of the week, hour, minute, duration.
	 * 
	 * Pre-condition: None Post-condition: At least one schedule is given.
	 * 
	 * @return a list of schedule which is represented by a list of integers.
	 */
	private static List<List<Integer>> getCourseSchedule() {
		System.out.println("Enter the schedule for course or done:");
		System.out.println("The format is 'Day, hour, minute, duration'.\ne.g.Monday, 15, 30, 50.");
		List<List<Integer>> schedules = new ArrayList<List<Integer>>();
		Scanner keyboard = new Scanner(System.in);
		String line = keyboard.nextLine();

		while (!line.trim().equalsIgnoreCase("done")) {
			// formatted line
			List<Integer> schedule = parseSchedule(line);
			if (schedule == null) {
				System.out.println("The wrong format.");
			} else {
				System.out.println("Schedule is noted.");
				schedules.add(schedule);
			}

			System.out.println("Enter the schedule for course or done:");
			System.out.println("The format is 'Day, hour, minute, duration'.\ne.g.Monday, 15, 30, 50.");
			line = keyboard.nextLine();
		}

		return schedules;
	}

	/**
	 * Method parseSchedule
	 * 
	 * Purpose: This method appropriately parse/formats the user input for schedule,
	 * so it can be used for schedule insertion.
	 * 
	 * Pre-condition: Schedule elements are separated by a comma
	 * 
	 * Post-condition: None
	 * 
	 * @param line string that represent a schedule for the course
	 * @return a list of integers that stand for day of the week, hour, minute,
	 *         duration
	 */
	private static List<Integer> parseSchedule(String line) {
		List<Integer> schedule = new ArrayList<Integer>();
		String[] words = line.split(",");
		Map<String, Integer> dayOfWeekMap = new HashMap<String, Integer>();

		// Adding entries to the map
		dayOfWeekMap.put("Monday", 1);
		dayOfWeekMap.put("Tuesday", 2);
		dayOfWeekMap.put("Wednesday", 3);
		dayOfWeekMap.put("Thursday", 4);
		dayOfWeekMap.put("Friday", 5);
		dayOfWeekMap.put("Saturday", 6);
		dayOfWeekMap.put("Sunday", 7);

		if (words.length != 4) {
			System.out.println("The wrong number of arguments.");
			return null;
		}

		if (!dayOfWeekMap.containsKey(words[0])) {
			System.out.println("The day of the week does not exist.");
			return null;
		}

		schedule.add(dayOfWeekMap.get(words[0]));
		for (int i = 1; i < 4; i++) {
			int numericVal = isNumeric(words[i]);

			if (numericVal >= 0) {
				schedule.add(numericVal);
			} else {
				System.out.println("String given when integer was expected." + words[i]);
				return null;
			}
		}

		return schedule;

	}

	/**
	 * Method isNumeric
	 * 
	 * Purpose: The method checks if the number is numeric and returns its int value
	 * if so. Otherwise, it returns -1.
	 * 
	 * Pre-condition: Input is positive.
	 * 
	 * Post-condition: None
	 * 
	 * @param numberString a string whose int value is sought.
	 * @return int value of numberString. -1 if the input is not a number.
	 */
	private static int isNumeric(String numberString) {
		try {
			return Integer.parseInt(numberString.trim());
		} catch (NumberFormatException ex) {
			return -1;
		}
	}
}