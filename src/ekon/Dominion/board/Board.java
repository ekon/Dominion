package ekon.dominion.board;

import static ekon.dominion.Card.COPPER;
import static ekon.dominion.Card.DUCHY;
import static ekon.dominion.Card.ESTATE;
import static ekon.dominion.Card.GOLD;
import static ekon.dominion.Card.PROVINCE;
import static ekon.dominion.Card.SILVER;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ekon.dominion.Card;
import ekon.dominion.Cards;
import ekon.dominion.SecurityUtil;
import ekon.dominion.UIUtil;

public class Board {
  
  public static UIUtil uiUtil = UIUtil.getUIUtil();
  
  private Map<Card, Deck> decks = new HashMap<Card, Deck>();
  
  private final int numPlayers;
  private final Cards actionCards;
  private final Trash trash;
  
  private Cards initialVictoryCards;
  private Cards initialTreasureCards;
  
  public Board(Cards actionCards, int numPlayers) {
	this(actionCards, numPlayers, new Trash());
  }

  public Board(Cards actionCards, int numPlayers, Trash trash) {
	// Initialize the board cards based on the action cards picked.
	this.actionCards = actionCards;
	this.numPlayers = numPlayers;
	this.trash = trash;
	
	setInitialTreasureCards();
	setInitialVictoryCards();
	
	addInitialCards();
	
	uiUtil.tellUser("Initializing board with: ");
	uiUtil.tellUser("	ActionCards:" + actionCards.toString());
	uiUtil.tellUser("	Treasure:" + initialTreasureCards.toString());
	uiUtil.tellUser("	VictoryCards:" + initialVictoryCards.toString());
  }
  
  public boolean hasCard(Card card) {
	return decks.containsKey(card);
  }
  
  public int getCardCount(Card card) {
	return decks.get(card).currentCount();
  }
  
  public Trash trash() {
	return trash;
  }
  
  public void trash(Card card) {
	trash.add(card);
  }
  
  public void trash(Cards cards) {
	trash.add(cards);
  }
  
  public void buy(Cards cards) {
	for (Card card : cards) {
	  buy(card);
	}
  }
  
  public void buy(Card card) {
	decks.get(card).take();
  }
  
  public void buy(Card card, int numToBuy) {
	decks.get(card).take(numToBuy);
  }
  
  public Cards getAvailableCards() {
	Cards availableCards = new Cards();
	for (Card card : decks.keySet()) {
	  if (decks.get(card).currentCount() > 0) {
		availableCards.add(card);
	  }
	}
	return availableCards;
  }
  
  public Cards getAvailableCardCostingUpTo(int cost) {
	Cards availableCards = new Cards();
	for (Card card : decks.keySet()) {
	  if ((decks.get(card).currentCount() > 0) && (card.cost() <= cost)) {
		availableCards.add(card);
	  }
	}
	return availableCards;
  }
  
  public Cards getAvailableCardsCostingExactly(int cost) {
	Cards availableCards = new Cards();
	for (Deck deck : decks.values()) {
	  if (deck.isAvailable() && (deck.card().cost() == cost)) {
		availableCards.add(deck.card());
	  }
	}
	return availableCards;
  }
  
  /** @returns a list of cards that have all been used up. */
  public Cards getDepletedCards() {
	Cards depletedCards = new Cards();
	for (Deck deck : decks.values()) {
	  if (!deck.isAvailable()) {
		depletedCards.add(deck.card());
	  }
	}	
	return depletedCards;
  }
  
  private void addInitialCards() {
	addActionCards();
	addTreasureCards();
	addVictoryCards();
  }
  
  private void addTreasureCards() {
	// TODO(ekon): fix this. 20?
	addCards(initialTreasureCards, 20);
  }
  
  // @formatter:off
  private void addVictoryCards() {
	switch (numPlayers) {
	  case 2: addCards(initialVictoryCards, 8);
	  case 3:
	  case 4: addCards(initialVictoryCards, 10);
	}
  }
  // @formatter:on
  
  private void addActionCards() {
	addCards(actionCards, 10);
  }
  
  private void addCards(Cards cards, int numEach) {
	for (Card card : cards) {
	  Deck deck = new Deck(card, numEach);
	  decks.put(card, deck);
	}
  }
  
  private void setInitialVictoryCards() {
	// TODO(ekon): fix this? Does this depend on numPlayers?
	initialVictoryCards = new Cards(ESTATE, DUCHY, PROVINCE);
  }
  
  private void setInitialTreasureCards() {
	// TODO(ekon): fix this. does this depend on numPlayers?
	initialTreasureCards = new Cards(COPPER, SILVER, GOLD);
  }
  
  public void initForTesting(Cards actionCards, Cards treasureCards, Cards victoryCards) {
	SecurityUtil securityUtil = new SecurityUtil();
	securityUtil.verifyCallingClassIsTest();
	
	// this.actionCards = actionCards;
	this.initialTreasureCards = treasureCards;
	this.initialVictoryCards = victoryCards;
	
	// TODO(ekon): Finish this! not sure how i want to do this yet.
  }
  
  @Override
  public int hashCode() {
	return Arrays.hashCode(new long[] { actionCards.hashCode(), decks.hashCode(), trash.hashCode()});
  }

  @Override
  public boolean equals(Object obj) {
	if (this == obj) return true;
	if (obj == null) return false;
	if (getClass() != obj.getClass()) return false;
	Board other = (Board) obj;
	if (actionCards == null) {
	  if (other.actionCards != null) return false;
	} else if (!actionCards.equals(other.actionCards)) return false;
	if (decks == null) {
	  if (other.decks != null) return false;
	} else if (!decks.equals(other.decks)) return false;
	if (initialTreasureCards == null) {
	  if (other.initialTreasureCards != null) return false;
	} else if (!initialTreasureCards.equals(other.initialTreasureCards)) return false;
	if (initialVictoryCards == null) {
	  if (other.initialVictoryCards != null) return false;
	} else if (!initialVictoryCards.equals(other.initialVictoryCards)) return false;
	if (numPlayers != other.numPlayers) return false;
	if (trash == null) {
	  if (other.trash != null) return false;
	} else if (!trash.equals(other.trash)) return false;
	return true;
  }

  @Override
  public String toString() {
	return "Board [decks=" + decks + ", numPlayers=" + numPlayers + ", actionCards=" + actionCards + ", trash=" + trash + "]";
  }
}
