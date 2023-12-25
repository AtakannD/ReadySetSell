public class Buyer extends User{

    public Buyer(String userEmail, String userPassword) {
        super(userEmail, userPassword);
    }

    public void displayBuyerMenu() {
        System.out.println("\nWelcome Buyer! Choose an option:");
        System.out.println("1. Search Item");
        System.out.println("2. Join Auction");
        System.out.println("3. Watch Auction");
        System.out.println("0. Quit");
    }
}
