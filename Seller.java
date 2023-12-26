import java.sql.Connection;
import java.sql.DriverManager;
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
            case 3 -> watchAuction();
        }
    }

    private void addItem() {
        System.out.println("Please enter your items name: ");
        String itemName = scanner.nextLine();
        System.out.println("Please enter your items' quantity: ");
        String itemsQuantity = scanner.nextLine();
        System.out.println("Please decide your items' base price: ");
        Integer itemsBasePrice = scanner.nextInt();
        System.out.println("Please enter your items' auction number which belongs to: ");
        Integer itemsAuctionNumber = scanner.nextInt();
        Integer lowerBid = itemsBasePrice;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String itemAddingQuery = "INSERT INTO items (item_name, item_quantity, base_price, auction_id, bids)" +
                    "VALUES ('" + itemName + "', '" + itemsQuantity + "', '" + itemsBasePrice + "', '" + itemsAuctionNumber + "', '" + lowerBid + "')";
            int rowsAffertedItems = statement.executeUpdate(itemAddingQuery);

            if (rowsAffertedItems > 0) {
                System.out.println("Item addition is succesfull!");
                System.out.println("You are directing menu again.");
                getActionsBySeller();
            } else {
                System.out.println("Items addition failed. Please try again.");
            }
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
                System.out.println("You are directing menu again.");
                getActionsBySeller();
            } else {
                System.out.println("Items removing failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
