package ekon.dominion;

import java.util.Stack;

import ekon.dominion.board.Board;

import junit.framework.Assert;

public class TestPlayer extends Player {
  
  private static final Cards INITIAL_HAND = new Cards(Card.COPPER, Card.ESTATE, Card.COPPER, Card.COPPER, Card.COPPER);
  
  private Cards handCards, deckCards, discardCards;
  private Cards expectedHand, expectedDeck, expectedDiscard;
  
  public TestPlayer(String name) {
	this(name, new Cards(), new Cards(), new Cards(), new Cards(), new Cards(), new Cards());
  }
  
  public TestPlayer(String name, Cards handCards, Cards deckCards, Cards discardCards,
	  Cards expectedHand, Cards expectedDeck, Cards expectedDiscard) {
	super(name);
	this.handCards = handCards;
	this.deckCards = deckCards;
	this.discardCards = discardCards;
	
	this.expectedHand = expectedHand;
	this.expectedDeck = expectedDeck;
	this.expectedDiscard = expectedDiscard;
  }
  
  @SuppressWarnings("unchecked")
  public void init(Players opponents, Board board) {
	Stack<Card> deckStack = new Stack<Card>();
	deckStack.addAll(deckCards.asList());
	
	/** Initialize to {@link #INITIAL_HAND} for now & user {@link Hand#initForTesting} to setUp Hand. */
	Hand hand = new Hand(INITIAL_HAND);
	hand.initForTesting(handCards);
	
	initPlayer(opponents, board);
	initForTesting(hand, (Stack<Card>) deckStack.clone(), discardCards);
  }
  
  public void verify() {
	Stack<Card> deckStack = new Stack<Card>();
	deckStack.addAll(expectedDeck.asList());
	
	// TODO(ekon): Don't care about order. May want to though.
	Assert.assertEquals(expectedHand, hand().availableCards());
	Assert.assertTrue(deckStack.containsAll(deck())); // Don't care about order.
	Assert.assertEquals(expectedDiscard, discard());
  }
  
  public static class PlayerBuilder {
	
	private Cards handCards, deckCards, discardCards;
	private Cards expectedHand, expectedDeck, expectedDiscard;
	
	public TestPlayer build(String name) {
	  return new TestPlayer(name, handCards, deckCards, discardCards, expectedHand, expectedDeck, expectedDiscard);
	}
	
	public PlayerBuilder setHand(Cards initial, Cards expected) {
	  this.handCards = new Cards(initial);
	  this.expectedHand = expected;
	  return this;
	}
	
	public PlayerBuilder setDeck(Cards initial, Cards expected) {
	  this.deckCards = new Cards(initial);
	  this.expectedDeck = expected;
	  return this;
	}
	
	public PlayerBuilder setDiscard(Cards initial, Cards expected) {
	  this.discardCards = new Cards(initial);
	  this.expectedDiscard = expected;
	  return this;
	}
  }
}
