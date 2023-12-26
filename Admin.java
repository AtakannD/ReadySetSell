import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class Admin extends User{
	String url = "jdbc:mysql://localhost:3306/auction_system";
    String username = "root";
    String password = "";
    //List<String> auctionList;
    
    public List<Auction> auctions;
	
	Scanner scanner;

    public Admin(String userEmail, String userPassword) {
        super(userEmail, userPassword);
        scanner = new Scanner(System.in);
        
        auctions = new ArrayList(); //Her user tipi için bu olsun.
		
		//databasedeki auctionları bu listeye ekleme kodu gelecek.
		
        
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
            case 2 -> watchAuction();
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

	private void createAuction() {
		System.out.println("Please enter Auction Id: ");
		int auctionId = scanner.nextInt();
		System.out.println("Please decide capacity of auction item: ");
		int capacity = scanner.nextInt();
		System.out.println("Please set timer for auction(hour): ");
		int timer = scanner.nextInt();
		Auction newAuction = new Auction(auctionId,capacity,timer);
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
         
         	String insertQueryUser = "INSERT INTO auctions (auction_id, capacity, timer) " + "VALUES ('" + auctionId + "', '" + capacity + "'";
         	int rowsAffectedUser = statement.executeUpdate(insertQueryUser);
         	if (rowsAffectedUser > 0) {
         		auctions.add(newAuction);
         	} else {
         		System.out.println("Creating auction failed. Please try again.");
         		}
         	connection.close();

			
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void watchAuction() {

		
		System.out.println("Please enter Auction Id: ");
		int auctionId = scanner.nextInt();

        boolean found = false;
        for (Auction auction : auctions) {
            if (auction.getAuctionId() == auctionId) {
            	watchMezat(); //KONUŞULACAK
            	System.out.println("Watching " + auctionId +" Auction from streaming service!");
                found = true;
            }
        }

        if (!found) {
            System.out.println("\nNo auction found.");
        }

	}

	private void endAuction() {
		System.out.println("Please enter Auction Id: ");
		int auctionId = scanner.nextInt();

        boolean found = false;
        for (Auction auction : auctions) {
            if (auction.getAuctionId() == auctionId) {
            	auctions.remove(auction);
            	//databaseden kaldırma gelecek. hem de auctionItems gidecek.
            	
            	System.out.println(auctionId +" Auction ended!");
                found = true;
            }
        }

        if (!found) {
            System.out.println("\nNo auction found.");
        }
		
	}

	private void removeItemFromAuction() {
		System.out.println("Please enter Auction Id: ");
		int auctionId = scanner.nextInt();

        boolean found = false;
        for (Auction auction : auctions) {
            if (auction.getAuctionId() == auctionId) {
   
            	//databaseden auctionItem silinecek.
            	
            	System.out.println(auctionId +" Auction history: ");
                found = true;
            }
        }

        if (!found) {
            System.out.println("\nNo auction found.");
        }

	}

	private void showAuctionHistory() {
		System.out.println("Please enter Auction Id: ");
		int auctionId = scanner.nextInt();

        boolean found = false;
        for (Auction auction : auctions) {
            if (auction.getAuctionId() == auctionId) {
   
            	//databaseden bid history görme gelecek.
            	
            	System.out.println(auctionId +" Auction history: ");
                found = true;
            }
        }

        if (!found) {
            System.out.println("\nNo auction found.");
        }

	}


}
