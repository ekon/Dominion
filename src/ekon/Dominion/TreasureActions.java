package ekon.dominion;
public class TreasureActions extends Actions {
	
	public TreasureActions(int coins) {
		super(0, 0, coins, 0);
	}
	
	public TreasureActions(int coins, String description) {
		super(0, 0, coins, 0, description);
	}
}