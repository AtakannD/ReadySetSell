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
    List<String> auctionList;

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
            case 3 -> watchAuctionAsBuyer();
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
                    int auctionId = searchResultSet.getInt("auction_id");

                    String resultString = itemQuantity + "x " + itemName + " for " + itemPrice + "TL and items " +
                            "belongs to auction " + auctionId;
                    searchList.add(resultString);
                }

                if (!searchList.isEmpty()) {
                    System.out.println("PS: If items get bid, you can not see in this option. \nSearch results:");
                    for (String itemName : searchList) {
                        System.out.println(itemName);
                    }
                } else {
                    System.out.println("No items found matching the search keyword.");
                }
            }
            returnBuyerMenu();
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void joinAuction() {

        System.out.println("Please enter Auction Id: ");
		int auctionId = scanner.nextInt();
        scanner.nextLine();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String searchQueryForItems = "SELECT *  FROM items WHERE auction_id = '" + auctionId + "' AND timer IS NOT NULL";
            ResultSet auctionListResult = statement.executeQuery(searchQueryForItems);

            auctionList = new ArrayList<>();
            while (auctionListResult.next()) {
                String resultString;
                String itemName = auctionListResult.getString("item_name");
                int itemQuantity = auctionListResult.getInt("item_quantity");
                int itemPrice = auctionListResult.getInt("base_price");
                int itemId = auctionListResult.getInt("item_id");
                int bids = auctionListResult.getInt("bids");
                if (bids > itemPrice) {
                    resultString = itemId + ". " + itemQuantity + "x " + itemName + " " + itemPrice + "TL but highest bid is " + bids + "TL";
                } else {
                    resultString = itemId + ". " + itemQuantity + "x " + itemName + " " + itemPrice + "TL";
                }
                auctionList.add(resultString);
            }

            if (!auctionList.isEmpty()) {
                System.out.println("These items are in the auction: ");
                for (String itemName : auctionList) {
                    System.out.println(itemName);
                }
                bidOnItem();
            } else {
                System.out.println("No items found in auction right now.");
            }
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void bidOnItem() {
        System.out.println("\nDo you want to exit without bid? (yes/no)");
        String returnOption = scanner.nextLine().toLowerCase();

        if ("yes".equals(returnOption)) {
            returnBuyerMenu();
        } else {
            System.out.println("Please enter the name of the item you want to bid on:");
            String itemName = scanner.nextLine();

            while (itemName.trim().isEmpty()) {
                System.out.println("Item name cannot be empty. Please enter a valid item name:");
                itemName = scanner.nextLine();
            }

            System.out.println("Please enter your bid. Remember your bid needs to be higher than the base price or previous bids.");
            int newBidOnItem = scanner.nextInt();
            scanner.nextLine();

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                String currentBidQuery = "SELECT bids, base_price, auction_id FROM items WHERE item_name ='"+ itemName +"'";
                ResultSet resultSet = statement.executeQuery(currentBidQuery);
                if (resultSet.next()) {
                    int currentBids = resultSet.getInt("bids");
                    int currentBasePrice = resultSet.getInt("base_price");
                    int auctionId = resultSet.getInt("auction_id");
                    // Check if the new bid is higher
                    if (newBidOnItem > currentBids && newBidOnItem > currentBasePrice) {
                        updateBHTable(newBidOnItem, currentBids, auctionId, itemName);
                        String bidQueryForItems = "UPDATE items SET bids = GREATEST(bids, ?) WHERE item_name = ?";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(bidQueryForItems)) {
                            preparedStatement.setInt(1, newBidOnItem);
                            preparedStatement.setString(2, itemName);

                            int rowsAffectedItems = preparedStatement.executeUpdate();

                            if (rowsAffectedItems > 0) {

                                    System.out.println("Bidding is successful. You can follow the auction with the Watch Auction option.");
                                    returnBuyerMenu();
                                }
                            }

                    } else {
                        System.out.println("Your bid must be higher than the current bids and base price.");
                        bidOnItem();
                    }
                } else {
                    returnBuyerMenu();
                }
                connection.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }

    private void watchAuctionAsBuyer() {
        watchAuction();
        returnBuyerMenu();
    }

    private void updateBHTable(int newBidOnItem, int currentBids, int auctionId, String item_name) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            String userName;
            String userSurname;
            String getBuyerInfos = "SELECT user_name, user_surname FROM user WHERE user_email = '" + userEmail + "' AND user_password = '" + userPassword + "'";
            ResultSet resultSetUserInfos = statement.executeQuery(getBuyerInfos);

            if (resultSetUserInfos.next()) {
                userName = resultSetUserInfos.getString("user_name");
                userSurname = resultSetUserInfos.getString("user_surname");
                String buyerName = userName + " " + userSurname;

                String bhUpdateQuery = "UPDATE bid_history SET bidder_name = '" + buyerName + "', item_bids = " + newBidOnItem + ", item_previous_bid = " + currentBids +" WHERE  item_name = '" + item_name + "' AND auction_id = " + auctionId;
                statement.executeUpdate(bhUpdateQuery);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private void returnBuyerMenu() {
        System.out.println("\nDo you want to return to the menu? (yes/no)");
        String returnOption = scanner.nextLine().toLowerCase();

        if ("yes".equals(returnOption)) {
            getActionsByUser();
        } else {
            System.out.println("Exiting the Auction System. Goodbye!");
            scanner.close();
            System.exit(0);
        }
    }
}
