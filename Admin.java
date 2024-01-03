import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Admin extends User {

    public List<Auction> auctions;

    Scanner scanner;
    private ScheduledExecutorService scheduler;

    public Admin(String userEmail, String userPassword) {
        super(userEmail, userPassword);
        scanner = new Scanner(System.in);
        auctions = new ArrayList<>();
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void displayAdminMenu() {
        System.out.println("\nWelcome Admin! Choose an option:");
        System.out.println("1. Create Auction");
        System.out.println("2. Watch Auction");
        System.out.println("3. End Auction");
        System.out.println("4. Remove Items From Auction");
        System.out.println("5. Show Auction History");
        System.out.println("0. Quit");
    }

    public void getActionsByAdmin() {
        displayAdminMenu();
        int option = getValidOption();
        switch (option) {
            case 1 -> createAuction();
            case 2 -> watchAuctionAsAdmin();
            case 3 -> endAuction();
            case 4 -> removeItemFromAuction();
            case 5 -> showAuctionHistory();
            case 0 -> {
                System.out.println("Exiting the Auction System. Goodbye!");
                scanner.close();
                System.exit(0);
            }
        }
    }

    public void createAuction() {
        System.out.println("Please enter Auction Id: ");
        int auctionId = scanner.nextInt();
        System.out.println("Please decide capacity of auction item: ");
        int capacity = scanner.nextInt();
        System.out.println("Please set timer for auction (hour): ");
        int timer = scanner.nextInt();
        scanner.nextLine();

        Auction newAuction = new Auction(auctionId, capacity, timer);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String updateQueryUser = "UPDATE items SET item_capacity = " + capacity + ", timer = " + timer + " WHERE auction_id = " + auctionId;
            int rowsUpdated = statement.executeUpdate(updateQueryUser);

            String selectItemsQuery = "SELECT * FROM items WHERE auction_id = " + auctionId + " ORDER BY item_id LIMIT " + capacity;

            ResultSet itemsResultSet = statement.executeQuery(selectItemsQuery);

            if (rowsUpdated > 0 && itemsResultSet.next()) {
                System.out.println("Auction created successfully!");

                CountDownLatch latch = new CountDownLatch(1);

                scheduler.execute(() -> {
                    countdownTimer(timer, auctionId);
                    latch.countDown();
                });

                latch.await();

                auctions.add(newAuction);
                sendNotification(auctionId);
            } else {
                System.out.println("Creating auction failed. Please try again.");
            }
            returnAdminsMenu();
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void countdownTimer(int hours, int auctionId) {
        Runnable countdownTask = () -> {
            for (int i = hours * 60; i > 0; i--) {
                updateRemainingTime(auctionId, i);
                try {
                    TimeUnit.SECONDS.sleep(1); // Sleep for 1 second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread interrupted. Exiting countdown.");
                    return;
                }
            }
            System.out.println("Auction has ended!");
            scheduler.shutdown();
        };

        scheduler.scheduleAtFixedRate(countdownTask, 0, 1, TimeUnit.SECONDS);
    }

    private void updateRemainingTime(int auctionId, int remainingTime) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String updateQuery = "UPDATE items SET timer = '" + remainingTime + "' WHERE auction_id = '" + auctionId + "'";
            statement.executeUpdate(updateQuery);
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public synchronized void sendNotification(int auctionId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            String notificationQuery = "SELECT DISTINCT auction_id FROM items WHERE auction_id = ? AND timer IS NOT NULL";
            try (PreparedStatement preparedStatementItems = connection.prepareStatement(notificationQuery)) {

                preparedStatementItems.setInt(1, auctionId);

                ResultSet notificationSetForItems = preparedStatementItems.executeQuery();
                List<String> auctionList = new ArrayList<>();
                while (notificationSetForItems.next()) {
                    int notificationAuctionId = notificationSetForItems.getInt("auction_id");
                    String notificationString = "Auction " + notificationAuctionId + " has started.";
                    auctionList.add(notificationString);
                }
                if (!auctionList.isEmpty()) {
                    System.out.println("Currently these auctions are continuing: ");
                    for (String auctionName : auctionList) {
                        System.out.println(auctionName);
                    }
                } else {
                    System.out.println("There is no auction started yet.");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private void endAuction() {
        boolean found = false;
        System.out.println("Please enter Auction Id: ");
        int auctionId = scanner.nextInt();
        scanner.nextLine();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            String updateQueryUser = "UPDATE items SET timer = NULL WHERE auction_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQueryUser)) {
                preparedStatement.setInt(1, auctionId);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    found = true;
                    System.out.println("Deletion is success");
                } else {
                    System.out.println("Deletion process is failed.");
                }
                if (!found) {
                    System.out.println("\nNo auction found.");
                }
                returnAdminsMenu();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void removeItemFromAuction() {
        boolean found = false;
        System.out.println("Please enter Auction Id: ");
        int auctionId = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Please enter item's name which do you want to remove: ");
        String removedName = scanner.nextLine();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            String updateQueryUser = "UPDATE items SET timer = NULL WHERE auction_id = ? AND item_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQueryUser)) {
                preparedStatement.setInt(1, auctionId);
                preparedStatement.setString(2, removedName);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    found = true;
                    System.out.println("Deletion is success");
                } else {
                    System.out.println("Deletion process is failed.");
                }
                if (!found) {
                    System.out.println("\nNo auction found.");
                }
                returnAdminsMenu();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void showAuctionHistory() {
        System.out.println("Please enter Auction Id: ");
        int auctionId = scanner.nextInt();
        System.out.println("History of bids: ");
        scanner.nextLine();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            String bidHistoryQuery = "SELECT * FROM bid_history WHERE auction_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(bidHistoryQuery)) {
                preparedStatement.setInt(1, auctionId);
                ResultSet bidHistoryResultSet = preparedStatement.executeQuery();

                while (bidHistoryResultSet.next()) {
                    String sellerName = bidHistoryResultSet.getString("seller_name");
                    String itemName = bidHistoryResultSet.getString("item_name");
                    int basePrice = bidHistoryResultSet.getInt("base_price");
                    int itemBids = bidHistoryResultSet.getInt("item_bids");
                    String bidderName = bidHistoryResultSet.getString("bidder_name");
                    int itemsPreviousBid = bidHistoryResultSet.getInt("item_previous_bid");

                    System.out.println("\n" + itemName + " current values are: \nItem's seller name: " + sellerName +", \nItem's Base Price is: " + basePrice + ", \nCurrent Bid Amount: " + itemBids + ", \nBidder: " + bidderName + ", \nItem's previous bid is: " + itemsPreviousBid);
                }
                returnAdminsMenu();
            }
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void watchAuctionAsAdmin() {
        watchAuction();
        returnAdminsMenu();
    }

    private void returnAdminsMenu() {
        System.out.println("\nDo you want to return to the menu? (yes/no)");
        String returnOption = scanner.nextLine().toLowerCase();

        if ("yes".equals(returnOption)) {
            getActionsByAdmin();
        } else {
            System.out.println("Exiting the Auction System. Goodbye!");
            scanner.close();
            System.exit(0);
        }
    }
}
