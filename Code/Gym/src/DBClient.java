import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.DayOfWeek;

/**
 * Class name: DBClient
 * 
 * Name: Umidjon Muzrapov, Abdullah Alkhamis, Hamad Marhoon, Yahya Al Malallah
 * 
 * Dependencies: java.sql.* java.util.*
 * 
 * Inherits from: None Interfaces: None ----------------------------------------
 * 
 * Purpose: This class aims to provide easy-to-use interface to communicate with
 * the dbms.
 * 
 * ----------------------------------------
 * 
 * Constructor: DBClient(String[] args) String[] -- username and password for
 * db.
 * 
 * Class methods: void main(String[] args): the beginning point of the program.
 * 
 * Instance methods: addCourse​(java.lang.String className, int maxParticipant,
 * int currentParticipant, java.sql.Date startDate, java.sql.Date endDate,
 * java.util.List<java.util.List<java.lang.Integer>> schedules)
 * addCoursePackage​(java.lang.String packageName,
 * java.util.List<java.lang.String[]> selectedCourses)
 * addMember​(java.lang.String firstName, java.lang.String lastName,
 * java.lang.String phoneNumber) addPackage​(java.lang.String packageName, int
 * packagePrice) deleteCourse​(java.lang.String className, java.sql.Date
 * startDate) deleteCoursePackage​(java.lang.String packageName)
 * deleteMember​(int memberNumber) deleteMember​(int memberNumber)
 * listCoursesInPackage​(java.lang.String packageName) listOngoingCourses()
 * memberExists​(java.lang.String firstname, java.lang.String lastname)
 * printCourseSchedule​(java.lang.String firstname, java.lang.String lastname)
 * queryOne() queryThree() queryTwo​(int memberNumber)
 * removeCourseFromPackage​(java.lang.String packageName, java.lang.String
 * className, java.lang.String startDate)
 * removeCourseFromPackage​(java.lang.String packageName, java.lang.String
 * className, java.lang.String startDate) updateCoursePackage​(java.lang.String
 * packageName, java.util.List<java.lang.String[]> updatedCourses)
 */
public class DBClient {

	// the field hold connection to db
	private Connection dbconn;

	public DBClient(String[] args) {
		this.dbconn = establishConnectionToDB(args);
	}

