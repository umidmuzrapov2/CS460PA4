import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class DBClient {
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
		String username = "umidmuzrapov";
		// default password
		String password = "a6549";

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
}
