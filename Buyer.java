import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Buyer extends User{

    Scanner scanner;

    String url = "jdbc:mysql://localhost:3306/auction_system";
    String username = "root";
    String password = "";
    List<String> searchList;

    public Buyer(String userEmail, String userPassword) {
        super(userEmail, userPassword);
        scanner = new Scanner(System.in);
    }

    public void displayBuyerMenu() {
        System.out.println("\nWelcome Buyer! Choose an option:");
        System.out.println("1. Search Item");
        System.out.println("2. Join Auction");
        System.out.println("3. Watch Auction");
        System.out.println("0. Quit");
    }

    public void getActionsByUser() {
        displayBuyerMenu();
        int option = getValidOption();
        switch (option) {
            case 1 -> searchItem();
            case 2 -> joinAuction();
            case 3 -> watchAuction();
            case 0 -> {
                System.out.println("Exiting the Auction System. Goodbye!");
                scanner.close();
                System.exit(0);
            }
        }
    }

    private void searchItem() {
        System.out.println("Please enter which item you looking for: ");
        String searchKeyword = scanner.nextLine();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            String searchQueryForItems = "SELECT *  FROM items WHERE item_name LIKE ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(searchQueryForItems)) {
                preparedStatement.setString(1, "%" + searchKeyword + "%");

                ResultSet searchResultSet = preparedStatement.executeQuery();

                searchList = new ArrayList<>();
                while (searchResultSet.next()) {
                    String itemName = searchResultSet.getString("item_name");
                    int itemQuantity = searchResultSet.getInt("item_quantity");
                    int itemPrice = searchResultSet.getInt("base_price");

                    String resultString = itemQuantity + "x " + itemName + " for " + itemPrice + "TL";
                    searchList.add(resultString);
                }

                if (!searchList.isEmpty()) {
                    System.out.println("Search results:");
                    for (String itemName : searchList) {
                        System.out.println(itemName);
                    }
                } else {
                    System.out.println("No items found matching the search keyword.");
                }
            }
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void joinAuction() {

    }

    private void watchAuction() {

    }
}
