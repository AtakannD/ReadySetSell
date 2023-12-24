import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class User {
	String url = "jdbc:mysql://localhost:3306/auction_system";
	String username = "root";
	String password = "";

	Scanner scanner;

	public User() {
		scanner = new Scanner(System.in);
	}
	public void login() {

		System.out.println("Enter e-mail address: ");
		String emailAddress = scanner.nextLine();

		System.out.println("Enter your password: ");
		String userPassword = scanner.nextLine();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();

			// Check if there is a user with the provided email and password
			String query = "SELECT * FROM user WHERE user_email = '" + emailAddress + "' AND user_password = '" + userPassword + "'";
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

	public void register() {
		System.out.println("Enter your first name: ");
		String firstName = scanner.nextLine();

		System.out.println("Enter your last name: ");
		String lastName = scanner.nextLine();

		System.out.println("Enter your e-mail address: ");
		String emailAddress = scanner.nextLine();

		System.out.println("Enter your password: ");
		String userPassword = scanner.nextLine();

		System.out.println("Enter your user type (e.g., buyer as B, seller as S): ");
		String userType = scanner.nextLine();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();

			// Insert the new user into the User table
			String insertQueryUser = "INSERT INTO user (user_name, user_surname, user_email, user_password, user_type) " +
					"VALUES ('" + firstName + "', '" + lastName + "', '" + emailAddress + "', '" + userPassword + "', '" + userType + "')";
			int rowsAffectedUser = statement.executeUpdate(insertQueryUser);

			if (rowsAffectedUser > 0) {

				// Insert the new user into the appropriate table based on user type
				String insertQuery;
				if ("B".equalsIgnoreCase(userType)) {
					insertQuery = "INSERT INTO buyer (user_name, user_surname, user_email) " +
							"VALUES ('" + firstName + "', '" + lastName + "', '" + emailAddress + "')";
				} else if ("S".equalsIgnoreCase(userType)) {
					insertQuery = "INSERT INTO seller (user_name, user_surname, user_email) " +
							"VALUES ('" + firstName + "', '" + lastName + "', '" + emailAddress + "')";
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
}
