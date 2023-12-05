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
    String levelID = scanner.nextLine();
    // Call DBClient method to add member
    //client.addMember(firstName, lastName, phoneNumber, levelID);

    // List course packages and allow member to select
    // client.listAndSelectCoursePackage();
  }

  private static void deleteMember(Scanner scanner, DBClient client) {
    System.out.println("Deleting a member.");
    System.out.print("Enter member's number: ");
    String memberId = scanner.nextLine();
    // Call DBClient method to check for unreturned equipment
    //boolean hasUnreturnedEquipment = client.hasUnreturnedEquipment(memberId);
    //if (hasUnreturnedEquipment) {
    /* mark equipment as lost and update available quantity */
    //}

    // Check for unpaid balances
    //boolean hasUnpaidBalances = client.hasUnpaidBalances(memberId);
    //if (hasUnpaidBalances) {
    /* print balances and prevent deletion */
    //}

    // Check if actively participating in any courses
    //boolean isParticipatingInCourses = client.isParticipatingInCourses(memberId);
    //if (isParticipatingInCourses) {
    /* delete course participation records and update available spots */
    //}
    //If all checks pass, proceed with deletion
    // client.deleteMember(memberId);
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
    System.out.print("Enter course description: ");
    String courseDescription = scanner.nextLine();
    // Additional course details here, such as duration, instructor, etc.

    // Call DBClient method to add the course
    // client.addCourse(courseName, courseDescription, ...);
  }

  private static void deleteCourse(Scanner scanner, DBClient client) {
    System.out.println("Deleting a course.");
    System.out.print("Enter course ID: ");
    String courseId = scanner.nextLine();
    // Check if the course is ongoing and has enrolled members
    // boolean isOngoing = client.checkIfCourseIsOngoing(courseId);
    // if (isOngoing) {
    //     List<Member> enrolledMembers = client.getEnrolledMembers(courseId);
    //     for (Member member : enrolledMembers) {
    //         System.out.println("Member Name: " + member.getName() + ", Phone: " + member.getPhone());
    //     }
    //     // Additional logic to handle notifying members
    // }

    // Proceed with course deletion
    // client.deleteCourse(courseId);
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
    // Additional logic to input course package details
    // e.g., name, description, list of courses

    // Call DBClient method to add the course package
    // client.addCoursePackage(packageDetails);
  }

  private static void updateCoursePackage(Scanner scanner, DBClient client) {
    System.out.println("Updating a course package.");
    // First, list all available course packages to select from
    // client.listCoursePackages();

    System.out.print("Enter the ID of the course package to update: ");
    String packageId = scanner.nextLine();
    // Additional logic to update course package details
    // e.g., add/remove courses from the package, update pricing

    // Call DBClient method to update the course package
    // client.updateCoursePackage(packageId, updatedDetails);
  }

  private static void deleteCoursePackage(Scanner scanner, DBClient client) {
    System.out.println("Deleting a course package.");
    // First, list all available course packages to select from
    // client.listCoursePackages();

    System.out.print("Enter the ID of the course package to delete: ");
    String packageId = scanner.nextLine();
    // Additional checks to ensure deletion does not impact enrolled members
    // e.g., check if any members are currently enrolled in this package
    // client.checkEnrollmentsBeforePackageDeletion(packageId);

    // Call DBClient method to delete the course package
    // client.deleteCoursePackage(packageId);
  }
}