	/**
	 * Method estbalishConnectionToDB
	 *
	 * Purpose: This method establishes the connection to the DBMS.
	 *
	 * Pre-condition: Optinally, username and password to Oracle db are given
	 * correctly.
	 *
	 * Post-connection: Connection to the dbms is established or exit the program.
	 *
	 * @return Connection dbconn that represent the connection to DBMS.
	 */
	private static Connection establishConnectionToDB(String args[]) {
		// aloe access spell
		final String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
		// default user
		String username = "your username";
		// default password
		String password = "your password";

		// if the user gave their username and password
		if (args.length == 2) {
			username = args[0].trim();
			password = args[1].trim();
		}

		try {
			// load class/driver
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception. Error loading Oracle JDBC driver");
			System.exit(-1);
		}

		Connection dbconn = null;

		try {
			// establish connection
			dbconn = DriverManager.getConnection(oracleURL, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		return dbconn;
	}

	/**
	 * Method addMember
	 * 
	 * Purpose: This method facilitate addition of a tuple into Member relation.
	 * 
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 * Post-condition: addition of a tuple
	 * 
	 *
	 * @param firstName   first name of the member
	 * @param lastName    last name of the member
	 * @param phoneNumber phone number of the member
	 * @param levelId     membership level of a user
	 * 
	 * @return Status of sql execution
	 */
	public String addMember(String firstName, String lastName, String phoneNumber) {
		try {
			dbconn.setAutoCommit(false); // Start transaction

			// Find the next member number
			String maxMemberNumberQuery = "select MAX(memberNumber) from umidmuzrapov.member";
			Statement maxMemberNumberStmt = dbconn.createStatement();
			ResultSet rs = maxMemberNumberStmt.executeQuery(maxMemberNumberQuery);
			int nextMemberNumber = 1;
			if (rs.next()) {
				nextMemberNumber = rs.getInt(1) + 1;
			}

			// Sanitize the inputs
			firstName = firstName.replaceAll("'", "''");
			lastName = lastName.replaceAll("'", "''");
			phoneNumber = phoneNumber.replaceAll("'", "''");

			// Validate the inputs (basic example, expand as necessary)
			if (firstName.length() > 20 || lastName.length() > 20 || phoneNumber.length() != 10) {
				return "Invalid input lengths.";
			}

			// Create a SQL statement to insert a new member into the database
			String insertQuery = String.format(
					"insert into umidmuzrapov.member (memberNumber, fname, lname, phoneNumber, levelId) values (%d, '%s', '%s', '%s', %d)",
					nextMemberNumber, firstName, lastName, phoneNumber, 1);

			Statement statement = dbconn.createStatement();

			// Execute the SQL statement to add the member
			int rowsAffected = statement.executeUpdate(insertQuery);

			dbconn.commit(); // Commit transaction if all operations are successful
			// Check if the member was successfully added
			if (rowsAffected > 0) {
				return "Member added successfully | Member number: " + nextMemberNumber;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				dbconn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
				return "Error during rollback.";
			}
		}

		return "Member could not be added.";
	}

	/**
	 * Method memberNumber
	 * 
	 * Purpose: This method facilitates the deletion of a member from member
	 * relation.
	 * 
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 *
	 * Post-condition: deletion of a tuple
	 * 
	 *
	 * @param memberNumber id of the member who must be deleted
	 * @return string that represent the status of sql execution
	 */
	public String deleteMember(int memberNumber) {
		try {
			dbconn.setAutoCommit(false); // Start transaction

			// Check for unreturned equipment
			if (hasUnreturnedEquipment(memberNumber)) {
				// Retrieve unreturned equipment items
				String unreturnedQuery = String.format(
						"select itemNumber, quantity from umidmuzrapov.equipmentloan where memberItem = %d and returnedDate is NULL",
						memberNumber);
				Statement unreturnedStmt = dbconn.createStatement();
				ResultSet unreturnedItems = unreturnedStmt.executeQuery(unreturnedQuery);

				System.out.println("\nThe following loaned equipment will be marked as lost:");
				while (unreturnedItems.next()) {
					int itemNumber = unreturnedItems.getInt("itemNumber");
					int loanedQuantity = unreturnedItems.getInt("quantity");
					System.out.println("Item number: " + itemNumber + " | Quantity: " + loanedQuantity);

					String updateEquipQuery = String.format(
							"update umidmuzrapov.equipment set quantity = quantity - %d where itemNumber = %d",
							loanedQuantity, itemNumber);
					dbconn.createStatement().executeUpdate(updateEquipQuery);
				}
			}

			// Delete records from the Enrollment table
			String deleteEnrollmentQuery = String.format("delete from umidmuzrapov.enrollment where memberNumber = %d",
					memberNumber);
			dbconn.createStatement().executeUpdate(deleteEnrollmentQuery);

			// Delete records from the EquipmentLoan table
			String deleteEquipmentLoanQuery = String
					.format("delete from umidmuzrapov.equipmentloan where memberItem = %d", memberNumber);
			dbconn.createStatement().executeUpdate(deleteEquipmentLoanQuery);

			// Delete related records from Purchase and Transaction tables
			String findTransactionsQuery = String.format(
					"select transactionNumber from umidmuzrapov.transaction where memberNumber = %d", memberNumber);
			ResultSet transactions = dbconn.createStatement().executeQuery(findTransactionsQuery);
			while (transactions.next()) {
				int transactionNumber = transactions.getInt("transactionNumber");

				// Delete linked records from the Purchase table
				String deletePurchaseQuery = String
						.format("delete from umidmuzrapov.purchase where transactionNumber = %d", transactionNumber);
				dbconn.createStatement().executeUpdate(deletePurchaseQuery);
			}

			// Delete the transactions
			String deleteTransactionQuery = String
					.format("delete from umidmuzrapov.transaction where memberNumber = %d", memberNumber);
			dbconn.createStatement().executeUpdate(deleteTransactionQuery);

			// Finally, delete the member
			String deleteMemberQuery = String.format("delete from umidmuzrapov.member where memberNumber = %d",
					memberNumber);
			int rowsAffected = dbconn.createStatement().executeUpdate(deleteMemberQuery);

			dbconn.commit(); // Commit transaction if all operations are successful
			if (rowsAffected > 0) {
				return "Member deleted successfully!"; // Member deleted successfully
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				dbconn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return "Member cannot be deleted."; // Member deletion failed
	}

	/**
	 * 
	 * Method hasUnreturnedEquipment
	 * 
	 * Purpose: Implement logic to check if the member has unreturned equipment
	 * 
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 *
	 * Post-condition: None
	 * 
	 *
	 * @param memberNumber id of the member
	 * @return Return true if unreturned equipment is found; otherwise, return false
	 */
	private boolean hasUnreturnedEquipment(int memberNumber) {
		try {
			String query = String.format(
					"select count(*) from umidmuzrapov.equipmentloan where memberItem = %d and returnedDate is NULL",
					memberNumber);
			Statement statement = dbconn.createStatement();

			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

//	/**
//	 * Method hasUnpaidBalances
//	 * 
//	 * Purpose: Implement logic to check if the member has unpaid balances
//	 * 
//	 * Pre-condition: Connection to the dbms has been successfully established.
//	 *
//	 * Post-condition: None
//	 * 
//	 * 
//	 * @param memberNumber memberNumber id of the member
//	 * @return Return true if unpaid balances are found; otherwise, return false
//	 */
//	private boolean hasUnpaidBalances(int memberNumber) {
//		try {
//			String query = String.format(
//					"select count(*) from umidmuzrapov.transaction where memberNumber = %d and (type = 'Unpaid' or type is NULL)",
//					memberNumber);
//			Statement statement = dbconn.createStatement();
//
//			ResultSet resultSet = statement.executeQuery(query);
//			if (resultSet.next()) {
//				return resultSet.getInt(1) > 0;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}

	/**
	 * Method isParticipatingInCourses
	 * 
	 * Purpose: Implement logic to check if the member is actively participating in
	 * any courses. If so, delete course participation records and update course
	 * spots
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 *
	 * Post-condition: None
	 * 
	 * @param memberNumber
	 * @return Return true if participating in courses; otherwise, return false
	 */
	private boolean isParticipatingInCourses(int memberNumber) {
		try {
			String query = String.format("select count(*) from umidmuzrapov.enrollment where memberNumber = %d",
					memberNumber);
			Statement statement = dbconn.createStatement();

			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Method addCourse
	 * 
	 * Purpose: This method adds a tuple to Course relation. It checks that if any
	 * of the trainers is available. Then, it inserts schedules for the course.
	 * 
	 * Pre-condition: Connection to the dbms has been successfully established.
	 *
	 * Post-condition: None
	 * 
	 * 
	 * @param className          name of the course
	 * @param maxParticipant     max number of course participants
	 * @param currentParticipant current number of participants
	 * @param startDate          start date
	 * @param endDate            end date
	 * @param trainerNumber      trainer number
	 * @return true if successfull. false otjherwise.
	 */
	public boolean addCourse(String className, int maxParticipant, int currentParticipant, Date startDate, Date endDate,
			List<List<Integer>> schedules) {
		try {
			dbconn.setAutoCommit(false);
			int trainerNumber = getTrainerNumber(schedules);
			if (trainerNumber < 0) {
				System.out.println("No instructor is available for the schedule.");
				return false;
			} else {
				System.out.printf("Trainer with id: %d was chosen for this course.\n", trainerNumber);

			}

			String insertQuery = "insert into umidmuzrapov.course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber) values (?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = dbconn.prepareStatement(insertQuery);

			preparedStatement.setString(1, className);
			preparedStatement.setInt(2, maxParticipant);
			preparedStatement.setInt(3, currentParticipant);
			preparedStatement.setDate(4, new java.sql.Date(startDate.getTime()));
			preparedStatement.setDate(5, new java.sql.Date(endDate.getTime()));
			preparedStatement.setInt(6, trainerNumber);

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				for (List<Integer> schedule : schedules) {
					String scheduleInsert = "INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration) values (?, ?, ?, ?, ?, ?)";
					preparedStatement = dbconn.prepareStatement(scheduleInsert);
					preparedStatement.setString(1, className);
					preparedStatement.setDate(2, new java.sql.Date(startDate.getTime()));
					preparedStatement.setInt(3, schedule.get(0));
					preparedStatement.setInt(4, schedule.get(1));
					preparedStatement.setInt(5, schedule.get(2));
					preparedStatement.setInt(6, schedule.get(3));
					preparedStatement.executeUpdate();
				}

				System.out.println("The course is added.");
			}

			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				dbconn.rollback();
			} catch (SQLException ex) {
				System.out.println(e.getMessage());
			}
			return false;
		} finally {
			try {
				dbconn.commit();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Method getTrainerNumber
	 * 
	 * Purpose: Gets an available trainer number for the given course schedules
	 * 
	 * Pre-condition: Valid list of course schedules passed in
	 * 
	 * Post-condition: Trainer number returned if available, -1 if no trainer
	 * available
	 * 
	 * Logic: - Tries to find a trainer with no assigned courses - If none found,
	 * finds a trainer not busy during the given schedules - If still none found,
	 * returns -1
	 *
	 * @param schedules List of course schedules
	 * @return Trainer number if available, -1 if not available
	 * @throws SQLException If database error occurs
	 */
	private int getTrainerNumber(List<List<Integer>> schedules) throws SQLException {
		Statement statement = dbconn.createStatement();
		String queryOne = "(SELECT t.trainerNumber FROM umidmuzrapov.Trainer t)" + " MINUS" + " (SELECT t.trainerNumber"
				+ " FROM umidmuzrapov.Trainer t, umidmuzrapov.Course c" + " WHERE t.trainerNumber = c.trainerNumber)";

		ResultSet result = statement.executeQuery(queryOne);

		if (result.next()) {
			return result.getInt("trainerNumber");
		} else {
			StringBuilder queryTwo = new StringBuilder();
			queryTwo.append("(SELECT DISTINCT t.trainerNumber FROM umidmuzrapov.Trainer t) "
					+ " MINUS (SELECT DISTINCT t.trainerNumber"
					+ " FROM umidmuzrapov.Trainer t, umidmuzrapov.Course c, umidmuzrapov.Schedule s"
					+ " WHERE t.trainerNumber=c.trainerNumber AND s.className = c.className AND s.startDate = c.startDate"
					+ " AND (?busy))");
			StringBuilder busy = new StringBuilder();

			for (List<Integer> schedule : schedules) {
				String busySchedule = "OR (s.day = day? AND s.hour = hour? AND s.minute = minute?) ";
				busySchedule = busySchedule.replace("day?", String.valueOf(schedule.get(0)));
				busySchedule = busySchedule.replace("hour?", String.valueOf(schedule.get(1)));
				busySchedule = busySchedule.replace("minute?", String.valueOf(schedule.get(2)));
				busy.append(busySchedule);
			}

			String finalQuery = queryTwo.toString().replace("?busy", busy.toString().replaceFirst("OR", ""));
			ResultSet availableTrainer = statement.executeQuery(finalQuery);

			if (availableTrainer.next()) {
				return availableTrainer.getInt("trainerNumber");
			} else {
				return -1;
			}
		}
	}

	/**
	 * Method deleteCourse
	 *
	 * Purpose: Deletes a course and associated records from the database
	 *
	 * Pre-condition: Valid course name and start date, connection established
	 *
	 * Post-condition: Course and associated enrollments/records deleted
	 *
	 * Logic: - Checks if there are enrolled members - Prints enrolled members and
	 * prevents deletion - Deletes enrollments associated with the course - Deletes
	 * the course - On error, rolls back transaction
	 * 
	 * @param className Name of course to delete
	 * @param startDate Course start date
	 * @return True if deleted successfully, false otherwise
	 */
	public boolean deleteCourse(String className, Date startDate) {
		try {
			dbconn.setAutoCommit(false);

			// Check for enrolled members
			String enrolledMembersQuery = "select m.fname, m.lname, m.phoneNumber from umidmuzrapov.member m join umidmuzrapov.enrollment e on m.memberNumber = e.memberNumber where e.courseName = ? and e.startDate = ?";
			PreparedStatement enrolledStmt = dbconn.prepareStatement(enrolledMembersQuery);
			enrolledStmt.setString(1, className);
			enrolledStmt.setDate(2, new java.sql.Date(startDate.getTime()));
			ResultSet enrolledMembers = enrolledStmt.executeQuery();

			boolean hasEnrolledMembers = false;
			System.out.println("Notigy these members");
			while (enrolledMembers.next()) {
				hasEnrolledMembers = true;
				String memberName = enrolledMembers.getString("fname") + " " + enrolledMembers.getString("lname");
				String phoneNumber = enrolledMembers.getString("phoneNumber");
				System.out.println("Member: " + memberName + ", Phone: " + phoneNumber);
			}

			System.out.println("Notification is done.");
			hasEnrolledMembers = false;
			if (hasEnrolledMembers) {
				// Notify the admin/user to contact these members before proceeding with
				// deletion
				dbconn.rollback();
				return false; // Prevent deletion until members are notified
			}

			// Delete the course enrollments
			System.out.println("Enrollment is being updated ...");
			String deleteEnrollmentsQuery = "delete from umidmuzrapov.enrollment where courseName = ? and startDate = ?";
			PreparedStatement deleteEnrollmentsStmt = dbconn.prepareStatement(deleteEnrollmentsQuery);
			deleteEnrollmentsStmt.setString(1, className);
			deleteEnrollmentsStmt.setDate(2, new java.sql.Date(startDate.getTime()));
			deleteEnrollmentsStmt.executeUpdate();
			System.out.println("Enrollment is updated.");
			dbconn.commit();

			// Delete the course
			System.out.println("Deleting a course from package.");
			String deleteCourseFromPackage = "delete from umidmuzrapov.CoursePackage where className=? and startDate=?";
			PreparedStatement deleteCourseFromPackageStm = dbconn.prepareStatement(deleteCourseFromPackage);
			deleteCourseFromPackageStm.setString(1, className);
			deleteCourseFromPackageStm.setDate(2, new java.sql.Date(startDate.getTime()));
			deleteCourseFromPackageStm.executeUpdate();
			System.out.println("Deleted a course from package.");

			// deleting course schedule
			System.out.println("Deleting a course from schedule.");
			String deleteCourseFromSchedule = "delete from umidmuzrapov.Schedule where className=? and startDate=?";
			PreparedStatement deleteCourseFromScheduleStm = dbconn.prepareStatement(deleteCourseFromSchedule);
			deleteCourseFromScheduleStm.setString(1, className);
			deleteCourseFromScheduleStm.setDate(2, new java.sql.Date(startDate.getTime()));
			deleteCourseFromScheduleStm.executeUpdate();
			System.out.println("Deleted a course from schedule.");

			String deleteCourseQuery = "delete from umidmuzrapov.course where className = ? and startDate = ?";
			PreparedStatement deleteCourseStmt = dbconn.prepareStatement(deleteCourseQuery);
			deleteCourseStmt.setString(1, className);
			deleteCourseStmt.setDate(2, new java.sql.Date(startDate.getTime()));
			deleteCourseStmt.executeUpdate();

			dbconn.commit();
			System.out.println("The course has been deleted.");
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				dbconn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return false;
		} finally {
			try {
				dbconn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method listCoursesInPackage
	 * 
	 * Purpose: Gets courses associated with a package
	 *
	 * Pre-condition: Valid package name, connection established
	 *
	 * Post-condition: List of courses returned
	 * 
	 * @param packageName Name of package
	 * @return List containing courseName and startDate
	 */
	public List<String[]> listCoursesInPackage(String packageName) {
		List<String[]> courses = new ArrayList<>();
		String query = "SELECT className, startDate FROM umidmuzrapov.CoursePackage WHERE packageName = ?";
		try (PreparedStatement preparedStatement = dbconn.prepareStatement(query)) {
			preparedStatement.setString(1, packageName);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String className = resultSet.getString("className");
					Date startDate = resultSet.getDate("startDate");
					courses.add(new String[] { className, startDate.toString() });
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courses;
	}

	/**
	 * Method removeCourseFromPackage
	 *
	 * Purpose: Removes a course from a package
	 *
	 * Pre-condition: Valid package name and course info, connection established
	 *
	 * Post-condition: Course association removed from database
	 * 
	 * @param packageName Package name
	 * @param className   Course name
	 * @param startDate   Course start date
	 * @return True if removed successfully, false otherwise
	 */
	public boolean removeCourseFromPackage(String packageName, String className, String startDate) {
		String deleteQuery = "DELETE FROM umidmuzrapov.CoursePackage WHERE packageName = ? AND className = ? AND startDate = ?";

		try (PreparedStatement preparedStatement = dbconn.prepareStatement(deleteQuery)) {
			preparedStatement.setString(1, packageName);
			preparedStatement.setString(2, className);
			preparedStatement.setDate(3, Date.valueOf(startDate));
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Method addCourseToPackage
	 *
	 * Purpose: Adds a course to an existing package
	 * 
	 * Pre-condition: Valid package name, connection established
	 * 
	 * Post-condition: Course added to package in database
	 * 
	 * @param packageName     Name of package
	 * @param selectedCourses List of courses to add
	 * @return True if successful, false otherwise
	 */
	public boolean addCourseToPackage(String packageName, String className, String startDate) {
		String insertQuery = "INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES (?, ?, ?)";

		try (PreparedStatement preparedStatement = dbconn.prepareStatement(insertQuery)) {
			preparedStatement.setString(1, packageName);
			preparedStatement.setString(2, className);
			preparedStatement.setDate(3, Date.valueOf(startDate));
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Method isCourseInPackage
	 * 
	 * Purpose: This method checks whether a specific course, identified by its
	 * class name and start date, is already included in a given course package. It
	 * queries the database to determine if the specified course is part of the
	 * course package.
	 * 
	 * Pre-condition: A valid connection to the DBMS must be established. The
	 * parameters provided, packageName, className, and startDate, should be
	 * non-null and correspond to existing entries in the database.
	 * 
	 * Post-condition: Returns true if the specified course is found in the
	 * specified package; otherwise, returns false. In case of a SQL exception, an
	 * error message is printed, and false is returned, indicating the course is not
	 * in the package (or that the query could not be reliably executed).
	 * 
	 * @param packageName The name of the course package to check.
	 * @param className   The name of the class to check for in the package.
	 * @param startDate   The start date of the class, used to uniquely identify the
	 *                    course.
	 * @return A boolean value: true if the course is in the package, false
	 *         otherwise.
	 */
	public boolean isCourseInPackage(String packageName, String className, String startDate) {
		String query = "SELECT COUNT(*) FROM umidmuzrapov.CoursePackage WHERE packageName = ? AND className = ? AND startDate = ?";

		try (PreparedStatement preparedStatement = dbconn.prepareStatement(query)) {
			preparedStatement.setString(1, packageName);
			preparedStatement.setString(2, className);
			preparedStatement.setDate(3, Date.valueOf(startDate));

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Method listOngoingCourses
	 * 
	 * Purpose: Retrieves a list of all ongoing courses with their start and end
	 * dates
	 * 
	 * Pre-condition: Connection to database established
	 *
	 * Post-condition: List of ongoing courses returned
	 * 
	 * @return List of string arrays containing courseName, startDate, endDate
	 */
	public List<String[]> listOngoingCourses() {
		List<String[]> ongoingCourses = new ArrayList<>();
		try {
			String query = "select * from umidmuzrapov.course where endDate > CURRENT_DATE";
			PreparedStatement preparedStatement = dbconn.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String className = resultSet.getString("className");
				Date startDate = resultSet.getDate("startDate");
				Date endDate = resultSet.getDate("endDate");
				ongoingCourses.add(new String[] { className, startDate.toString(), endDate.toString() });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ongoingCourses;
	}

	/**
	 * Method addPackage
	 *
	 * Purpose: Adds a new package to the Package table
	 *
	 * Pre-condition: Valid package name and price, connection established
	 * 
	 * Post-condition: New package inserted into database
	 *
	 * @param packageName  Name of the package
	 * @param packagePrice Price of the package
	 * @return True if package inserted successfully, false otherwise
	 */
	public boolean addPackage(String packageName, int packagePrice) {
		String insertPackageQuery = "INSERT INTO umidmuzrapov.Package (packageName, cost) VALUES (?, ?)";

		try (PreparedStatement preparedStatement = dbconn.prepareStatement(insertPackageQuery)) {
			preparedStatement.setString(1, packageName);
			preparedStatement.setInt(2, packagePrice); // Set the package price in the prepared statement
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Method addCoursePackage
	 *
	 * Purpose: Adds selected courses to a package
	 *
	 * Pre-condition: Valid package name, valid courses, connection established
	 *
	 * Post-condition: Course associations added to database
	 *
	 * Logic: - Checks if package exists - Validates each course - Adds courses to
	 * CoursePackage table - Commits on success, rolls back on failure
	 *
	 * @param packageName     Name of package
	 * @param selectedCourses List of courses to add
	 * @return True if successful, false otherwise
	 */
	public boolean addCoursePackage(String packageName, List<String[]> selectedCourses) {
		String checkPackageQuery = "SELECT COUNT(*) FROM umidmuzrapov.Package WHERE packageName = ?";
		String checkCourseQuery = "SELECT COUNT(*) FROM umidmuzrapov.Course WHERE className = ? AND startDate = ?";
		String insertQuery = "INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES (?, ?, ?)";

		try {
			dbconn.setAutoCommit(false);

			// Check if the package exists
			try (PreparedStatement checkPackageStmt = dbconn.prepareStatement(checkPackageQuery)) {
				checkPackageStmt.setString(1, packageName);
				ResultSet rs = checkPackageStmt.executeQuery();
				if (rs.next() && rs.getInt(1) == 0) {
					throw new SQLException("Package not found: " + packageName);
				}
			}

			// Prepare the insert statement
			try (PreparedStatement preparedStatement = dbconn.prepareStatement(insertQuery)) {
				for (String[] course : selectedCourses) {
					// Check if each course exists
					try (PreparedStatement checkCourseStmt = dbconn.prepareStatement(checkCourseQuery)) {
						checkCourseStmt.setString(1, course[0]);
						checkCourseStmt.setDate(2, Date.valueOf(course[1]));
						ResultSet rs = checkCourseStmt.executeQuery();
						if (rs.next() && rs.getInt(1) == 0) {
							throw new SQLException("Course not found: " + course[0] + ", " + course[1]);
						}
					}

					// Add to batch
					preparedStatement.setString(1, packageName);
					preparedStatement.setString(2, course[0]);
					preparedStatement.setDate(3, Date.valueOf(course[1]));
					preparedStatement.addBatch();
				}
				preparedStatement.executeBatch();
			}

			dbconn.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				dbconn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return false;
		} finally {
			try {
				dbconn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method updateCoursePackage
	 *
	 * Purpose: Updates courses in an existing package
	 *
	 * Pre-condition: Valid package name, valid course info, connection established
	 *
	 * Post-condition: Package courses updated in database
	 *
	 * @param packageName    Name of package
	 * @param updatedCourses Updated list of package courses
	 * @return True if successful, false otherwise
	 */
	public boolean updateCoursePackage(String packageName, List<String[]> updatedCourses) {
		try {
			dbconn.setAutoCommit(false);

			if (!canUpdateCoursePackage(packageName, updatedCourses)) {
				// Handle the case where update is not possible
				return false;
			}

			// First, delete existing courses in the package
			String deleteQuery = "delete from umidmuzrapov.coursepackage where packageName = ?";
			PreparedStatement deleteStmt = dbconn.prepareStatement(deleteQuery);
			deleteStmt.setString(1, packageName);
			deleteStmt.executeUpdate();

			// Then, add the updated courses
			for (String[] course : updatedCourses) {
				String className = course[0];
				Date startDate = Date.valueOf(course[1]);

				String insertQuery = "insert into umidmuzrapov.coursepackage (packageName, className, startDate) values (?, ?, ?)";
				PreparedStatement preparedStatement = dbconn.prepareStatement(insertQuery);
				preparedStatement.setString(1, packageName);
				preparedStatement.setString(2, className);
				preparedStatement.setDate(3, startDate);
				preparedStatement.executeUpdate();
			}

			dbconn.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				dbconn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return false;
		} finally {
			try {
				dbconn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method canUpdateCoursePackage
	 * 
	 * Purpose: Checks if a course package can be updated
	 *
	 * Pre-condition: Valid package name and course data, connection established
	 *
	 * Post-condition: None
	 *
	 * Logic: - Iterates through updated courses - Checks if there are active
	 * enrollments for each course - Returns false if active enrollments found, true
	 * if safe to update
	 * 
	 * @param packageName    Package name
	 * @param updatedCourses Updated list of courses
	 * @return True if package can be updated, false otherwise
	 */
	private boolean canUpdateCoursePackage(String packageName, List<String[]> updatedCourses) {
		try {
			for (String[] course : updatedCourses) {
				String className = course[0];
				Date startDate = Date.valueOf(course[1]);

				String checkQuery = "select count(*) from umidmuzrapov.enrollment e where e.courseName = ? and e.startDate = ?";
				PreparedStatement checkStmt = dbconn.prepareStatement(checkQuery);
				checkStmt.setString(1, className);
				checkStmt.setDate(2, startDate);
				ResultSet rs = checkStmt.executeQuery();

				if (rs.next() && rs.getInt(1) > 0) {
					// There are active enrollments in this course
					return false;
				}
			}
			return true; // Safe to update the package
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Method deleteCoursePackage
	 * 
	 * Purpose: Deletes a course package and associated records
	 *
	 * Pre-condition: Valid package name, connection established
	 *
	 * Post-condition: Package and associated records removed from database
	 * 
	 * @param packageName Name of package
	 * @return True if deleted successfully, false otherwise
	 */
	public boolean deleteCoursePackage(String packageName) {
		String deleteCoursePackageQuery = "DELETE FROM umidmuzrapov.CoursePackage WHERE packageName = ?";
		String deletePackageQuery = "DELETE FROM umidmuzrapov.Package WHERE packageName = ?";

		try {
			dbconn.setAutoCommit(false);

			// Delete course packages
			try (PreparedStatement preparedStatement = dbconn.prepareStatement(deleteCoursePackageQuery)) {
				preparedStatement.setString(1, packageName);
				preparedStatement.executeUpdate();
			}

			// Delete package
			try (PreparedStatement preparedStatement = dbconn.prepareStatement(deletePackageQuery)) {
				preparedStatement.setString(1, packageName);
				preparedStatement.executeUpdate();
			}

			dbconn.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				dbconn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return false;
		} finally {
			try {
				dbconn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method queryOne
	 * 
	 * Purpose: Gets members with negative account balances
	 *
	 * Pre-condition: Database connection initialized
	 * 
	 * Post-condition: List of members with negative balances returned
	 * 
	 * @return List of member name, phone, and balance info
	 */
	public List<String[]> queryOne() {
		List<String[]> membersWithNegativeBalance = new ArrayList<>();
		try {
			// SQL query to find members with negative balance
			String query = "select m.fname, m.lname, m.phoneNumber, "
					+ "SUM(case when t.type = 'deposit' then t.total else -t.total end) as balance from "
					+ "umidmuzrapov.member m join umidmuzrapov.transaction t on m.memberNumber = t.memberNumber group by "
					+ "m.memberNumber, m.fname, m.lname, m.phoneNumber having SUM(case when t.type = 'deposit' "
					+ "then t.total else -t.total end) < 0";

			Statement statement = dbconn.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				String firstName = resultSet.getString("fname");
				String lastName = resultSet.getString("lname");
				String phoneNumber = resultSet.getString("phoneNumber");
				String balance = resultSet.getString("balance");

				membersWithNegativeBalance.add(new String[] { firstName + " " + lastName, phoneNumber, balance });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return membersWithNegativeBalance;
	}

	/**
	 * Method queryTwo
	 * 
	 * Purpose: Gets a member's November class schedule
	 *
	 * Pre-condition: Valid member number, database connection initialized
	 *
	 * Post-condition: November schedule returned
	 *
	 * @param memberNumber The member's ID number
	 * @return List of schedule info for November classes
	 */
	public List<String[]> queryTwo(int memberNumber) {
		List<String[]> novemeberClassSchedule = new ArrayList<>();
		try {
			String query = String.format(
					"select e.courseName, c.startDate, c.endDate, s.day, s.hour, s.minute, s.duration "
							+ "from umidmuzrapov.enrollment e join umidmuzrapov.course c on e.courseName = c.className and e.startDate = c.startDate "
							+ "join umidmuzrapov.schedule s on c.className = s.className and c.startDate = s.startDate where e.memberNumber = %d "
							+ "and c.startDate <= TO_DATE('30-11-2023', 'DD-MM-YYYY') and c.endDate >= TO_DATE('01-11-2023', 'DD-MM-YYYY')",
					memberNumber);

			Statement statement = dbconn.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				String courseName = resultSet.getString("courseName");
				Date startDate = resultSet.getDate("startDate");
				Date endDate = resultSet.getDate("endDate");
				int day = resultSet.getInt("day");
				int hour = resultSet.getInt("hour");
				int minute = resultSet.getInt("minute");
				int duration = resultSet.getInt("duration");

				novemeberClassSchedule.add(new String[] { courseName, startDate.toString(), endDate.toString(),
						intToDay(day), Integer.toString(hour), Integer.toString(minute), Integer.toString(duration) });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return novemeberClassSchedule;
	}

	/**
	 * Method queryThree
	 *
	 * Purpose: Gets trainers' working hours in December
	 *
	 * Pre-condition: Database connection initialized
	 *
	 * Post-condition: List of trainer schedule info returned
	 * 
	 * @return Trainer schedule data for December
	 */
	public List<String[]> queryThree() {
		List<String[]> trainerWorkingHours = new ArrayList<>();
		try {
			// Fetch schedule data
			String query = "select t.trainerNumber, t.fname, t.lname, s.day, s.hour, s.minute, s.duration, c.startDate, c.endDate "
					+ "from umidmuzrapov.trainer t join umidmuzrapov.course c on t.trainerNumber = c.trainerNumber "
					+ "join umidmuzrapov.schedule s on c.className = s.className and c.startDate = s.startDate "
					+ "where c.startDate <= TO_DATE('31-12-2023', 'DD-MM-YYYY') and c.endDate >= TO_DATE('01-12-2023', 'DD-MM-YYYY') "
					+ "group by t.trainerNumber, t.fname, t.lname, s.day, s.hour, s.minute, s.duration, c.startDate, c.endDate "
					+ "order by t.trainerNumber, s.day, s.hour";

			Statement statement = dbconn.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			List<String[]> rawScheduleData = new ArrayList<>();

			while (resultSet.next()) {
				String trainerNumber = resultSet.getString("trainerNumber");
				String firstName = resultSet.getString("fname");
				String lastName = resultSet.getString("lname");
				int day = resultSet.getInt("day");
				int hour = resultSet.getInt("hour");
				int minute = resultSet.getInt("minute");
				int duration = resultSet.getInt("duration");
				String startDate = resultSet.getString("startDate");
				String endDate = resultSet.getString("endDate");
				rawScheduleData.add(new String[] { trainerNumber, firstName + " " + lastName, String.valueOf(day),
						String.valueOf(hour), String.valueOf(minute), String.valueOf(duration), startDate, endDate });
			}

			// Calculate total hours
			Map<String, Integer> totalHours = calculateTotalHours(rawScheduleData);

			// Add to trainerWorkingHours only if the trainer has scheduled hours
			for (String[] data : rawScheduleData) {
				String trainerKey = data[0];
				if (totalHours.containsKey(trainerKey)) {
					String[] combinedData = new String[] { data[0], // Trainer number
							data[1], // Trainer name
							intToDay(Integer.parseInt(data[2])), // Day

							data[3], // Hour
							data[4], // Minute
							data[5], // Duration
							String.valueOf(totalHours.get(trainerKey)) // Total hours
					};

					trainerWorkingHours.add(combinedData);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return trainerWorkingHours;
	}

	/**
	 * Method calculateTotalHours
	 * 
	 * Purpose: Calculates total trainer hours for the month
	 *
	 * Pre-condition: Valid schedule data passed in
	 * 
	 * Post-condition: Trainer total hours map returned
	 * 
	 * @param scheduleData List of trainer schedule entries
	 * @return Map of trainer IDs to total hours
	 */
	private Map<String, Integer> calculateTotalHours(List<String[]> scheduleData) {
		Map<String, Integer> trainerTotalHours = new HashMap<>();

		for (String[] entry : scheduleData) {

			// Parse input strings into LocalDateTime objects
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime startDate = LocalDateTime.parse(entry[6], formatter);
			LocalDateTime endDate = LocalDateTime.parse(entry[7], formatter);

			// Clean the time part (set it to 00:00:00)
			startDate = startDate.withHour(0).withMinute(0).withSecond(0);
			endDate = endDate.withHour(0).withMinute(0).withSecond(0);

			// Call the function to count occurrences of each day of the week
			int[] dayOfWeekCount = countDaysOfWeek(startDate, endDate);

			String trainerKey = entry[0]; // Use trainer number as key
			int dayOfWeek = Integer.parseInt(entry[2]);
			int duration = Integer.parseInt(entry[5]);
			int totalHours = duration * dayOfWeekCount[dayOfWeek - 1];
			trainerTotalHours.put(trainerKey, trainerTotalHours.getOrDefault(trainerKey, 0) + totalHours);
		}

		return trainerTotalHours;
	}

	// Function to count occurrences of each day of the week within a date range
	private static int[] countDaysOfWeek(LocalDateTime startDate, LocalDateTime endDate) {
		// Calculate the number of days between the two dates (inclusive)
		long daysBetweenInclusive = ChronoUnit.DAYS.between(startDate, endDate) + 1;

		// Count the occurrences of each day of the week
		int[] dayOfWeekCount = new int[7];

		for (int i = 0; i < daysBetweenInclusive; i++) {
			DayOfWeek dayOfWeek = startDate.plusDays(i).getDayOfWeek();
			int dayIndex = dayOfWeek.getValue() - 1; // Adjust to 0-indexed for array
			dayOfWeekCount[dayIndex]++;
		}

		return dayOfWeekCount;
	}

	/**
	 * Method printCourseSchedule
	 *
	 * Purpose: Prints the course schedule for given member
	 *
	 * Pre-condition: Valid first and last names, member exists
	 *
	 * Post-condition: Schedule printed to console
	 *
	 * @param firstname First name of member
	 * @param lastname  Last name of member
	 */
	public void printCourseSchedule(String firstname, String lastname) throws SQLException {

		int memberNumber = memberExists(firstname, lastname);
		if (memberNumber < 0) {
			System.out.println("No member with this name exists.");
			return;
		}

		String query = String.format("SELECT c.className, c.startDate, s.day, s.hour, s.minute, s.duration"
				+ " FROM umidmuzrapov.Member m, umidmuzrapov.Course c, umidmuzrapov.Enrollment e, umidmuzrapov.Schedule s"
				+ " WHERE m.memberNumber=e.memberNumber AND m.memberNumber=%d"
				+ " AND c.className=e.courseName AND c.startDate=e.startDate AND s.className=c.className AND s.startDate=c.startDate ORDER BY s.day",
				memberNumber);

		Statement statement = dbconn.createStatement();
		ResultSet result = statement.executeQuery(query);

		System.out.printf("Schedule for %s %s\n", firstname, lastname);
		while (result.next()) {
			System.out.printf("Course %s. %s, %d:%d. Duration: %d.\n", result.getString("className"),
					intToDay(result.getInt("day")), result.getInt("hour"), result.getInt("minute"),
					result.getInt("duration"));
		}

	}

	/**
	 * Method memberExists
	 *
	 * Purpose: Checks if a member with the given first and last names exists
	 * 
	 * Pre-condition: Connection established
	 * 
	 * Post-condition: Member number returned if member exists, -1 otherwise
	 *
	 * @param firstname First name of member
	 * @param lastname  Last name of member
	 * @return Member number if exists, -1 otherwise
	 */
	public int memberExists(String firstname, String lastname) throws SQLException {
		Statement statement = dbconn.createStatement();
		String query = "SELECT * FROM umidmuzrapov.Member" + " WHERE fname='1?' and lname='2?'";
		query = query.replace("1?", firstname).replace("2?", lastname);
		ResultSet result = statement.executeQuery(query);
		ArrayList<Integer> memberNumbers = new ArrayList<Integer>();

		while (result.next()) {
			int memberNumber = result.getInt("memberNumber");
			memberNumbers.add(memberNumber);
		}

		if (memberNumbers.size() == 0) {
			return -1;
		} else if (memberNumbers.size() == 1) {
			return memberNumbers.remove(0);
		} else {
			System.out.println("There are several numbers with this name. Ask the member for id and check."
					+ "\nEnter one of the options:");
			int count = 0;
			for (int elem : memberNumbers) {
				System.out.printf("%d) %d", count, elem);
			}

			Scanner keyboard = new Scanner(System.in);
			try {
				int userChoice = keyboard.nextInt();
				if (userChoice < memberNumbers.size()) {
					return memberNumbers.get(userChoice);
				} else {
					System.out.println("You entered the wrong format.");
					return -1;
				}
			}

			catch (InputMismatchException ex) {
				System.out.println("You entered the wrong format.");
				return -1;
			}
		}
	}

	/**
	 * Method listAllPackages
	 * 
	 * Purpose: Retrieves names of all packages from the database
	 *
	 * Pre-condition: Connection established
	 *
	 * Post-condition: List of package names returned
	 * 
	 * @return List of package name strings
	 */
	public List<String> listAllPackages() {
		List<String> packages = new ArrayList<>();
		String query = "SELECT packageName FROM umidmuzrapov.Package";
		try (PreparedStatement preparedStatement = dbconn.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				packages.add(resultSet.getString("packageName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return packages;
	}

	/**
	 * Method intToDay
	 *
	 * Purpose: Converts integer day of week to string name
	 *
	 * Pre-condition: Valid day of week integer
	 * 
	 * Post-condition: String day name returned
	 * 
	 * @param day Integer day of week
	 * @return String name for day
	 */
	private String intToDay(int day) {
		switch (day) {
		case 1:
			return "Monday";
		case 2:
			return "Tueday";
		case 3:
			return "Wednesday";
		case 4:
			return "Thursday";
		case 5:
			return "Friday";
		case 6:
			return "Satruday";
		case 7:
			return "Sunday";
		default:
			return "None";
		}
	}

	/**
	 * Method listAllPackagesAndCourses
	 *
	 * Purpose: retrieves a list of all packages along with their costs and
	 * associated courses.
	 *
	 * Pre-condition: the package and coursePackage tables exist and are accessible.
	 * 
	 * Post-condition: returns a list of String arrays, each containing package
	 * name, cost, and courses.
	 * 
	 * @return list of all the package information
	 */

	public List<String[]> listAllPackagesAndCourses() {
		List<String[]> packageDetails = new ArrayList<>();
		try {
			String query = "select p.packageName, p.cost, LISTAGG(cp.className, ', ') within group (order by cp.className) as courses "
					+ "from umidmuzrapov.Package p join umidmuzrapov.coursepackage cp on p.packageName = cp.packageName "
					+ "group by p.packageName, p.cost";
			Statement statement = dbconn.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				String packageName = resultSet.getString("packageName");
				double cost = resultSet.getDouble("cost");
				String courses = resultSet.getString("courses");
				packageDetails.add(new String[] { packageName, Double.toString(cost), courses });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return packageDetails;
	}

	/**
	 * Method packagePurchased
	 *
	 * Purpose: when a member purchases a package, it adds the transaction to the
	 * table, records the purchase, updates the course participants for each course
	 * in the package, and finally updates the member levelId based on amount spent.
	 *
	 * Pre-condition: chosen package name, its cost, and the memberNumber
	 * 
	 * Post-condition: returns a string confirming the purchase of a package.
	 * 
	 * @return conformation message;
	 */
	public String packagePurchased(String packageName, String cost, int memberNumber) {
		try {
			dbconn.setAutoCommit(false);
			// get max transaction number
			String maxTransactionNumberQuery = "select MAX(transactionNumber) from umidmuzrapov.transaction";
			Statement maxTransactionNumberStmt = dbconn.createStatement();
			ResultSet rs = maxTransactionNumberStmt.executeQuery(maxTransactionNumberQuery);
			int nextTransactionNumber = 1;
			if (rs.next()) {
				nextTransactionNumber = rs.getInt(1) + 1;
			}

			// add a transaction for the package purchase
			String transactionQuery = String.format(
					"insert into umidmuzrapov.transaction (transactionNumber, memberNumber, total, transactionDate, type) "
							+ "VALUES (%d, %d, %f, CURRENT_DATE, 'deposit')",
					nextTransactionNumber, memberNumber, Double.parseDouble(cost));
			Statement transactionStmt = dbconn.createStatement();
			transactionStmt.executeUpdate(transactionQuery);

			// add the package to the Purchase table
			String purchaseQuery = String.format(
					"insert into umidmuzrapov.purchase (packageName, transactionNumber) VALUES ('%s', %d)", packageName,
					nextTransactionNumber);
			Statement purchaseStmt = dbconn.createStatement();
			purchaseStmt.executeUpdate(purchaseQuery);

			// update the currentParticipants for each course in the package and add to the
			// enrollment table
			String upcomingCoursesQuery = String
					.format("select cp.className, cp.startDate from umidmuzrapov.coursepackage cp "
							+ "join umidmuzrapov.course c on cp.className = c.className and cp.startDate = c.startDate "
							+ "where cp.packageName = '%s' AND c.startDate >= CURRENT_DATE", packageName);

			Statement upcomingCoursesStmt = dbconn.createStatement();
			ResultSet upcomingCourses = upcomingCoursesStmt.executeQuery(upcomingCoursesQuery);

			// enroll the member in each course and update currentParticipant
			while (upcomingCourses.next()) {
				String className = upcomingCourses.getString("className");
				Date startDate = upcomingCourses.getDate("startDate");

				// Insert into enrollment table
				String enrollQuery = String.format(
						"insert into umidmuzrapov.enrollment (memberNumber, courseName, startDate) "
								+ "VALUES (%d, '%s', TO_DATE('%tF', 'YYYY-MM-DD'))",
						memberNumber, className, startDate);
				Statement enrollStmt = dbconn.createStatement();
				enrollStmt.executeUpdate(enrollQuery);

				// Update currentParticipant in course table
				String updateParticipantsQuery = String.format(
						"update umidmuzrapov.course set currentParticipant = currentParticipant + 1 "
								+ "where className = '%s' and startDate = TO_DATE('%tF', 'YYYY-MM-DD')",
						className, startDate);
				Statement updateParticipantsStmt = dbconn.createStatement();
				updateParticipantsStmt.executeUpdate(updateParticipantsQuery);
			}

			// update the member's levelId based on amount spent
			String levelUpdateQuery = String.format(
					"update umidmuzrapov.member set levelId = case when (select SUM(total) "
							+ "from umidmuzrapov.transaction where memberNumber = %d) >= 1000 then 3 when (select SUM(total) from umidmuzrapov.transaction "
							+ "where memberNumber = %d) >= 500 THEN 2 ELSE levelId END WHERE memberNumber = %d",
					memberNumber, memberNumber, memberNumber);
			Statement levelUpdateStmt = dbconn.createStatement();
			levelUpdateStmt.executeUpdate(levelUpdateQuery);

			dbconn.commit(); // Commit transaction if all operations are successful
			return "Package purchased successfully!";
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				dbconn.rollback(); // Rollback transaction in case of error
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return "Package purchase error";
		}
	}

}
