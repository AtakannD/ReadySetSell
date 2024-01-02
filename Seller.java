import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Seller extends User{

    Scanner scanner;

    public Seller(String userEmail, String userPassword) {
        super(userEmail, userPassword);
        scanner = new Scanner(System.in);
    }

    public void displaySellerMenu() {
        System.out.println("\nWelcome Seller! Choose an option:");
        System.out.println("1. Add Item");
        System.out.println("2. Remove Item");
        System.out.println("3. Watch Auction");
        System.out.println("0. Quit");
    }

    public void getActionsBySeller() {
        displaySellerMenu();
        int option = getValidOption();
        switch (option) {
            case 1 -> addItem();
            case 2 -> removeItem();
            case 3 -> watchAuctionAsSeller();
            case 0 -> {
                System.out.println("Exiting the Auction System. Goodbye!");
                scanner.close();
                System.exit(0);
            }
        }
    }

    private void addItem() {
        System.out.println("Please enter your items name: ");
        String itemName = scanner.nextLine();
        System.out.println("Please enter your items' quantity: ");
        String itemsQuantity = scanner.nextLine();
        System.out.println("Please decide your items' base price: ");
        int itemsBasePrice = scanner.nextInt();
        System.out.println("Please enter your items' auction number which belongs to: ");
        int itemsAuctionNumber = scanner.nextInt();
        int lowerBid = itemsBasePrice;
        scanner.nextLine();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String itemAddingQuery = "INSERT INTO items (item_name, item_quantity, base_price, auction_id, bids)" +
                    "VALUES ('" + itemName + "', '" + itemsQuantity + "', '" + itemsBasePrice + "', '" + itemsAuctionNumber + "', '" + lowerBid + "')";
            int rowsAffertedItems = statement.executeUpdate(itemAddingQuery);
            String userName;
            String userSurname;

            if (rowsAffertedItems > 0) {

                String getSellerInfos = "SELECT user_name, user_surname FROM user WHERE user_email = '" + userEmail + "' AND user_password = '" + userPassword + "'";
                ResultSet resultSetUserInfos = statement.executeQuery(getSellerInfos);

                if (resultSetUserInfos.next()) {
                    userName = resultSetUserInfos.getString("user_name");
                    userSurname = resultSetUserInfos.getString("user_surname");
                    String sellerName = userName + " " + userSurname;

                    String bhInsertQuery = "INSERT INTO bid_history (seller_name, item_name, item_quantity, base_price, auction_id)" +
                            "VALUES ('" + sellerName + "','" + itemName + "', '" + itemsQuantity + "', '" + itemsBasePrice + "', '" + itemsAuctionNumber + "')";
                    int rowsAffectedBidsHistory = statement.executeUpdate(bhInsertQuery);
                    if (rowsAffectedBidsHistory > 0) {
                        System.out.println("Item addition is succesfull!");
                    }
                }
            } else {
                System.out.println("Items addition failed. Please try again.");
            }
            returnSellersMenu();
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void removeItem() {
        System.out.println("Please enter items' name which you want to remove: ");
        String itemsName = scanner.nextLine();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String itemRemovingQuery = "DELETE FROM items WHERE item_name = '" + itemsName + "'";
            int rowsAffectedItems = statement.executeUpdate(itemRemovingQuery);

            if (rowsAffectedItems > 0) {
                System.out.println("Item removing is succesfull!");

            } else {
                System.out.println("Items removing failed. Please try again.");
            }
            returnSellersMenu();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    private void watchAuctionAsSeller() {
        watchAuction();
        returnSellersMenu();
    }
    private void returnSellersMenu() {
        System.out.println("\nDo you want to return to the menu? (yes/no)");
        String returnOption = scanner.nextLine().toLowerCase();

        if ("yes".equals(returnOption)) {
            getActionsBySeller();
        } else {
            System.out.println("Exiting the Auction System. Goodbye!");
            scanner.close();
            System.exit(0);
        }
    }
}
