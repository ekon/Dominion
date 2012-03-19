package ekon.dominion;

import java.util.Arrays;

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
  
  // This is only allowed for testing.
  public TurnProperties(TurnProperties tp) {
	new SecurityUtil().verifyCallingClassIsTest();
	add(tp.cards(), tp.actions(), tp.coins(), tp.buys(), tp.victoryTokens());
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
  
  public void add(Choice choice) {
	switch (choice) {
	  case PLUS_ACTION: actions++; break;
	  case PLUS_BUY: buys++; break;
	  case PLUS_CARD: cards++; break;
	  case PLUS_COIN: coins++; break;
	}
  }
  
  public void add(Choice...choices) {
	for (Choice choice : choices) {
	  add(choice);
	}
  }
  
  public void addFromPreviousTurn(TurnProperties previousTP) {
	  add(previousTP.cards, previousTP.actions, previousTP.coins, previousTP.buys, previousTP.victoryTokens);
  }

  public void useCards() { cards = 0; } // TODO(ekon): Do I actually need this one becaues all cards get picked up at once?
  public void useAction() { actions--; }
  public void useCoins(int numCoins) { coins -= numCoins; }
  public void useBuy() { buys--; }
  
  public int cards() { return cards; }
  public int actions() { return actions; }
  public int coins() { return coins; }
  public int buys() { return buys; }
  public int victoryTokens() { return victoryTokens; }

  @Override
  public String toString() {
	return "TurnProperties [cards=" + cards + ", actions=" + actions + ", coins=" + coins + ", buys=" + buys + ", victoryTokens=" + victoryTokens + "]";
  }

  @Override
  public int hashCode() {
	return Arrays.hashCode(new Object[] { cards, actions, coins, buys, victoryTokens });
  }

  @Override
  public boolean equals(Object obj) {
	if (this == obj) return true;
	if (obj == null) return false;
	if (getClass() != obj.getClass()) return false;
	TurnProperties other = (TurnProperties) obj;
	if (actions != other.actions) return false;
	if (buys != other.buys) return false;
	if (cards != other.cards) return false;
	if (coins != other.coins) return false;
	if (victoryTokens != other.victoryTokens) return false;
	return true;
  }
}
