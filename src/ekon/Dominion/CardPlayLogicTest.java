package ekon.dominion;

import static ekon.dominion.Card.*;

import java.io.StringReader;

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
	
	private Cards actionCards = new Cards();

	private Trash trash, expectedTrash;

	@Test
	public void testCellar() {
		// This never changes for a CELLAR, so initializing for all tests.
		trash = new Trash(CHANCELLOR);
		expectedTrash = trash;

		// Verify discarding one card.
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( ESTATE, COPPER, PROVINCE ))
			.setDeck(	new Cards( PROVINCE ),
						new Cards())
			.setDiscard(new Cards(),
						new Cards( COPPER ))
			.build("P1");
		runSimple("COPPER", CELLAR);
		
		// Verify that one card is removed & if there is no deck/discard, it is picked back up.	
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( ESTATE, COPPER, COPPER ))
		    .setDeck(	new Cards(),
		    			new Cards())
		    .setDiscard(new Cards(),
		    			new Cards())
		    .build("P1");
		runSimple("COPPER", CELLAR);
		
		// Verify that multiple cards are removed and replaced.		
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( COPPER, DUCHY, PROVINCE ))
		    .setDeck(	new Cards( PROVINCE, DUCHY ),
		    			new Cards())
		    .setDiscard(new Cards(),
		    			new Cards( COPPER, ESTATE ))
		    .build("P1");
		runSimple("COPPER,ESTATE", CELLAR);
		
		// Verify that entire deck is removed and replaced.	
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( FEAST, DUKE, CURSE))
			.setDeck(	new Cards( MILITIA, CURSE, DUKE, FEAST ),
						new Cards( MILITIA))
		    .setDiscard(new Cards( PROVINCE ),
		    			new Cards( PROVINCE, COPPER, ESTATE, COPPER))
		    .build("P1");
		runSimple("COPPER,ESTATE,COPPER", CELLAR);
		
		// Verify DONE.
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( COPPER, ESTATE, COPPER ))
		    .setDeck(	new Cards( MILITIA ),
		    			new Cards( MILITIA ))
		    .setDiscard(new Cards( PROVINCE ),
		    			new Cards( PROVINCE ))
		    .build("P1");
		runSimple("DONE", CELLAR);
		
		// Verify invalid cards, but then eventually valid cards.
		player = new PlayerBuilder()
			.setHand(	new Cards( CELLAR, COPPER, ESTATE, COPPER ),
						new Cards( ESTATE, FEAST, DUKE ))
		    .setDeck(	new Cards( MILITIA, DUKE, FEAST ),
		    			new Cards( MILITIA ))
		    .setDiscard(new Cards( PROVINCE ),
		    			new Cards( PROVINCE, COPPER, COPPER ))
		    .build("P1");
		runSimple("ESTATE,FEAST\nCOPPER,COPPER", CELLAR);
	}
	
	@Test
	public void testChapel() {
		// These never change with a CHAPEL - so initializing for all tests.
		Cards deck = new Cards( MILITIA);
		Cards discard = new Cards( PROVINCE);
		
		// Verify DONE (not trashing anything).
		trash = new Trash();
		expectedTrash = trash;
		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHAPEL, COPPER),
						new Cards( COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("DONE", CHAPEL);
		
		// Verify trashing 1 card.
		trash = new Trash();
		expectedTrash = new Trash( COPPER);
		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHAPEL, COPPER, ESTATE, COPPER),
					 	new Cards( ESTATE, COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("COPPER", CHAPEL);
		
		// Verify trashing multiple cards.
		trash = new Trash( CHANCELLOR);
		expectedTrash = new Trash( CHANCELLOR, COPPER, SILVER);
		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHAPEL, COPPER, ESTATE, COPPER, SILVER),
						new Cards( ESTATE, COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("COPPER,SILVER", CHAPEL);
		
		// Verify trashing hand.
		trash = new Trash();
		expectedTrash = new Trash( COPPER, ESTATE);
		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHAPEL, COPPER, ESTATE),
					 	new Cards())
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("COPPER,ESTATE", CHAPEL);
		
		// Verify invalid cards.
		trash = new Trash();
		expectedTrash = new Trash( ESTATE);
		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHAPEL, COPPER, ESTATE, COPPER),
						new Cards( COPPER, COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("COPPER,SILVER\nESTATE", CHAPEL); // this will only trash the estate.

		// Verify invalid cards.
		trash = new Trash();
		expectedTrash = new Trash( COPPER, ESTATE);
		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHAPEL, COPPER, ESTATE, COPPER),
						new Cards( COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(	deck, deck)
		    .build("P1");
		runSimple("COPPER,SILVER\nCOPPER,ESTATE", CHAPEL); // this will only trash both copper and estate
	}
	
	@Test
	public void testChancellor() {
		// These aren't changed by chancellor.
		Cards deck = new Cards( PROVINCE, DUCHY );
		trash = new Trash( COLONY );
		expectedTrash = trash;
		
		// YES		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHANCELLOR, COPPER, ESTATE, COPPER ),
						new Cards())
		    .setDiscard(new Cards(),
		    			new Cards( COPPER, ESTATE, COPPER ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("YES", CHANCELLOR);
		
		// NO
		player = new PlayerBuilder()
			.setHand(	new Cards( CHANCELLOR, COPPER, ESTATE, COPPER ),
					new Cards( COPPER, ESTATE, COPPER ))
		    .setDiscard(new Cards( PROVINCE ),
		    			new Cards( PROVINCE ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("NO", CHANCELLOR);
		
		// Invalid input, then YES.		
		player = new PlayerBuilder()
			.setHand(	new Cards( CHANCELLOR, COPPER, ESTATE, COPPER ),
					new Cards())
		    .setDiscard(new Cards( MILITIA ),
		    		new Cards( MILITIA, COPPER, ESTATE, COPPER ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("INVALID\nYES", CHANCELLOR);
	}
	
	@Test
	public void testWorkshop() {
		// Gain a card costing up to 4.
		Cards hand = new Cards( WORKSHOP, COPPER, ESTATE );
		Cards deck = new Cards( PROVINCE, DUCHY );
		trash = new Trash( COLONY );
		Cards expectedHand = new Cards( COPPER, ESTATE );
		expectedTrash = trash;
		
		// Set-up board. All we need is the card being bought.
		actionCards = new Cards( BEAUROCRAT, FEAST, FESTIVAL );
		
		// Buy an available card.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, BEAUROCRAT ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("BEAUROCRAT", WORKSHOP);		
		
		// Buy an unavailable (card not on the board) card & then an available one.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, BEAUROCRAT ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("MILITIA\nBEAUROCRAT", WORKSHOP);
		
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
		runSimple("FESTIVAL\nBEAUROCRAT", WORKSHOP);
	}
	
	@Test
	public void testBeaurocrat() {
		// Player gets a silver & opponents put victory, if in hand, into deck.
		StringBuilder userInput = new StringBuilder();
		opponents = new TestPlayer[6];
		trash = new Trash( COPPER );
		expectedTrash = trash;
		Cards discard = new Cards( FESTIVAL );
		
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

		run(userInput.toString(), BEAUROCRAT);
	}
	
	@Test
	public void testFeast() {
		// Gain a card costing up to 5. Trash Feast.
		Cards hand = new Cards( FEAST, COPPER, ESTATE );
		Cards expectedHand = new Cards( COPPER, ESTATE );
		Cards deck = new Cards( PROVINCE, DUCHY );
		trash = new Trash( COLONY );
		expectedTrash = new Trash ( COLONY, FEAST );
		
		// Set-up board. All we need is the card being bought.
		actionCards = new Cards( FESTIVAL, COUNCIL_ROOM, GOLD );
		
		// Buy an available card at exact cost.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, FESTIVAL ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("FESTIVAL", FEAST);		
		
		// Buy an unavailable (card not on the board) card & then an available one.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, FESTIVAL ))
		    .setDeck(deck, deck)
		    .build("P1");
		runSimple("MILITIA\nFESTIVAL", FEAST);
		
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
		runSimple("GOLD\nFESTIVAL", FEAST);
	}
	
	@Test
	public void testMoneylander() {
	  Cards discard = new Cards ( PROVINCE );
	  Cards deck = new Cards ( COLONY );
	  
	  // Verify trashing copper gives player 3 extra coins.
	  player = new PlayerBuilder()
		.setHand(new Cards( MILITIA, COPPER, COPPER ),
				 new Cards( MILITIA, COPPER ))
	    .setDiscard(discard, discard)
	    .setDeck(deck, deck)
	    .build("P1");

	  trash = new Trash( COLONY );
	  expectedTrash = new Trash( COLONY, COPPER );
	  runSimple("MONEYLANDER", MONEYLANDER);
	  
	  // Verify a hand with no copper.
	}
	
	@Test
	public void testRemodel() {
		Cards deck = new Cards( PROVINCE, DUCHY );
		trash = new Trash( COLONY );
		expectedTrash = new Trash( COLONY, COPPER );
		
		// Set-up board. All we need is the card being bought.
		actionCards = new Cards( MOAT, CELLAR );
		
		// Verify remodeling a card works.
		player = recreateRemodelPlayer(deck);
		runSimple("COPPER\nMOAT", REMODEL);
		
		// Try remodeling a card that's not in the hand, and then one that is.
		player = recreateRemodelPlayer(deck);
		runSimple("MILITIA\nCOPPER\nMOAT", REMODEL);
		
		// Try remodeling to a card that's not on the board, and then one that is.
		player = recreateRemodelPlayer(deck);
		runSimple("COPPER\nCHAPEL\nMOAT", REMODEL);
		
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
	
	private TestPlayer recreateRemodelPlayer(Cards deck) {
		return new PlayerBuilder()
			.setHand( 	new Cards( REMODEL, COPPER ),
						new Cards( ))
		    .setDiscard(new Cards( MILITIA ),
		    			new Cards( MILITIA, MOAT ))
		    .setDeck(deck, deck)
		    .build("P1");
	}
	
	private void runSimple(String userInput, Card cardToPlay) {
		opponents = new TestPlayer[] { new TestPlayer("P2"), new TestPlayer("P3") };
		run(userInput, cardToPlay);
	}
	
	private void run(String userInput, Card cardToPlay) {
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
