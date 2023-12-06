import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.Scanner;

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
	 * Purpose:
	 * 	This method coordinates methods related to CRUD operations
	 *  -- update, delete, and add.
	 *  
	 *  Pre-condition:
	 *  	Connection to the dbms has been successfully established.
	 *  Post-condition:
	 *  	None
	 *  
	 * @param scanner input stream
	 * @param client a client connected to dbms.
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
	 * 	addition, deletion.
	 * 
	 * Pre-condition:
	 *  	Connection to the dbms has been successfully established.
	 *  
	 *  Post-condition:
	 *  	None
	 *  
	 *  
	 * @param scanner input stream
	 * @param client a client connected to dbms.
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
		System.out.println(result);
		// List course packages and allow member to select
		// Method needs to be implemented here
		// client.listAndSelectCoursePackage();
	}

	private static void deleteMember(Scanner scanner, DBClient client) {
		System.out.println("Deleting a member.");
		System.out.print("Enter member's number: ");
		int memberNumber = scanner.nextInt();
		String result = client.deleteMember(memberNumber);
		System.out.println(result);
	}

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
					System.out.println("Course: " + schedule[0] + " | Day: "
							+ schedule[3] + " | Time: " + schedule[4] + ":"
							+ schedule[5] + " | Duration: " + schedule[6] + " minutes");
				}
			}
			break;
		case 3:
			// Call method to handle query 3
			break;
		case 4:
			// Call method to handle custom query
			System.out.println("Enter the firsname");
    		String fname= scanner.nextLine();
    		System.out.println("Enter the lastname");
    		String lname= scanner.nextLine();
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
		List<List<Integer>> schedules = getSchedule();

		// Call DBClient method to add the course
		client.addCourse(courseName, maxParticipants, curParticipants, startDate, endDate, schedules);
	}

	private static void deleteCourse(Scanner scanner, DBClient client) {
		System.out.println("Deleting a course.");
		System.out.print("Enter course name: ");
		String courseName = scanner.nextLine();
		System.out.print("Enter course start date: ");
		Date startDate = Date.valueOf(scanner.nextLine());

		client.deleteCourse(courseName, startDate);
	}

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

	/*
  When adding a course package, the system lists all available courses that have not yet ended, 
  allowing the admin to select which to include. */
  private static void addCoursePackage(Scanner scanner, DBClient client) {
    System.out.println("Adding a new course package.");
    System.out.print("Enter the name of the course package to add: ");
    String packageName = scanner.nextLine();

    System.out.print("Enter the price of the course package: ");
    int packagePrice = scanner.nextInt();

    List<String[]> allCourses = client.listOngoingCourses();
    if (allCourses.isEmpty()) {
        System.out.println("There are no ongoing courses to add to the package.");
        return;
    }

    List<String[]> selectedCourses = new ArrayList<>();
    System.out.println("Select courses to add to the package (Enter the class name to select, 'done' to finish):");
    for (int i = 0; i < allCourses.size(); i++) {
        System.out.println((i + 1) + ". " + allCourses.get(i)[0]);
    }

    String input;
    while (!(input = scanner.nextLine()).equalsIgnoreCase("done")) {
        boolean courseFound = false;
        for (String[] course : allCourses) {
            if (course[0].equalsIgnoreCase(input)) {
                if (selectedCourses.contains(course)) {
                    System.out.println(input + " has already been added.");
                } else {
                    selectedCourses.add(course);
                    System.out.println(input + " added.");
                }
                courseFound = true;
                break;
            }
        }
        if (!courseFound) {
            System.out.println("Course not found. Please enter a valid course name.");
        }
        System.out.println("Enter next course or 'done':");
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

	private static void updateCoursePackage(Scanner scanner, DBClient client) {
		System.out.println("Updating a course package.");
		// First, list all available course packages to select from
		// Will be implemented
		// client.listCoursePackages();

		System.out.print("Enter the name of the course package to update: ");
		String packageName = scanner.nextLine();
		List<String[]> selectedCourses = new ArrayList<>();

		client.updateCoursePackage(packageName, selectedCourses);
	}

	private static void deleteCoursePackage(Scanner scanner, DBClient client) {
    System.out.println("Select a course package to delete:");
    List<String> packages = client.listAllPackages();
    for (int i = 0; i < packages.size(); i++) {
        System.out.println((i + 1) + ". " + packages.get(i));
    }

    int choice = scanner.nextInt();
    scanner.nextLine();  // Consume the newline left-over
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


	private static List<List<Integer>> getSchedule() {
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

	private static int isNumeric(String numberString) {
		try {
			return Integer.parseInt(numberString.trim());
		} catch (NumberFormatException ex) {
			return -1;
		}
	}
}