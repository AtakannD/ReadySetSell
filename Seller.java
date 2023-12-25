public class Seller extends User{

    public Seller(String userEmail, String userPassword) {
        super(userEmail, userPassword);
    }

    public void displaySellerMenu() {
        System.out.println("\nWelcome Seller! Choose an option:");
        System.out.println("1. Add Item");
        System.out.println("2. Remove Item");
        System.out.println("3. Watch Auction");
        System.out.println("0. Quit");
    }
}
