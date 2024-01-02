import java.util.List;
import java.util.ArrayList;

public class AuctionItem {
	int auctionId;
	String itemName;
	int itemQuantity;
	public List<Integer> bids;
	
	public AuctionItem(Integer auctionId, String itemName, Integer itemQuantity) {
		this.auctionId = auctionId;
		this.itemName = itemName;
		this.itemQuantity = itemQuantity;
		bids = new ArrayList();
	}
}