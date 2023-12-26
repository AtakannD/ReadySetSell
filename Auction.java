import java.util.List;
import java.util.ArrayList;

public class Auction {
    
    int auctionId;
    int capacity = 10;
    int timer = 60;
    public List<AuctionItem> auctionItems;
    // itemlist
    
    public Auction(int auctionId, int capacity, int timer) {
    	this.auctionId = auctionId;
    	this.capacity = capacity;
    	this.timer = timer;
    	
    	auctionItems = new ArrayList();
    	
    	// databaseden auction bilgileri çekilecek.?
    }
    
    public int getAuctionId() {
    	return auctionId;
    }
    
	
	public void addItemToAuction() { //seller işi
		
	}
	
	public void removeItemFromAuction() { //Admin işi
		
	}

}
