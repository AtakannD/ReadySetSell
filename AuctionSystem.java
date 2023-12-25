import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuctionSystem {

    private Scanner scanner;
    private List<AuctionItem> auctionItems;

    private boolean loginStatus;


    private AuctionSystem(){
        this.scanner = new Scanner(System.in);
        this.loginStatus = false;
        //this.user = new User();
        this.auctionItems = new ArrayList<>();
    }

    public static void main(String[] args) {
        AuctionSystem auctionSystem = new AuctionSystem();
        auctionSystem.run();

    }
    private void run(){
        System.out.println("Welcome to the Auction System!");

        while (true) {
            if (loginStatus == false){
                displayMainMenu();
                int option = getValidOption();
                switch (option) {
                    case 1 -> login();
                    case 2 -> register();
                    //case 3 -> watchMezat();
                    case 0 -> {
                        System.out.println("Exiting the Auction System. Goodbye!");
                        scanner.close();
                        System.exit(0);
                }
            }// Switch Ending
            }else{
                // Burada buyer seller admin ayrılacak.
            }
        } // While Ending
    } // Method Ending

    private void displayMainMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Quit");
    }

    private void displayBuyerMenu() {
        System.out.println("\nWelcome Buyer! Choose an option:");
        System.out.println("1. Search Item");
        System.out.println("2. Join Auction");
        System.out.println("3. Watch Auction");
        System.out.println("0. Quit");
    }

    private void displaySellerMenu() {
        System.out.println("\nWelcome Seller! Choose an option:");
        System.out.println("1. Add Item");
        System.out.println("2. Remove Item");
        System.out.println("3. Watch Auction");
        System.out.println("0. Quit");
    }
    private void displayAdminMenu() {
        System.out.println("\nWelcome Admin! Choose an option:");
        System.out.println("1. Create Auction");
        System.out.println("2. Watch Auction");
        System.out.println("3. End Auction");
        System.out.println("4. Remove Items From Auction");
        System.out.println("5. Show Auction History");
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

    private void login() { //bunu int döndür. Local kaydetsin. Ona göre menü gözüksün.
        System.out.println("Enter e-mail address: ");
        String emailAddress = scanner.nextLine();
        System.out.println("Enter your password: ");
        String userPassword = scanner.nextLine();

        User user = new User(emailAddress,userPassword);
        user.login();
        loginStatus = true;
    }

    private void register() { //bunu int döndür. Local kaydetsin. Ona göre menü gözüksün.
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

        User user = new User(emailAddress,userPassword);
        user.register(firstName,lastName,userType);
        loginStatus = true;
    }

    private void searchItem(){ //BUYER

    }

    private void addItem(){ //SELLER

    }

    private void removeItem(){ //SELLER

    }

    private void createMezat(){ //ADMIN

    }

    private void watchMezat(){ //Auction
        //User user = new User();
        //user.watchMezat();
        System.out.println("Registration successful!");
    }

    private void endMezat(){ //ADMIN

    }
}
