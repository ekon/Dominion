package ekon.dominion;

// @formatter:off
public class TurnProperties {

  private int cards, actions, coins, buys, victoryTokens;
  
  public TurnProperties() {
	// Each turn starts with 1 action and 1 buy.
	cards = 0;
	actions = 1;
	coins = 0;
	buys = 1;
	victoryTokens = 0;
  }
  
  public void addCards(int numCards) { cards += numCards; }
  public void addActions(int numActions) { actions += numActions; }
  public void addCoins(int numCoins) { coins += numCoins; }
  public void addBuys(int numBuys) { buys += numBuys; }
  public void addVictoryTokens(int numVictoryTokens) { victoryTokens += numVictoryTokens; }
  
  public void add(int numCards, int numActions, int numCoins, int numBuys, int numTokens) {
	cards += numCards;
	actions += numActions;
	coins += numCoins;
	buys += numBuys;
	victoryTokens += numTokens;
  }
  
  public void addFromPreviousTurn(TurnProperties previousTP) {
	  add(previousTP.cards, previousTP.actions, previousTP.coins, previousTP.buys, previousTP.victoryTokens);
  }

  public void useCards() { cards = 0; }
  public void useAction() { actions--; }
  public void useCoins(int numCoins) { coins -= numCoins; }
  public void useBuy() { buys--; }
  
  public int cards() { return cards; }
  public int actionsLeft() { return actions; }
  public int coins() { return coins; }
  public int buys() { return buys; }
  public int victoryTokens() { return victoryTokens; }
}
