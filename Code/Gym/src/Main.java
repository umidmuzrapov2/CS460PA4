

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
		// TODO Auto-generated method stub
		DBClient client = new DBClient(args);

	}

}
