package ekon.dominion;

import java.util.Stack;

import static junit.framework.Assert.*;
import ekon.dominion.board.Board;

public class TestPlayer extends Player {
  
  private static final Cards INITIAL_HAND = new Cards(Card.COPPER, Card.ESTATE, Card.COPPER, Card.COPPER, Card.COPPER);
  
  private Cards handCards, deckCards, discardCards;
  private Cards expectedHand, expectedDeck, expectedDiscard;
  
  private TurnProperties tp, nextTp;
  private TurnProperties expectedTp, expectedNextTp; 
  
  public TestPlayer(String name) {
	this(name, new Cards(), new Cards(), new Cards(), new TurnProperties(), new TurnProperties(),
		new Cards(), new Cards(), new Cards(), new TurnProperties(), new TurnProperties());
  }
  
  private TestPlayer(String name, Cards handCards, Cards deckCards, Cards discardCards, TurnProperties tp, TurnProperties nextTp,
	  Cards expectedHand, Cards expectedDeck, Cards expectedDiscard, TurnProperties expectedTp, TurnProperties expectedNextTp) {
	super(name);
	this.handCards = handCards;
	this.deckCards = deckCards;
	this.discardCards = discardCards;
	this.tp = tp;
	this.nextTp = nextTp;
	
	this.expectedHand = expectedHand;
	this.expectedDeck = expectedDeck;
	this.expectedDiscard = expectedDiscard;
	this.expectedTp = expectedTp;
	this.expectedNextTp = expectedNextTp;
  }
  
  @SuppressWarnings("unchecked")
  public void init(Players opponents, Board board) {
	Stack<Card> deckStack = new Stack<Card>();
	deckStack.addAll(deckCards.asList());
	
	/** Initialize to {@link #INITIAL_HAND} for now & user {@link Hand#initForTesting} to setUp Hand. */
	Hand hand = new Hand(INITIAL_HAND);
	hand.initForTesting(handCards);
	
	initPlayer(opponents, board);
	initForTesting(hand, (Stack<Card>) deckStack.clone(), discardCards, tp, nextTp);
  }
  
  public void verify() {
	Stack<Card> deckStack = new Stack<Card>();
	deckStack.addAll(expectedDeck.asList());
	
	// TODO(ekon): Don't care about order. May want to though.
	assertEquals(expectedHand, hand().availableCards());
	assertTrue(deckStack.containsAll(deck())); // Don't care about order.
	assertEquals(expectedDiscard, discard());
	
	assertEquals(expectedTp, tp());
	assertEquals(expectedNextTp, nextTp());
  }
  
  public static class PlayerBuilder {
	
	private Cards handCards, deckCards, discardCards;
	private Cards expectedHand, expectedDeck, expectedDiscard;
	  
	private TurnProperties tp, nextTp;
	private TurnProperties expectedTp, expectedNextTp;
	
	public TestPlayer build(String name) {
	  if (expectedTp == null) { expectedTp = new TurnProperties(); }
	  if (nextTp == null) { nextTp = new TurnProperties(); }
	  
	  return new TestPlayer(name, handCards, deckCards, discardCards, tp, nextTp,
		  expectedHand, expectedDeck, expectedDiscard, expectedTp, expectedNextTp);
	}
	
	public PlayerBuilder setHand(Cards initial, Cards expected) {
	  this.handCards = new Cards(initial);
	  this.expectedHand = expected;
	  return this;
	}

	/** This is used when initial = expected. */
	public PlayerBuilder setHand(Cards cards) {
	  this.handCards = new Cards(cards);
	  this.expectedHand = new Cards(cards);
	  return this;
	}
	
	public PlayerBuilder setDeck(Cards initial, Cards expected) {
	  this.deckCards = new Cards(initial);
	  this.expectedDeck = expected;
	  return this;
	}

	/** This is used when initial = expected. */
	public PlayerBuilder setDeck(Cards cards) {
	  this.deckCards = new Cards(cards);
	  this.expectedDeck = new Cards(cards);
	  return this;
	}
	
	public PlayerBuilder setDiscard(Cards initial, Cards expected) {
	  this.discardCards = new Cards(initial);
	  this.expectedDiscard = expected;
	  return this;
	}
	
	/** This is used when initial = expected. */
	public PlayerBuilder setDiscard(Cards cards) {
	  this.discardCards = new Cards(cards);
	  this.expectedDiscard = new Cards(cards);
	  return this;
	}
	
	public PlayerBuilder setTp(TurnProperties tp, TurnProperties expectedTp) {
	  this.tp = new TurnProperties(tp);
	  this.expectedTp = new TurnProperties(expectedTp);
	  return this;
	}
	
	public PlayerBuilder setNextTp(TurnProperties nextTp, TurnProperties expectedNextTp) {
	  this.nextTp = new TurnProperties(nextTp);
	  this.expectedNextTp = new TurnProperties(expectedNextTp);
	  return this;
	}
  }
}
