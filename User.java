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
	private String currentUserType;

	private Seller seller;
	private Buyer buyer;
	private Admin admin;

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
			String query = "SELECT * FROM user WHERE user_email = '" + userEmail + "' AND user_password = '" + userPassword + "'";
			ResultSet resultSet = statement.executeQuery(query);

			if (resultSet.next()) {
				// User is authenticated
				System.out.println("Login successful!");

			} else {
				// Authentication failed
				System.out.println("Invalid email or password. Please try again.");
			}

			// After successful login, retrieve user_type and store it
			String userTypeQuery = "SELECT user_type FROM user WHERE user_email = '" + userEmail + "'";
			ResultSet userTypeResult = statement.executeQuery(userTypeQuery);

			if (userTypeResult.next()) {
				currentUserType = userTypeResult.getString("user_type");
			} else {
				System.out.println("Error: Could not retrieve user_type for the logged-in user.");
			}

			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void register(String userFirstName, String userLastName, String userType) {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();

			// Insert the new user into the User table
			String insertQueryUser = "INSERT INTO user (user_name, user_surname, user_email, user_password, user_type) " +
					"VALUES ('" + userFirstName + "', '" + userLastName + "', '" + userEmail + "', '" + userPassword + "', '" + userType + "')";
			int rowsAffectedUser = statement.executeUpdate(insertQueryUser);

			if (rowsAffectedUser > 0) {

			} else {
				System.out.println("User registration failed. Please try again.");
			}

			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void displayMenusForDifferentUserTypes() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();

			// Use the stored currentUserType to fetch relevant menus
			String menuDisplayQuery = "SELECT * FROM user WHERE user_type = '" + currentUserType + "'";
			ResultSet resultSet = statement.executeQuery(menuDisplayQuery);

			if(resultSet.next()) {
				if("S".equalsIgnoreCase(currentUserType)) {
					seller = new Seller(userEmail, userPassword);
					seller.displaySellerMenu();
				} else if ("B".equalsIgnoreCase(currentUserType)) {
					buyer = new Buyer(userEmail, userPassword);
					buyer.displayBuyerMenu();
				} else {
					admin = new Admin(userEmail, userPassword);
					admin.displayAdminMenu();
				}
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
