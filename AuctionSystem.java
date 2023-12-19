import java.util.Scanner;
import java.io.IOException;

public class AuctionSystem {
    private Scanner scanner;

    private AuctionSystem(){
        this.scanner = new Scanner(System.in);
    }

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
                case 1:
                    login();
                    break;
                case 0:
                    System.out.println("Exiting the Auction System. Goodbye!");
                    scanner.close();
                    System.exit(0);
            } // Switch Ending
        } // While Ending
    } // Method Ending

    private void displayMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Login");
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

    private void login(){
        System.out.println("Login Page.");
    }



}
