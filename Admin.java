public class Admin extends User{

    public Admin(String userEmail, String userPassword) {
        super(userEmail, userPassword);
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
}
