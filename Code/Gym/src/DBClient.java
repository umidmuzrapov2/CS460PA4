import java.sql.*;
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
		}

		catch (ClassNotFoundException e) {
			System.out.println("Class not found exception. Error loading Oracle JDBC driver");
			System.exit(-1);
		}

		Connection dbconn = null;

		try {
			// establish connection
			dbconn = DriverManager.getConnection(oracleURL, username, password);
		}

		catch (SQLException e) {
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
	public boolean addMember(String firstName, String lastName, String phoneNumber, int levelId) {
	    try {
	        // Create a SQL statement to insert a new member into the database
	        String insertQuery = "INSERT INTO MEMBER (MEMBERNUMBER, FNAME, LNAME, PHONENUMBER, LEVELID) " +
	                             "VALUES (SEQ_MEMBER.NEXTVAL, ?, ?, ?, ?)";
	        
	        PreparedStatement preparedStatement = dbconn.prepareStatement(insertQuery);
	        preparedStatement.setString(1, firstName);
	        preparedStatement.setString(2, lastName);
	        preparedStatement.setString(3, phoneNumber);
	        preparedStatement.setInt(4, levelId);
	        
	        // Execute the SQL statement to add the member
	        int rowsAffected = preparedStatement.executeUpdate();
	        
	        // Check if the member was successfully added
	        if (rowsAffected > 0) {
	            return true; // Member added successfully
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return false; // Member addition failed
	}
	
	/**
	 * 
	 * @param memberNumber
	 * @return
	 */
	public boolean deleteMember(int memberNumber) {
	    try {
	        // Check if the member has unreturned equipment
	        if (hasUnreturnedEquipment(memberNumber)) {
	            // Mark the equipment as lost and update quantities (you need to implement this logic)
	            // Return false or handle the case where equipment is unreturned
	        }

	        // Check if the member has unpaid balances
	        if (hasUnpaidBalances(memberNumber)) {
	            // Print unpaid balances and prevent deletion (you need to implement this logic)
	            // Return false or handle the case where there are unpaid balances
	        }

	        // Check if the member is actively participating in any courses
	        if (isParticipatingInCourses(memberNumber)) {
	            // Delete course participation records and update course spots (you need to implement this logic)
	        }

	        // If all checks pass, delete the member
	        String deleteQuery = "DELETE FROM MEMBER WHERE MEMBERNUMBER = ?";

	        PreparedStatement preparedStatement = dbconn.prepareStatement(deleteQuery);
	        preparedStatement.setInt(1, memberNumber);

	        // Execute the SQL statement to delete the member
	        int rowsAffected = preparedStatement.executeUpdate();

	        // Check if the member was successfully deleted
	        if (rowsAffected > 0) {
	            return true; // Member deleted successfully
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return false; // Member deletion failed
	}

	/**
	 * Implement logic to check if the member has unreturned equipment
	 * 
	 * Return true if unreturned equipment is found; otherwise, return false
	 */
	private boolean hasUnreturnedEquipment(int memberNumber) {
		return false;
	}

	/**
	 * Implement logic to check if the member has unpaid balances
	 * 
	 * Return true if unpaid balances are found; otherwise, return false
	 */
	private boolean hasUnpaidBalances(int memberNumber) {
		return false;
	}

	/**
	 * Implement logic to check if the member is actively participating in any courses. 
	 * If so, delete course participation records and update course spots
	 * 
	 * Return true if participating in courses; otherwise, return false
	 */
	private boolean isParticipatingInCourses(int memberNumber) {
		return false;
	}

}
