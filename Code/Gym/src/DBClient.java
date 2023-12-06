import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DBClient {

	@SuppressWarnings("unused")
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
	 *
	 * @param firstName
	 * @param lastName
	 * @param phoneNumber
	 * @param levelId
	 * @return
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
	 *
	 * @param memberNumber
	 * @return
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

			// Check if the member has unpaid balances
			if (hasUnpaidBalances(memberNumber)) {
				String balanceQuery = String.format(
						"select transactionNumber, total from umidmuzrapov.transaction where memberNumber = %d and (type = 'Unpaid' or type is NULL)",
						memberNumber);
				Statement balanceStmt = dbconn.createStatement();
				ResultSet balanceResult = balanceStmt.executeQuery(balanceQuery);

				System.out.println("\nThe member has unpaid charge(s).");
				while (balanceResult.next()) {
					int transactionNumber = balanceResult.getInt("transactionNumber");
					float total = balanceResult.getFloat("total");
					System.out.println(
							"Unpaid Balance - Transaction Number: " + transactionNumber + ", Amount: " + total);
				}

				dbconn.rollback(); // Rollback transaction
				return "Cannot delete member due to unpaid charge(s)."; // Prevent deletion due to unpaid balances
			}

			// Check if the member is actively participating in any courses
			if (isParticipatingInCourses(memberNumber)) {
				String enrolledCoursesQuery = String.format(
						"select courseName, startDate from umidmuzrapov.enrollment where memberNumber = %d",
						memberNumber);
				Statement enrolledStmt = dbconn.createStatement();
				ResultSet enrolledCourses = enrolledStmt.executeQuery(enrolledCoursesQuery);

				System.out.println("\nThe member will be dropped from the following course(s):");
				while (enrolledCourses.next()) {
					String courseName = enrolledCourses.getString("courseName");
					String startDate = enrolledCourses.getString("startDate");
					System.out.println(courseName);
					String updateCourseEnrollment = String.format(
							"update from umidmuzrapov.course set currentParticipant = currentParticipant - 1 where courseName = %s and startDate = %s",
							courseName, startDate);
				}
				String deleteEnrollmentQuery = String
						.format("delete from umidmuzrapov.enrollment where memberNumber = %d", memberNumber);
				dbconn.createStatement().executeUpdate(deleteEnrollmentQuery);
			}

			// Additional steps to handle foreign key constraints before deleting the member
			// Delete related records from Transaction table
			String deleteTransactionQuery = String
					.format("delete from umidmuzrapov.transaction where memberNumber = %d", memberNumber);
			dbconn.createStatement().executeUpdate(deleteTransactionQuery);

			// Delete related records from EquipmentLoan table
			String deleteEquipmentLoanQuery = String
					.format("delete from umidmuzrapov.equipmentloan where memberItem = %d", memberNumber);
			dbconn.createStatement().executeUpdate(deleteEquipmentLoanQuery);

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
	 * Implement logic to check if the member has unreturned equipment
	 *
	 * Return true if unreturned equipment is found; otherwise, return false
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

	/**
	 * Implement logic to check if the member has unpaid balances
	 *
	 * Return true if unpaid balances are found; otherwise, return false
	 */
	private boolean hasUnpaidBalances(int memberNumber) {
		try {
			String query = String.format(
					"select count(*) from umidmuzrapov.transaction where memberNumber = %d and (type = 'Unpaid' or type is NULL)",
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
	 * Implement logic to check if the member is actively participating in any
	 * courses. If so, delete course participation records and update course spots
	 *
	 * Return true if participating in courses; otherwise, return false
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
	 * 
	 * @param className
	 * @param maxParticipant
	 * @param currentParticipant
	 * @param startDate
	 * @param endDate
	 * @param trainerNumber
	 * @return
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
			while (enrolledMembers.next()) {
				hasEnrolledMembers = true;
				String memberName = enrolledMembers.getString("fname") + " " + enrolledMembers.getString("lname");
				String phoneNumber = enrolledMembers.getString("phoneNumber");
				System.out.println("Member: " + memberName + ", Phone: " + phoneNumber);
			}

			if (hasEnrolledMembers) {
				// Notify the admin/user to contact these members before proceeding with
				// deletion
				dbconn.rollback();
				return false; // Prevent deletion until members are notified
			}

			// Delete the course enrollments
			String deleteEnrollmentsQuery = "delete from umidmuzrapov.enrollment where courseName = ? and startDate = ?";
			PreparedStatement deleteEnrollmentsStmt = dbconn.prepareStatement(deleteEnrollmentsQuery);
			deleteEnrollmentsStmt.setString(1, className);
			deleteEnrollmentsStmt.setDate(2, new java.sql.Date(startDate.getTime()));
			deleteEnrollmentsStmt.executeUpdate();

			// Delete the course
			String deleteCourseQuery = "delete from umidmuzrapov.course where className = ? and startDate = ?";
			PreparedStatement deleteCourseStmt = dbconn.prepareStatement(deleteCourseQuery);
			deleteCourseStmt.setString(1, className);
			deleteCourseStmt.setDate(2, new java.sql.Date(startDate.getTime()));
			deleteCourseStmt.executeUpdate();

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

	public boolean deleteCoursePackage(String packageName) {
		try {
			dbconn.setAutoCommit(false);

			if (!canDeleteCoursePackage(packageName)) {
				// Handle the case where deletion is not possible
				return false;
			}

			// Assuming safeguard check passed:
			String deleteQuery = "delete from umidmuzrapov.coursepackage where packageName = ?";
			PreparedStatement preparedStatement = dbconn.prepareStatement(deleteQuery);
			preparedStatement.setString(1, packageName);
			int rowsAffected = preparedStatement.executeUpdate();

			dbconn.commit();
			return rowsAffected > 0;
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

	private boolean canDeleteCoursePackage(String packageName) {
		try {
			String checkQuery = "select count(*) from umidmuzrapov.enrollment e join umidmuzrapov.coursepackage cp on e.courseName = cp.className and e.startDate = cp.startDate where cp.packageName = ?";
			PreparedStatement checkStmt = dbconn.prepareStatement(checkQuery);
			checkStmt.setString(1, packageName);
			ResultSet rs = checkStmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				// There are active enrollments in this package's courses
				return false;
			}
			return true; // Safe to delete the package
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

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

				novemeberClassSchedule
						.add(new String[] { courseName, startDate.toString(), endDate.toString(), intToDay(day),
								Integer.toString(hour), Integer.toString(minute), Integer.toString(duration) });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return novemeberClassSchedule;
	}

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

}
