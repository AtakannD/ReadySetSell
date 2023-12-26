import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
	String url = "jdbc:mysql://localhost:3306/auction_system";
	String username = "root";
	String password = "";

	String userEmail;
	String userPassword;
	private String currentUserType;

	private Seller seller;
	private Buyer buyer;
	private Admin admin;
	List<String> auctionList;


	private Scanner scanner;

	public User(String userEmail, String userPassword) {
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		scanner = new Scanner(System.in);
	}


	public void login() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();

			String query = "SELECT * FROM user WHERE user_email = '" + userEmail + "' AND user_password = '" + userPassword + "'";
			ResultSet resultSet = statement.executeQuery(query);

			if (resultSet.next()) {
				System.out.println("Login successful!");

			} else {
				System.out.println("Invalid email or password. Please try again.");
			}

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
					seller.getActionsBySeller();
				} else if ("B".equalsIgnoreCase(currentUserType)) {
					buyer = new Buyer(userEmail, userPassword);
					buyer.getActionsByUser();
				} else {
					admin = new Admin(userEmail, userPassword);
					admin.getActionsByAdmin();
				}
			}

			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public int getValidOption() {
		int option;
		while (true) {
			try {
				System.out.print("Enter option number: ");
				option = scanner.nextInt();
				scanner.nextLine();
				if (option >= 0 && option <= 9) {
					break;
				} else {
					System.out.println("Invalid option. Please enter a number between 1 and 8.");
				}
			} catch (Exception e) {
				System.out.println("Invalid input. Please enter a number.");
				scanner.nextLine();
			}
		}
		return option;
	}

	public void watchAuction() {
		System.out.println("Please enter Auction Id: ");
		int auctionId = scanner.nextInt();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, username, password);
			String searchQueryForItems = "SELECT * FROM items WHERE auction_id = ? AND timer IS NOT NULL";

			try (PreparedStatement preparedStatementItems = connection.prepareStatement(searchQueryForItems)) {

				preparedStatementItems.setInt(1, auctionId);
				ResultSet resultSetForItems = preparedStatementItems.executeQuery();

				auctionList = new ArrayList<>();

				while (resultSetForItems.next()) {
					String resultString;
					String itemName = resultSetForItems.getString("item_name");
					int itemQuantity = resultSetForItems.getInt("item_quantity");
					int itemPrice = resultSetForItems.getInt("base_price");
					int itemId = resultSetForItems.getInt("item_id");
					int bids = resultSetForItems.getInt("bids");
					int auctionTimer = resultSetForItems.getInt("timer");

					if (bids > itemPrice) {
						resultString = itemId + ". " + itemQuantity + "x " + itemName + " " + itemPrice + "TL but highest bid is " + bids + "TL and remaining time is: " + auctionTimer + " minutes.";
					} else {
						resultString = itemId + ". " + itemQuantity + "x " + itemName + " " + itemPrice + "TL and remaining time is: " + auctionTimer + " minutes.";
					}
					auctionList.add(resultString);
				}

				if (!auctionList.isEmpty()) {
					System.out.println("These items are in the auction: ");
					for (String itemName : auctionList) {
						System.out.println(itemName);
					}
				} else {
					System.out.println("No items found in the auction right now.");
				}
			}
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
