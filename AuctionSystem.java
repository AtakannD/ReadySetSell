import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.io.IOException;

public class AuctionSystem {


    private Scanner scanner;
    private User user;

    private AuctionSystem(){
        this.scanner = new Scanner(System.in);
    }

    String url = "jdbc:mysql://localhost:3306/auction_system";
    String username = "root";
    String password = "";

    public static void main(String[] args) {
        AuctionSystem auctionSystem = new AuctionSystem();
        auctionSystem.run();

    }
    private void run(){
        System.out.println("Welcome to the Auction System!");

        while (true) {
            displayMenu();
            int option = getValidOption();
            switch (option) {
                case 1 -> login();
                case 2 -> register();
                case 0 -> {
                    System.out.println("Exiting the Auction System. Goodbye!");
                    scanner.close();
                    System.exit(0);
                }
            } // Switch Ending
        } // While Ending
    } // Method Ending

    private void displayMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Quit");
    }

    private int getValidOption() {
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

    private void login() {

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
                // You can add additional logic or call other methods here
            } else {
                // Authentication failed
                System.out.println("Invalid email or password. Please try again.");
            }

            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void register() {
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

        //System.out.println("Enter your user id: ");
       // String userId = scanner.nextLine();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            // Insert the new user into the database
            String insertQuery = "INSERT INTO user (user_name, user_surname, user_email, user_password, user_type) " +
                    "VALUES ('" + firstName + "', '" + lastName + "', '" + emailAddress + "', '" + userPassword + "', '" + userType + "')";
            int rowsAffected = statement.executeUpdate(insertQuery);

            if (rowsAffected > 0) {
                System.out.println("Registration successful!");
                // You can add additional logic or call other methods here
            } else {
                System.out.println("Registration failed. Please try again.");
            }

            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }


}
