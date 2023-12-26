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
    Auction auctions;

    // Auction currentAuction;

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
                    int auctionId = searchResultSet.getInt("auction_id");

                    String resultString = itemQuantity + "x " + itemName + " for " + itemPrice + "TL and items " +
                            "belongs to auction " + auctionId;
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
            System.out.println("\nDo you want to return to the menu? (yes/no)");
            String returnOption = scanner.nextLine().toLowerCase();

            if ("yes".equals(returnOption)) {
                getActionsByUser();
            } else {
                System.out.println("Exiting the Auction System. Goodbye!");
                scanner.close();
                System.exit(0);
            }
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void joinAuction() {
        System.out.println("Please enter Auction Id: ");
		int auctionId = scanner.nextInt();
        /*
        boolean found = false;
        for (Auction auction : auctions) {
            if (auction.getAuctionId() == auctionId) {
            	currentAuction = auctions;
            	System.out.println("Watching " + auctionId +" Auction from streaming service!");
                found = true;
            }
        }

        if (!found) {
            System.out.println("\nNo auction found.");
        }
         */

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String searchQueryForItems = "SELECT *  FROM items WHERE auction_id = '" + auctionId + "'";
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
        System.out.println("Please enter the name of the item you want to bid on:");
        String itemName = scanner.nextLine();

        // Validate that the item name is not empty
        while (itemName.trim().isEmpty()) {
            System.out.println("Item name cannot be empty. Please enter a valid item name:");
            itemName = scanner.nextLine();
        }

        System.out.println("Please enter your bid. Remember your bid needs to be higher than the base price or previous bids.");
        int newBidOnItem = scanner.nextInt();
        scanner.nextLine();  // Consume the newline character left by nextInt()

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String currentBidQuery = "SELECT bids, base_price FROM items WHERE item_name ='"+ itemName +"'";
            ResultSet resultSet = statement.executeQuery(currentBidQuery);
            if (resultSet.next()) {
                int currentBids = resultSet.getInt("bids");
                int currentBasePrice = resultSet.getInt("base_price");
                // Check if the new bid is higher
                if (newBidOnItem > currentBids && newBidOnItem > currentBasePrice) {
                    String bidQueryForItems = "UPDATE items SET bids = GREATEST(bids, ?) WHERE item_name = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(bidQueryForItems)) {
                        preparedStatement.setInt(1, newBidOnItem);
                        preparedStatement.setString(2, itemName);

                        // Execute the update
                        int rowsAffectedItems = preparedStatement.executeUpdate();

                        if (rowsAffectedItems > 0) {
                            System.out.println("Bidding is successful. You can follow the auction with the Watch Auction option.");
                            getActionsByUser();
                        } else {
                            System.out.println("Bidding failed. Please try again.");
                        }
                    }
                } else {
                    System.out.println("Your bid must be higher than the current bids and base price.");
                    bidOnItem();
                }
            } else {
                System.out.println("Item not found. You are directing buyer menu.");
                getActionsByUser();
            }

            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
