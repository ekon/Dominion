package ekon.dominion;

public class Actions {
  
  private final int cards, actions, coins, buys, victoryTokens;
  private final String description;
  
  public Actions() {
	this(0, 0, 0, 0, "");
  }
  
  public Actions(String description) {
	this(0, 0, 0, 0, description);
  }
  
  public Actions(int cards, int actions, int coins, int buys) {
	this(cards, actions, coins, buys, "");
  }
  
  public Actions(int cards, int actions, int coins, int buys, String description) {
	this(cards, actions, coins, buys, 0, description);
  }
  
  public Actions(int cards, int actions, int coins, int buys, int victoryTokens) {
	this(cards, actions, coins, buys, victoryTokens, "");
  }
  
  public Actions(int cards, int actions, int coins, int buys, int victoryTokens, String description) {
	this.cards = cards;
	this.actions = actions;
	this.coins = coins;
	this.buys = buys;
	this.victoryTokens = victoryTokens;
	this.description = description;
  }
  
  public int plusCards() {
	return cards;
  }
  
  public int plusActions() {
	return actions;
  }
  
  public int plusCoins() {
	return coins;
  }
  
  public int plusBuys() {
	return buys;
  }
  
  public int plusVictoryTokens() {
	return victoryTokens;
  }
  
  public String description() {
	return description;
  }
}