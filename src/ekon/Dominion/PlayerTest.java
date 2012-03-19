package ekon.dominion;

import java.util.Stack;

import junit.framework.TestCase;

import org.junit.Test;

import ekon.dominion.board.Board;

public class PlayerTest extends TestCase {
	Player player;
	Players opponents;
	
	@Override
	public void setUp() throws Exception {
	  	super.setUp();
		player = new Player("P1");
		Player opponent1 = new Player("P2");
		Player opponent2 = new Player("P3");
		opponents = new Players(opponent1, opponent2);
		
		Cards actionCards = new Cards();
		Board board = new Board(actionCards, 3);
		
		player.initPlayer(opponents, board);
		opponent1.initPlayer(new Players(player, opponent2), board);
		opponent2.initPlayer(new Players(player, opponent1), board);
	}
	
	@Test
	public void testTakeTurn() {
	  // When player takes a turn, he picks new cards from the deck, then puts them in the discard pile.
	  Hand initialHand = new Hand(new Cards(Card.COPPER, Card.ESTATE, Card.COPPER, Card.COPPER, Card.COPPER));
	  Stack<Card> initialDeck = new Stack<Card>();
	  initialDeck.add(Card.COPPER);
	  initialDeck.add(Card.COPPER);
	  initialDeck.add(Card.COPPER);
	  initialDeck.add(Card.ESTATE);
	  initialDeck.add(Card.ESTATE);
	  player.initForTesting(initialHand, initialDeck, new Cards());
	  player.takeTurn();
	  
	  // Buy SILVER
	  // TODO(ekon): make this not manual. Figure out a way to automate user-input.
	  initialHand.add(Card.SILVER);
	  
	  assertEquals(6, player.discard().size());
	  assertEquals(player.discard(), initialHand.cards()); // check without order
	  assertEquals(5, player.hand().cards().size());
	}
}
