package ekon.dominion;

import static ekon.dominion.Card.*;

import java.io.StringReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import ekon.dominion.TestPlayer.PlayerBuilder;
import ekon.dominion.board.Board;
import ekon.dominion.board.Trash;

//@formatter:off
public class CardPlayLogicTest extends TestCase {
  
	private Board board;
	private TestPlayer player;
	private TestPlayer[] opponents;
	private Card cardToPlay;
	
	private Cards actionCards = new Cards();

	private Trash trash, expectedTrash;

	@Test
	public void testCellar() {
		// This never changes for a CELLAR, so initializing for all tests.
		trash = new Trash(CHANCELLOR);
		expectedTrash = trash;
		cardToPlay = CELLAR;

		// Verify discarding one card.
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( ESTATE, COPPER, PROVINCE ))
			.setDeck(	new Cards( PROVINCE ),
						new Cards())
			.setDiscard(new Cards(),
						new Cards( COPPER ))
			.build("P1");
		runSimple("COPPER");
		
		// Verify that one card is removed & if there is no deck/discard, it is picked back up.	
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( ESTATE, COPPER, COPPER ))
		    .setDeck(	new Cards(),
		    			new Cards())
		    .setDiscard(new Cards(),
		    			new Cards())
		    .build("P1");
		runSimple("COPPER");
		
		// Verify that multiple cards are removed and replaced.		
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( COPPER, DUCHY, PROVINCE ))
		    .setDeck(	new Cards( PROVINCE, DUCHY ),
		    			new Cards())
		    .setDiscard(new Cards(),
		    			new Cards( COPPER, ESTATE ))
		    .build("P1");
		runSimple("COPPER,ESTATE");
		
		// Verify that entire deck is removed and replaced.	
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( FEAST, DUKE, CURSE))
			.setDeck(	new Cards( MILITIA, CURSE, DUKE, FEAST ),
						new Cards( MILITIA))
		    .setDiscard(new Cards( PROVINCE ),
		    			new Cards( PROVINCE, COPPER, ESTATE, COPPER))
		    .build("P1");
		runSimple("COPPER,ESTATE,COPPER");
		
		// Verify DONE.
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( COPPER, ESTATE, COPPER ))
		    .setDeck(	new Cards( MILITIA ),
		    			new Cards( MILITIA ))
		    .setDiscard(new Cards( PROVINCE ),
		    			new Cards( PROVINCE ))
		    .build("P1");
		runSimple("DONE");
		
		// Verify invalid cards, but then eventually valid cards.
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( ESTATE, FEAST, DUKE ))
		    .setDeck(	new Cards( MILITIA, DUKE, FEAST ),
		    			new Cards( MILITIA ))
		    .setDiscard(new Cards( PROVINCE ),
		    			new Cards( PROVINCE, COPPER, COPPER ))
		    .build("P1");
		runSimple("ESTATE,FEAST\nCOPPER,COPPER");
	}
	
	@Test
	public void testChapel() {
		// These never change with a CHAPEL - so initializing for all tests.
		Cards deck = new Cards( MILITIA);
		Cards discard = new Cards( PROVINCE);
		cardToPlay = CHAPEL;
		
		// Verify DONE (not trashing anything).
		trash = new Trash();
		expectedTrash = trash;
		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHAPEL, COPPER),
						new Cards( COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("DONE");
		
		// Verify trashing 1 card.
		trash = new Trash();
		expectedTrash = new Trash( COPPER);
		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHAPEL, COPPER, ESTATE, COPPER),
					 	new Cards( ESTATE, COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("COPPER");
		
		// Verify trashing multiple cards.
		trash = new Trash( CHANCELLOR);
		expectedTrash = new Trash( CHANCELLOR, COPPER, SILVER);
		
		player = new PlayerBuilder()
			.setHand(new Cards( CHAPEL, COPPER, ESTATE, COPPER, SILVER),
					 new Cards( ESTATE, COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("COPPER,SILVER");
		
		// Verify trashing hand.
		trash = new Trash();
		expectedTrash = new Trash( COPPER, ESTATE);
		
		player = new PlayerBuilder()
			.setHand(new Cards( CHAPEL, COPPER, ESTATE),
					 new Cards())
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("COPPER,ESTATE");
		
		// Verify invalid cards.
		trash = new Trash();
		expectedTrash = new Trash( ESTATE);
		
		player = new PlayerBuilder()
			.setHand(new Cards( CHAPEL, COPPER, ESTATE, COPPER),
					 new Cards( COPPER, COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("COPPER,SILVER\nESTATE"); // this will only trash the estate.

		// Verify invalid cards.
		trash = new Trash();
		expectedTrash = new Trash( COPPER, ESTATE);
		
		player = new PlayerBuilder()
			.setHand(new Cards( CHAPEL, COPPER, ESTATE, COPPER),
					 new Cards( COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("COPPER,SILVER\nCOPPER,ESTATE"); // this will only trash both copper and estate
	}
	
	@Test
	public void testChancellor() {
		// These aren't changed by chancellor.
		Cards deck = new Cards( PROVINCE, DUCHY );
		trash = new Trash( COLONY );
		expectedTrash = trash;
		cardToPlay = CHANCELLOR;
		
		// YES		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHANCELLOR, COPPER, ESTATE, COPPER ),
					 	new Cards())
		    .setDiscard(new Cards(),
		    			new Cards( COPPER, ESTATE, COPPER ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("YES");
		
		// NO
		player = new PlayerBuilder()
			.setHand(	new Cards( CHANCELLOR, COPPER, ESTATE, COPPER ),
					 	new Cards( COPPER, ESTATE, COPPER ))
		    .setDiscard(new Cards( PROVINCE ),
		    			new Cards( PROVINCE ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("NO");
		
		// Invalid input, then YES.		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHANCELLOR, COPPER, ESTATE, COPPER ),
						new Cards())
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, COPPER, ESTATE, COPPER ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("INVALID\nYES");
	}
	
	@Test
	public void testWorkshop() {
		// Gain a card costing up to 4.
		Cards hand = new Cards( WORKSHOP, COPPER, ESTATE );
		Cards deck = new Cards( PROVINCE, DUCHY );
		trash = new Trash( COLONY );
		Cards expectedHand = new Cards( COPPER, ESTATE );
		expectedTrash = trash;
		cardToPlay = WORKSHOP;
		
		// Set-up board. All we need is the card being bought.
		actionCards = new Cards( BEAUROCRAT, FEAST, FESTIVAL );
		
		// Buy an available card.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, BEAUROCRAT ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("BEAUROCRAT");		
		
		// Buy an unavailable (card not on the board) card & then an available one.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, BEAUROCRAT ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("MILITIA\nBEAUROCRAT");
		
		// Buy an unavailable (depleted card stack) card & then an available one.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, BEAUROCRAT ))
		    .setDeck(deck, deck)
		    .build("P1");
		opponents = new TestPlayer[] { new TestPlayer("P2"), new TestPlayer("P3") };
		setUp("FEAST\nBEAUROCRAT");
		for (int i=0; i<10; i++) {
			board.buy(FEAST);
		}
		assertEquals(0, board.getCardCount(FEAST));
		CardUtil.playCard(WORKSHOP, player, board);
		verify();
		
		// Try to buy a card that's more expensive than 4, then buy one that is cheaper.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, BEAUROCRAT ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("FESTIVAL\nBEAUROCRAT");
	}
	
	@Test
	public void testBeaurocrat() {
		// Player gets a silver & opponents put victory, if in hand, into deck.
		StringBuilder userInput = new StringBuilder();
		opponents = new TestPlayer[6];
		trash = new Trash( COPPER );
		expectedTrash = trash;
		Cards discard = new Cards( FESTIVAL );
		cardToPlay = BEAUROCRAT;
		
		// Verify player gets a silver.
		player = new PlayerBuilder()
			.setHand(	new Cards( BEAUROCRAT, COPPER ),
						new Cards( COPPER ))
			.setDiscard(discard, discard)
			.setDeck(	new Cards( GOLD ),
						new Cards( GOLD, SILVER ))
			.build("P1");
		
		// No victory/reaction, does nothing.
		opponents[0] = new PlayerBuilder()
			.setHand(	new Cards( COPPER ),
						new Cards( COPPER ))
			.setDiscard(discard, discard)
			.setDeck(	new Cards( MILITIA ),
						new Cards( MILITIA ))
			.build("P2");
		
		// Victory, no reaction, puts victory on deck.
		opponents[1] = new PlayerBuilder()
			.setHand(	new Cards( COPPER, ESTATE ),
						new Cards( COPPER ))
			.setDiscard(discard, discard)
			.setDeck(	new Cards( CURSE ),
						new Cards( CURSE, ESTATE ))
			.build("P3");
		
		// Multiple victories, no reaction, puts a chosen one on deck. First input invalid.
		opponents[2] = new PlayerBuilder()
			.setHand(	new Cards( MILITIA, ESTATE, DUCHY ),
						new Cards( MILITIA, DUCHY  ))
			.setDiscard(discard, discard)
			.setDeck(	new Cards( CHAPEL ),
						new Cards( CHAPEL, ESTATE ))
			.build("P4");
		userInput.append("COPPER\nESTATE\n");
		
		// Reaction, no victory. Reveal reaction. First reaction card entry is invalid.
		opponents[3] = new PlayerBuilder()
			.setHand(	new Cards( COPPER, MOAT ), // TOOD(ekon): may want to add a more interesting reaction.
						new Cards( COPPER, MOAT ))
			.setDiscard(discard, discard)
			.setDeck(	new Cards( GARDENS ),
						new Cards( GARDENS ))
			.build("P5");
		userInput.append("YES\nCOPPER\nMOAT\n");
		
		// Reaction and victory, reveal reaction.
		opponents[4] = new PlayerBuilder()
			.setHand(	new Cards( COPPER, ESTATE, MOAT ),
						new Cards( COPPER, ESTATE, MOAT ))
			.setDiscard(discard, discard)
			.setDeck(	new Cards( GARDENS ),
						new Cards( GARDENS ))
			.build("P6");
		userInput.append("MAYBE\nYES\nMOAT\n");
		
		// Reaction and victory, don't reveal reaction.
		opponents[5] = new PlayerBuilder()
			.setHand(	new Cards( COPPER, ESTATE, MOAT ),
						new Cards( COPPER, MOAT ))
			.setDiscard(discard, discard)
			.setDeck(	new Cards( GARDENS ),
						new Cards( GARDENS, ESTATE ))
			.build("P7");
		userInput.append("NO\n");

		run(userInput.toString());
	}
	
	@Test
	public void testFeast() {
		// Gain a card costing up to 5. Trash Feast.
		Cards hand = new Cards( FEAST, COPPER, ESTATE );
		Cards expectedHand = new Cards( COPPER, ESTATE );
		Cards deck = new Cards( PROVINCE, DUCHY );
		trash = new Trash( COLONY );
		expectedTrash = new Trash ( COLONY, FEAST );
		cardToPlay = FEAST;
		
		// Set-up board. All we need is the card being bought.
		actionCards = new Cards( FESTIVAL, COUNCIL_ROOM, GOLD );
		
		// Buy an available card at exact cost.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, FESTIVAL ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("FESTIVAL");		
		
		// Buy an unavailable (card not on the board) card & then an available one.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, FESTIVAL ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("MILITIA\nFESTIVAL");
		
		// Buy an unavailable (depleted card stack) card & then an available one.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, FESTIVAL ))
		    .setDeck(deck, deck)
		    .build("P1");
		opponents = new TestPlayer[] { new TestPlayer("P2"), new TestPlayer("P3") };
		setUp("COUNCIL ROOM\nFESTIVAL");
		for (int i=0; i<10; i++) {
			board.buy(COUNCIL_ROOM);
		}
		assertEquals(0, board.getCardCount(COUNCIL_ROOM));
		CardUtil.playCard(FEAST, player, board);
		verify();
		
		// Try to buy a card that's more expensive than 4, then buy one that is cheaper.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, FESTIVAL ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("GOLD\nFESTIVAL");
	}
	
	@Test
	public void testMoneylander() {
	  Cards discard = new Cards ( PROVINCE );
	  Cards deck = new Cards ( COLONY );
	  cardToPlay = MONEYLANDER;
	  
	  // Verify trashing copper gives player 3 extra coins.
	  player = new PlayerBuilder()
		.setHand(new Cards( MONEYLANDER, MILITIA, COPPER, COPPER ),
				 new Cards( MILITIA, COPPER ))
	    .setDiscard(discard, discard)
	    .setDeck(deck, deck)
	    .build("P1");

	  trash = new Trash( COLONY );
	  expectedTrash = new Trash( COLONY, COPPER );
	  runSimple("MONEYLANDER");
	  
	  // Verify a hand with no copper.
	  player = new PlayerBuilder()
		.setHand(new Cards( MONEYLANDER, MILITIA ),
				 new Cards( MILITIA ))
	    .setDiscard(discard, discard)
	    .setDeck(deck, deck)
	    .build("P1");

	  trash = new Trash( COLONY );
	  expectedTrash = trash;
	  runSimple("MONEYLANDER");	  
	}
	
	@Test
	public void testRemodel() {
		Cards deck = new Cards( PROVINCE, DUCHY );
		trash = new Trash( COLONY );
		expectedTrash = new Trash( COLONY, COPPER );
		cardToPlay = REMODEL;
		
		// Set-up board. All we need is the card being bought.
		actionCards = new Cards( MOAT, CELLAR );
		
		// Verify remodeling a card works.
		player = recreateRemodelPlayer(deck);
		runSimple("COPPER\nMOAT");
		
		// Try remodeling a card that's not in the hand, and then one that is.
		player = recreateRemodelPlayer(deck);
		runSimple("MILITIA\nCOPPER\nMOAT");
		
		// Try remodeling to a card that's not on the board, and then one that is.
		player = recreateRemodelPlayer(deck);
		runSimple("COPPER\nCHAPEL\nMOAT");
		
		// Try remodeling to a card that's depleted, and then a valid ones.
		player = recreateRemodelPlayer(deck);
		opponents = new TestPlayer[] { new TestPlayer("P2"), new TestPlayer("P3") };
		setUp("COPPER\nCELLAR\nMOAT");
		for (int i=0; i<10; i++) {
			board.buy(CELLAR);
		}
		assertEquals(0, board.getCardCount(CELLAR));
		CardUtil.playCard(REMODEL, player, board);
		verify();
	}
	
	@Test
	public void testSpy() {
	  // Each player (including you) reveals the top card of his deck and either discards it or puts it back, your choice.
	  trash = new Trash(COLONY);
	  expectedTrash = trash;
	  opponents = new TestPlayer[3];
	  StringBuilder userInput = new StringBuilder();
	  cardToPlay = SPY;
	  
	  // Player discards card.
	  player = new PlayerBuilder()
		.setHand(	new Cards( SPY, ESTATE ),
					new Cards( ESTATE ))
	    .setDeck(	new Cards( ESTATE ),
					new Cards())
	    .setDiscard(new Cards( COPPER ),
	    			new Cards( COPPER, ESTATE ))
	    .build("P1");
	  userInput.append("YES\n");
	  
	  // Opponent reveals reaction. Nothing happens.
	  opponents[0] = new PlayerBuilder()
		.setHand(	new Cards( MILITIA, MOAT),
					new Cards( MILITIA, MOAT))
		.setDeck(	new Cards( ESTATE ),
		   			new Cards( ESTATE ))
	    .setDiscard(new Cards( COPPER, ESTATE ),
	    			new Cards( COPPER, ESTATE ))
	    .build("P2");
	  userInput.append("YES\n");
	  
	  // Opponent decides to not reveal reaction. Player chooses to put card back on deck.
	  opponents[1] = new PlayerBuilder()
		.setHand(	new Cards( MILITIA, MOAT ),
					new Cards( MILITIA, MOAT))
		.setDeck(	new Cards( ESTATE ),
		    		new Cards( ESTATE ))
	    .setDiscard(new Cards( COPPER ),
	    			new Cards( COPPER ))
	    .build("P3");
	  userInput.append("NO\nNO\n");
	  
	  // Opponent does not have reaction to reveal. Player chooses to put card in discard.
	  opponents[2] = new PlayerBuilder()
		.setHand(	new Cards( MILITIA, COPPER ),
					new Cards( MILITIA, COPPER ))
		.setDeck(	new Cards( ESTATE, GOLD ),
		   			new Cards( ESTATE ))
	    .setDiscard(new Cards( COPPER ),
	    			new Cards( COPPER, GOLD ))
	    .build("P4");
	  userInput.append("YES\n");

	  run(userInput.toString());
	  
	  // Verify player putting card back on deck.
	  player = new PlayerBuilder()
		.setHand(	new Cards( SPY, ESTATE ),
					new Cards( ESTATE))
		.setDeck(	new Cards( ESTATE ),
		    		new Cards( ESTATE ))
	    .setDiscard(new Cards( COPPER ),
	    			new Cards( COPPER ))
	    .build("P1");
	  userInput = new StringBuilder("NO\n");
	  
	  // Verify that opponent with no more cards left on deck, has the cards from his discard reshuffled. Player chooses to keep card on deck.
	  opponents = new TestPlayer[1];
	  opponents[0] = new PlayerBuilder()
		.setHand(	new Cards( ESTATE ),
					new Cards( ESTATE))
		.setDeck(	new Cards(),
		    		new Cards( CURSE, COPPER ))
	    .setDiscard(new Cards( CURSE, COPPER ),
	    			new Cards( ))
	    .build("P2");
	  userInput.append("NO\n");
	  
	  run(userInput.toString());
	}
	
	@Test
	public void testThief() {
	  // Each other player reveals the top 2 cards of his deck. If they revealed any Treasure cards, they trash one of them that you choose.
	  // You may gain any or all of these trashed cards. They discard the other revealed cards.

	  Cards deck = new Cards( COPPERSMITH );
	  opponents = new TestPlayer[4];
	  StringBuilder userInput = new StringBuilder();
	  cardToPlay = THIEF;
	  
	  // Opponent doesn't have Treasure cards & has to pick up from Discard.
	  opponents[0] = new PlayerBuilder()
		.setHand(	new Cards( MOAT),
					new Cards( MOAT))
		.setDeck(	new Cards( MILITIA ),
		   			new Cards( COPPER, MILITIA ))
	    .setDiscard(new Cards( ESTATE, PROVINCE ),
	    			new Cards())
	    .build("P1");
	  
	  // Opponent reveals reaction. Nothing happens.
	  opponents[0] = new PlayerBuilder()
		.setHand(	new Cards( MOAT),
					new Cards( MOAT))
		.setDeck(	new Cards( COPPER, MILITIA ),
		   			new Cards( COPPER, MILITIA ))
	    .setDiscard(new Cards( DUCHY ),
	    			new Cards( DUCHY ))
	    .build("P2");
	  userInput.append("YES\n");
	  
	  // Opponent decides to not reveal reaction. Player chooses to trash card.
	  opponents[1] = new PlayerBuilder()
		.setHand(	new Cards( MOAT ),
					new Cards( MOAT))
		.setDeck(	new Cards( COPPER, MILITIA ),
		    		new Cards( ))
	    .setDiscard(new Cards( DUCHY ),
	    			new Cards( DUCHY, MILITIA ))
	    .build("P3");
	  userInput.append("NO\nTRASH\n");
	  
	  // Opponent does not have reaction to reveal. Player chooses to take his treasure.
	  opponents[2] = new PlayerBuilder()
		.setHand(	new Cards( FEAST ),
					new Cards( FEAST ))
		.setDeck(	new Cards( GOLD, MILITIA ),
		   			new Cards( MILITIA ))
	    .setDiscard(new Cards( DUCHY ),
	    			new Cards( DUCHY, MILITIA ))
	    .build("P4");
	  userInput.append("TAKE\n");
	  
	  // Opponent has 2 treasures. Player choose to take one of them.
	  opponents[3] = new PlayerBuilder()
		.setHand(	new Cards( FEAST ),
					new Cards( FEAST ))
		.setDeck(	new Cards( SILVER, COPPER ),
		   			new Cards( MILITIA ))
	    .setDiscard(new Cards( DUCHY ),
	    			new Cards( DUCHY, COPPER ))
	    .build("P5");
	  userInput.append("SILVER\nTAKE\n");
	  
	  // Player gains opponent's Treasure.
	  player = new PlayerBuilder()
		.setHand(	new Cards( THIEF, ESTATE ),
					new Cards( ESTATE ))
	    .setDeck(deck, deck)
	    .setDiscard(new Cards( ESTATE ),
	    			new Cards( ESTATE, GOLD, SILVER ))
	    .build("P6");

	  trash = new Trash(COLONY);
	  expectedTrash = new Trash(COLONY, COPPER);
	  run(userInput.toString());
	}
	
	private TestPlayer recreateRemodelPlayer(Cards deck) {
		return new PlayerBuilder()
			.setHand( 	new Cards( REMODEL, COPPER ),
						new Cards( ))
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, MOAT ))
		    .setDeck(deck, deck)
		    .build("P1");
	}
	
	private void runSimple(String userInput) {
		opponents = new TestPlayer[] { new TestPlayer("P2"), new TestPlayer("P3") };
		run(userInput);
	}
	
	private void run(String userInput) {
	  setUp(userInput);
	  CardUtil.playCard(cardToPlay, player, board);
	  verify();
	}
	
	private void setUp(String userInput) {
		setUpBoard();
		setUpPlayers();
		UIUtil.setUpForTest(new StringReader(userInput));
	}
	
	private void setUpPlayers() {
		player.init(new Players(opponents), board);
		
		// Verify that player's hand contains the card we're trying to play.
		if (!player.hand().contains(cardToPlay)) {
		  Assert.fail("TEST ERROR: Player's hand doesn't contain card to play.");
		} 
		
		for (TestPlayer opponent : opponents) {
			Players itsOpponents = new Players();
			for (Player itsOpponent : opponents) {
				// Players must have unique names.
				if (!opponent.equals(itsOpponent)) {
					itsOpponents.add(itsOpponent);
				}
			}
			itsOpponents.add(player);
			opponent.init(itsOpponents, board);
		}
	}
	
	private void verify() {
		player.verify();
		for (TestPlayer opponent : opponents) {
			opponent.verify();
		}
		verifyBoard();
	}
	
	private void verifyBoard() {
		assertEquals(expectedTrash, board.trash());
	}
	
	private void setUpBoard() {
		board = new Board(actionCards, 3, new Trash(trash));
	}
}
