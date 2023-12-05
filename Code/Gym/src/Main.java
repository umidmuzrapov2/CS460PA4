import java.sql.Date;
import java.util.*;
import java.util.Scanner;

public class Main {

  /**
   * Method main
   *
   * Purpose:
   * 	This is the entry point of the program.
   * 	It coordinates the tasks to accomplish the purpose of this program.
   *
   * Pre-condition:
   * 	The user either pass two command-line arguments (their username
   * 	and password to Oracle) or does not pass anything.
   *
   * 	Relations are populated and data is well-formatted.
   *
   * 	Oracle JDBC driver is added to the CLASSPATH:
   * 		source add_class_path
   *
   * Post-condition:
   * 	None.
   *
   * @param args (Into this method) command-line arguments. Might be the username and password to Oracle.
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

    System.out.print("Enter member's level ID: ");
    int levelID = scanner.nextInt();

    // Call DBClient method to add member
    client.addMember(firstName, lastName, phone, levelID);
    // List course packages and allow member to select
    // Method needs to be implemented here
    //client.listAndSelectCoursePackage();
  }

  private static void deleteMember(Scanner scanner, DBClient client) {
    System.out.println("Deleting a member.");
    System.out.print("Enter member's number: ");
    int memberNumber = scanner.nextInt();
    client.deleteMember(memberNumber);
    System.out.println("Member " + memberNumber + " deleted!");
  }

  private static void handleQueries(Scanner scanner, DBClient client) {
    System.out.println("\n--- Queries ---");
    System.out.println("1. List members with negative balance");
    System.out.println("2. Check a member's class schedule for November");
    System.out.println("3. View trainers' working hours for December");
    System.out.println("4. Custom Query");
    System.out.print("Select a query: ");

    int query = scanner.nextInt();
    scanner.nextLine(); // Consume the remaining newline

    switch (query) {
      case 1:
        // Call method to handle query 1
        break;
      case 2:
        // Call method to handle query 2
        break;
      case 3:
        // Call method to handle query 3
        break;
      case 4:
        // Call method to handle custom query
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

    System.out.print("Enter current number of course participants: ");
    int curParticipants = scanner.nextInt();

    System.out.print("Enter course start date: ");
    Date startDate = Date.valueOf(scanner.nextLine());

    System.out.print("Enter course end date: ");
    Date endDate = Date.valueOf(scanner.nextLine());

    System.out.print("Enter trainer number: ");
    int trainerNumber = scanner.nextInt();

    // Call DBClient method to add the course
    client.addCourse(
      courseName,
      maxParticipants,
      curParticipants,
      startDate,
      endDate,
      trainerNumber
    );
  }

  private static void deleteCourse(Scanner scanner, DBClient client) {
    System.out.println("Deleting a course.");
    System.out.print("Enter course name: ");
    String courseName = scanner.nextLine();
    System.out.print("Enter course start date: ");
    Date startDate = Date.valueOf(scanner.nextLine());

    client.deleteCourse(courseName, startDate);
  }

  private static void handleCoursePackageOperations(
    Scanner scanner,
    DBClient client
  ) {
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

  private static void addCoursePackage(Scanner scanner, DBClient client) {
    System.out.println("Adding a new course package.");
    System.out.print("Enter the name of the course package to add: ");
    String packageName = scanner.nextLine();
    List<String[]> selectedCourses = new ArrayList<>();

    client.addCoursePackage(packageName, selectedCourses);
    // Additional logic to input course package details
    // Method to be implemented here to list course packages

    // Call DBClient method to add the course package
    // client.addCoursePackage(packageDetails);
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
    System.out.println("Deleting a course package.");
    // First, list all available course packages to select from
    // Will be implemented
    // client.listCoursePackages();

    System.out.print("Enter the name of the course package to delete: ");
    String packageName = scanner.nextLine();
    List<String[]> selectedCourses;

    client.deleteCoursePackage(packageName);
  }
}
