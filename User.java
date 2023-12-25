import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class User {
	String url = "jdbc:mysql://localhost:3306/auction_system";
	String username = "root";
	String password = "";

	//userEmail, userpassword. constructor ile kurulacak.
	private String userEmail;
	private String userPassword;

	public User(String userEmail, String userPassword) {
		this.userEmail = userEmail;
		this.userPassword = userPassword;
	}


	public void login() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();

			// Check if there is a user with the provided email and password
			String query = "SELECT * FROM buyer, seller WHERE user_email = '" + userEmail + "' AND user_password = '" + userPassword + "'";
			ResultSet resultSet = statement.executeQuery(query);

			if (resultSet.next()) {
				// User is authenticated
				System.out.println("Login successful!");
			} else {
				// Authentication failed
				System.out.println("Invalid email or password. Please try again.");
			}

			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void register(String userFirstName, String userLastName,String userType) {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();

			// Insert the new user into the User table
			//String insertQueryUser = "INSERT INTO user (user_name, user_surname, user_email, user_password, user_type) " +
			//		"VALUES ('" + userFirstName + "', '" + userLastName + "', '" + userEmail + "', '" + userPassword + "', '" + userType + "')";
			int rowsAffectedUser = 1;//statement.executeUpdate(insertQueryUser);

			if (rowsAffectedUser > 0) {

				// Insert the new user into the appropriate table based on user type
				String insertQuery;
				if ("B".equalsIgnoreCase(userType)) {
					insertQuery = "INSERT INTO buyer (user_name, user_surname, user_email) " +
							"VALUES ('" + userFirstName + "', '" + userLastName + "', '" + userEmail + "')";
				} else if ("S".equalsIgnoreCase(userType)) {
					insertQuery = "INSERT INTO seller (user_name, user_surname, user_email) " +
							"VALUES ('" + userFirstName + "', '" + userLastName + "', '" + userEmail + "')";
				} else {
					System.out.println("Invalid user type. Please enter 'B' for buyer or 'S' for seller.");
					return;
				}

				int rowsAffected = statement.executeUpdate(insertQuery);

				if (rowsAffected > 0) {
					System.out.println("Registration successful!");
				} else {
					System.out.println("Registration failed. Please try again.");
				}
			} else {
				System.out.println("User registration failed. Please try again.");
			}

			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void watchMezat(){
		System.out.println("Watching Mezat Stream!");
	}


}
