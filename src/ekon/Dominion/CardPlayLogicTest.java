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
	
	// Initializing these to empty because by default don't expect TurnProperties for many cards.
	private TurnProperties tp = new TurnProperties(), nextTp = new TurnProperties();
	private TurnProperties expectedTp = new TurnProperties(), expectedNextTp = new TurnProperties();

	// TODO(ekon): Not sure if these are needed? Especially for picking up cards, this is NOT getting tracked.
	private TurnProperties opponentTp = new TurnProperties(), opponentNextTp = new TurnProperties();
	private TurnProperties opponentExpectedTp = new TurnProperties(), opponentExpectedNextTp = new TurnProperties();

	@Test
	public void testCellar() {
		// This never changes for a CELLAR, so initializing for all tests.
		trash = new Trash(CHANCELLOR);
		expectedTrash = trash;
		expectedTp.addActions(1);
		cardToPlay = CELLAR;

		// Verify discarding one card.
		player = new PlayerBuilder()
			.setHand(	new Cards(CELLAR, COPPER, ESTATE, COPPER),
						new Cards(ESTATE, COPPER, PROVINCE))
			.setDeck(	new Cards(PROVINCE),
						new Cards())
			.setDiscard(new Cards(),
						new Cards(COPPER))
			.setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
			.build("P1");
		runSimple("COPPER");
		
		// Verify that one card is removed & if there is no deck/discard, it is picked back up.	
		player = new PlayerBuilder()
			.setHand(	new Cards(CELLAR, COPPER, ESTATE, COPPER),
						new Cards(ESTATE, COPPER, COPPER))
		    .setDeck(	new Cards(),
		    			new Cards())
		    .setDiscard(new Cards(),
		    			new Cards())
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("COPPER");
		
		// Verify that multiple cards are removed and replaced.		
		player = new PlayerBuilder()
			.setHand(	new Cards(CELLAR, COPPER, ESTATE, COPPER),
						new Cards(COPPER, DUCHY, PROVINCE))
		    .setDeck(	new Cards(PROVINCE, DUCHY),
		    			new Cards())
		    .setDiscard(new Cards(),
		    			new Cards(COPPER, ESTATE))
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("COPPER,ESTATE");
		
		// Verify that entire deck is removed and replaced.	
		player = new PlayerBuilder()
			.setHand(	new Cards(CELLAR, COPPER, ESTATE, COPPER),
						new Cards(FEAST, DUKE, CURSE))
			.setDeck(	new Cards(MILITIA, CURSE, DUKE, FEAST),
						new Cards(MILITIA))
		    .setDiscard(new Cards(PROVINCE),
		    			new Cards(PROVINCE, COPPER, ESTATE, COPPER))
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("COPPER,ESTATE,COPPER");
		
		// Verify DONE.
		player = new PlayerBuilder()
			.setHand(	new Cards(CELLAR, COPPER, ESTATE, COPPER),
						new Cards(COPPER, ESTATE, COPPER))
		    .setDeck(	new Cards(MILITIA),
		    			new Cards(MILITIA))
		    .setDiscard(new Cards(PROVINCE),
		    			new Cards(PROVINCE))
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("DONE");
		
		// Verify invalid cards, but then eventually valid cards.
		player = new PlayerBuilder()
			.setHand(	new Cards(CELLAR, COPPER, ESTATE, COPPER),
						new Cards(ESTATE, FEAST, DUKE))
		    .setDeck(	new Cards(MILITIA, DUKE, FEAST),
		    			new Cards(MILITIA))
		    .setDiscard(new Cards(PROVINCE),
		    			new Cards(PROVINCE, COPPER, COPPER))
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("ESTATE,FEAST\nCOPPER,COPPER");
	}
	
	@Test
	public void testChapel() {
		// These never change with a CHAPEL - so initializing for all tests.
		Cards deck = new Cards(MILITIA);
		Cards discard = new Cards(PROVINCE);
		cardToPlay = CHAPEL;
		
		// Verify DONE (not trashing anything).
		trash = new Trash();
		expectedTrash = trash;
		
		player = new PlayerBuilder()
			.setHand(	new Cards(CHAPEL, COPPER),
						new Cards(COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("DONE");
		
		// Verify trashing 1 card.
		trash = new Trash();
		expectedTrash = new Trash( COPPER);
		
		player = new PlayerBuilder()
			.setHand(	new Cards(CHAPEL, COPPER, ESTATE, COPPER),
					 	new Cards(ESTATE, COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("COPPER");
		
		// Verify trashing multiple cards.
		trash = new Trash( CHANCELLOR);
		expectedTrash = new Trash( CHANCELLOR, COPPER, SILVER);
		
		player = new PlayerBuilder()
			.setHand(new Cards(CHAPEL, COPPER, ESTATE, COPPER, SILVER),
					 new Cards(ESTATE, COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("COPPER,SILVER");
		
		// Verify trashing hand.
		trash = new Trash();
		expectedTrash = new Trash( COPPER, ESTATE);
		
		player = new PlayerBuilder()
			.setHand(new Cards(CHAPEL, COPPER, ESTATE),
					 new Cards())
		    .setDiscard(discard,discard)
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("COPPER,ESTATE");
		
		// Verify invalid cards.
		trash = new Trash();
		expectedTrash = new Trash( ESTATE);
		
		player = new PlayerBuilder()
			.setHand(new Cards(CHAPEL, COPPER, ESTATE, COPPER),
					 new Cards(COPPER, COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("COPPER,SILVER\nESTATE"); // this will only trash the estate.

		// Verify invalid cards.
		trash = new Trash();
		expectedTrash = new Trash( COPPER, ESTATE);
		
		player = new PlayerBuilder()
			.setHand(new Cards(CHAPEL, COPPER, ESTATE, COPPER),
					 new Cards(COPPER))
		    .setDiscard(discard,discard)
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("COPPER,SILVER\nCOPPER,ESTATE"); // this will only trash both copper and estate
	}
	
	@Test
	public void testMoat() {
	  Cards discard = new Cards(MILITIA);
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  expectedTp.addCards(2);
	  cardToPlay = MOAT;
	  
	  player = new PlayerBuilder()
		.setHand(new Cards(MOAT, COPPER),
				 new Cards(COPPER, ESTATE, DUKE))
	    .setDiscard(discard,discard)
	    .setDeck(new Cards(ESTATE, DUKE),
	    		 new Cards())
	    .setTp(tp, expectedTp)
	    .build("P1");
	  runSimple();
	}
	
	@Test
	public void testCourtyard() {
	  Cards discard = new Cards(MILITIA);
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  expectedTp.addCards(3);
	  cardToPlay = COURTYARD;
	  
	  player = new PlayerBuilder()
		.setHand(new Cards(COPPER, SILVER, COURTYARD),
				 new Cards(COPPER, ESTATE, DUKE, PROVINCE))
	    .setDiscard(discard,discard)
	    .setDeck(new Cards(ESTATE, DUKE, PROVINCE),
	    		 new Cards(SILVER))
	    .setTp(tp, expectedTp)
	    .build("P1");
	  runSimple("SILVER");
	}
	
	@Test
	public void testChancellor() {
		// These aren't changed by chancellor.
		Cards deck = new Cards(PROVINCE, DUCHY);
		trash = new Trash( COLONY);
		expectedTrash = trash;
		expectedTp.addCoins(2);
		cardToPlay = CHANCELLOR;
		
		// YES		
		player = new PlayerBuilder()
			.setHand(	new Cards(CHANCELLOR, COPPER, ESTATE, COPPER),
					 	new Cards())
		    .setDiscard(new Cards(),
		    			new Cards(COPPER, ESTATE, COPPER))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("YES");
		
		// NO
		player = new PlayerBuilder()
			.setHand(	new Cards(CHANCELLOR, COPPER, ESTATE, COPPER),
					 	new Cards(COPPER, ESTATE, COPPER))
		    .setDiscard(new Cards(PROVINCE),
		    			new Cards(PROVINCE))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("NO");
		
		// Invalid input, then YES.		
		player = new PlayerBuilder()
			.setHand(	new Cards(CHANCELLOR, COPPER, ESTATE, COPPER),
						new Cards())
		    .setDiscard(new Cards(MILITIA),
		    			new Cards(MILITIA, COPPER, ESTATE, COPPER))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("INVALID\nYES");
	}
	
	@Test
	public void testVillage() {
	  Cards discard = new Cards(MILITIA);
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  expectedTp.addCards(1);
	  expectedTp.addActions(2);
	  cardToPlay = VILLAGE;	 
	  
	  player = new PlayerBuilder()
		.setHand(new Cards(VILLAGE, COPPER),
				 new Cards(COPPER, COLONY))
	    .setDiscard(discard,discard)
	    .setDeck(new Cards(COLONY),
	    		 new Cards())
	    .setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");
	  runSimple();
	}
	
	@Test
	public void testWoodcutter() {
	  Cards deck = new Cards(COLONY);
	  Cards discard = new Cards(MILITIA);
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  expectedTp.addBuys(1);
	  expectedTp.addCoins(2);
	  cardToPlay = WOODCUTTER;	 
	  
	  player = new PlayerBuilder()
		.setHand(new Cards(WOODCUTTER, COPPER),
				 new Cards(COPPER))
	    .setDiscard(discard,discard)
	    .setDeck(deck)
	    .setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");
	  runSimple();
	}
	
	@Test
	public void testWorkshop() {
		// Gain a card costing up to 4.
		Cards hand = new Cards(WORKSHOP, COPPER, ESTATE);
		Cards deck = new Cards(PROVINCE, DUCHY);
		trash = new Trash( COLONY);
		Cards expectedHand = new Cards(COPPER, ESTATE);
		expectedTrash = trash;
		cardToPlay = WORKSHOP;
		
		// Set-up board. All we need is the card being bought.
		actionCards = new Cards(BEAUROCRAT, FEAST, FESTIVAL);
		
		// Buy an available card.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards(MILITIA),
		    			new Cards(MILITIA, BEAUROCRAT))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("BEAUROCRAT");		
		
		// Buy an unavailable (card not on the board) card & then an available one.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards(MILITIA),
		    			new Cards(MILITIA, BEAUROCRAT))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("MILITIA\nBEAUROCRAT");
		
		// Buy an unavailable (depleted card stack) card & then an available one.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards(MILITIA),
		    			new Cards(MILITIA, BEAUROCRAT))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		opponents = new TestPlayer[] { new TestPlayer("P2"), new TestPlayer("P3") };
		setUp("FEAST\nBEAUROCRAT");
		board.buy(FEAST, 10); // Deplete the deck.
		assertEquals(0, board.getCardCount(FEAST));
		CardUtil.playCard(WORKSHOP, player, board);
		verify();
		
		// Try to buy a card that's more expensive than 4, then buy one that is cheaper.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards(MILITIA),
		    			new Cards(MILITIA, BEAUROCRAT))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("FESTIVAL\nBEAUROCRAT");
	}
	
	@Test
	public void testBeaurocrat() {
		// Player gets a silver & opponents put victory, if in hand, into deck.
		StringBuilder userInput = new StringBuilder();
		opponents = new TestPlayer[6];
		trash = new Trash( COPPER);
		expectedTrash = trash;
		Cards discard = new Cards(FESTIVAL);
		cardToPlay = BEAUROCRAT;
		
		// Verify player gets a silver.
		player = new PlayerBuilder()
			.setHand(	new Cards(BEAUROCRAT, COPPER),
						new Cards(COPPER))
			.setDiscard(discard)
			.setDeck(	new Cards(GOLD),
						new Cards(GOLD, SILVER))
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
			.build("P1");
		
		// No victory/reaction, does nothing.
		opponents[0] = new PlayerBuilder()
			.setHand(	new Cards(COPPER),
						new Cards(COPPER))
			.setDiscard(discard)
			.setDeck(	new Cards(MILITIA),
						new Cards(MILITIA))
			.setTp(opponentTp, opponentExpectedTp)
			.setNextTp(opponentNextTp, opponentExpectedNextTp)
			.build("P2");
		
		// Victory, no reaction, puts victory on deck.
		opponents[1] = new PlayerBuilder()
			.setHand(	new Cards(COPPER, ESTATE),
						new Cards(COPPER))
			.setDiscard(discard)
			.setDeck(	new Cards(CURSE),
						new Cards(CURSE, ESTATE))
			.setTp(opponentTp, opponentExpectedTp)
			.setNextTp(opponentNextTp, opponentExpectedNextTp)
			.build("P3");
		
		// Multiple victories, no reaction, puts a chosen one on deck. First input invalid.
		opponents[2] = new PlayerBuilder()
			.setHand(	new Cards(MILITIA, ESTATE, DUCHY),
						new Cards(MILITIA, DUCHY ))
			.setDiscard(discard)
			.setDeck(	new Cards(CHAPEL),
						new Cards(CHAPEL, ESTATE))
			.setTp(opponentTp, opponentExpectedTp)
			.setNextTp(opponentNextTp, opponentExpectedNextTp)
			.build("P4");
		userInput.append("COPPER\nESTATE\n");
		
		// Reaction, no victory. Reveal reaction. First reaction card entry is invalid.
		opponents[3] = new PlayerBuilder()
			.setHand(	new Cards(COPPER, MOAT), // TOOD(ekon): may want to add a more interesting reaction.
						new Cards(COPPER, MOAT))
			.setDiscard(discard)
			.setDeck(	new Cards(GARDENS),
						new Cards(GARDENS))
			.setTp(opponentTp, opponentExpectedTp)
			.setNextTp(opponentNextTp, opponentExpectedNextTp)
			.build("P5");
		userInput.append("YES\nCOPPER\nMOAT\n");
		
		// Reaction and victory, reveal reaction.
		opponents[4] = new PlayerBuilder()
			.setHand(	new Cards(COPPER, ESTATE, MOAT),
						new Cards(COPPER, ESTATE, MOAT))
			.setDiscard(discard)
			.setDeck(	new Cards(GARDENS),
						new Cards(GARDENS))
			.setTp(opponentTp, opponentExpectedTp)
			.setNextTp(opponentNextTp, opponentExpectedNextTp)
			.build("P6");
		userInput.append("MAYBE\nYES\nMOAT\n");
		
		// Reaction and victory, don't reveal reaction.
		opponents[5] = new PlayerBuilder()
			.setHand(	new Cards(COPPER, ESTATE, MOAT),
						new Cards(COPPER, MOAT))
			.setDiscard(discard)
			.setDeck(	new Cards(GARDENS),
						new Cards(GARDENS, ESTATE))
			.setTp(opponentTp, opponentExpectedTp)
			.setNextTp(opponentNextTp, opponentExpectedNextTp)
			.build("P7");
		userInput.append("NO\n");

		run(userInput.toString());
	}
	
	@Test
	public void testFeast() {
		// Gain a card costing up to 5. Trash Feast.
		Cards hand = new Cards(FEAST, COPPER, ESTATE);
		Cards expectedHand = new Cards(COPPER, ESTATE);
		Cards deck = new Cards(PROVINCE, DUCHY);
		trash = new Trash( COLONY);
		expectedTrash = new Trash ( COLONY, FEAST);
		cardToPlay = FEAST;
		
		// Set-up board. All we need is the card being bought.
		actionCards = new Cards(FESTIVAL, COUNCIL_ROOM, GOLD);
		
		// Buy an available card at exact cost.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards(MILITIA),
		    			new Cards(MILITIA, FESTIVAL))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("FESTIVAL");		
		
		// Buy an unavailable (card not on the board) card & then an available one.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards(MILITIA),
		    			new Cards(MILITIA, FESTIVAL))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("MILITIA\nFESTIVAL");
		
		// Buy an unavailable (depleted card stack) card & then an available one.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards(MILITIA),
		    			new Cards(MILITIA, FESTIVAL))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		opponents = new TestPlayer[] { new TestPlayer("P2"), new TestPlayer("P3") };
		setUp("COUNCIL ROOM\nFESTIVAL");
		board.buy(COUNCIL_ROOM, 10); // Deplete the deck.
		assertEquals(0, board.getCardCount(COUNCIL_ROOM));
		CardUtil.playCard(FEAST, player, board);
		verify();
		
		// Try to buy a card that's more expensive than 4, then buy one that is cheaper.
		player = new PlayerBuilder()
			.setHand(hand, expectedHand)
		    .setDiscard(new Cards(MILITIA),
		    			new Cards(MILITIA, FESTIVAL))
		    .setDeck(deck)
		    .setTp(tp, expectedTp)
			.setNextTp(nextTp, expectedNextTp)
		    .build("P1");
		runSimple("GOLD\nFESTIVAL");
	}
	
	@Test
	public void testGardens() {
	  // TODO(ekon): This only matters at the end of the game, so may need to go in another test class.
	}
	
	@Test
	public void testMilitia() {
	  // Opponents discard down to 3 cards, unless show reaction.
	  StringBuilder userInput = new StringBuilder();
	  Cards deck = new Cards(PROVINCE);
	  Cards discard = new Cards(COPPER);
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  expectedTp.addCoins(2);
	  opponents = new TestPlayer[3];
	  cardToPlay = MILITIA;
	  
	  player = new PlayerBuilder()
		.setHand(new Cards(MILITIA, DUCHY),
				 new Cards(DUCHY))
	    .setDiscard(discard)
	    .setDeck(deck)
		.setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");
	  
	  // Opponent shows reaction, doesn't have to discard.
	  opponents[0] = new PlayerBuilder()
	  	.setHand(new Cards(MOAT, DUCHY),
			 	 new Cards(MOAT, DUCHY))
		.setDiscard(discard)
		.setDeck(deck)
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
		.build("P2");
	  userInput.append("YES\n");
	  
	  // Opponent chooses to not show reaction, has to discard.
	  opponents[1] = new PlayerBuilder()
	  	.setHand(new Cards(GOLD, LABORATORY, COPPER, ESTATE, MOAT),
			 	 new Cards(GOLD, LABORATORY, COPPER))
		.setDiscard(new Cards(ESTATE),
					new Cards(ESTATE, ESTATE, MOAT))
		.setDeck(deck)
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
		.build("P3");
	  userInput.append("NO\nESTATE,MOAT\n");
	  
	  // Opponent doesn't have reaction, has to discard.
	  opponents[2] = new PlayerBuilder()
	  	.setHand(new Cards(GOLD, LABORATORY, COPPER, ESTATE, DUCHY),
			 	 new Cards(GOLD, LABORATORY, COPPER))
		.setDiscard(new Cards(ESTATE),
					new Cards(ESTATE, DUCHY, ESTATE))
		.setDeck(deck)
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
		.build("P4");
	  userInput.append("ESTATE,DUCHY\n");
	  
	  run(userInput.toString());
	}
	
	@Test
	public void testMoneylander() {
	  Cards discard = new Cards ( PROVINCE);
	  Cards deck = new Cards ( COLONY);
	  cardToPlay = MONEYLANDER;
	  
	  // Verify trashing copper gives player 3 extra coins.
	  expectedTp.addCoins(3);
	  player = new PlayerBuilder()
		.setHand(new Cards(MONEYLANDER, MILITIA, COPPER, COPPER),
				 new Cards(MILITIA, COPPER))
	    .setDiscard(discard)
	    .setDeck(deck)
		.setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");

	  trash = new Trash(COLONY);
	  expectedTrash = new Trash(COLONY, COPPER);
	  runSimple();
	  
	  // Verify a hand with no copper.
	  expectedTp = new TurnProperties();
	  player = new PlayerBuilder()
		.setHand(new Cards(MONEYLANDER, MILITIA),
				 new Cards(MILITIA))
	    .setDiscard(discard)
	    .setDeck(deck)
		.setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");

	  trash = new Trash( COLONY);
	  expectedTrash = trash;
	  runSimple();
	}
	
	@Test
	public void testRemodel() {
		Cards deck = new Cards(PROVINCE, DUCHY);
		trash = new Trash( COLONY);
		expectedTrash = new Trash( COLONY, COPPER);
		cardToPlay = REMODEL;
		
		// Set-up board. All we need is the card being bought.
		actionCards = new Cards(MOAT, CELLAR);
		
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
		board.buy(CELLAR, 10); // Deplete the deck.
		assertEquals(0, board.getCardCount(CELLAR));
		CardUtil.playCard(REMODEL, player, board);
		verify();
	}
	
	@Test
	public void testSmithy() {
	  Cards discard = new Cards(MILITIA);
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  expectedTp.addCards(3);
	  cardToPlay = SMITHY;	 
	  
	  player = new PlayerBuilder()
		.setHand(new Cards(SMITHY, COPPER),
				 new Cards(COPPER, ESTATE, DUKE, MOAT))
	    .setDiscard(discard,discard)
	    .setDeck(new Cards(ESTATE, DUKE, MOAT),
	    		 new Cards())
	    .setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");
	  runSimple();
	}
	
	@Test
	public void testSpy() {
	  // Each player (including you) reveals the top card of his deck and either discards it or puts it back, your choice.	  
	  trash = new Trash(COLONY);
	  expectedTrash = trash;
	  opponents = new TestPlayer[3];
	  StringBuilder userInput = new StringBuilder();
	  expectedTp.addCards(1);
	  expectedTp.addActions(1);
	  cardToPlay = SPY;
	  
	  // Player discards card.
	  player = new PlayerBuilder()
		.setHand(	new Cards(SPY, ESTATE),
					new Cards(ESTATE, DUKE))
	    .setDeck(	new Cards(PROVINCE, DUKE), // This is a little tricky because cards from deck get first picked up and then peeked.
					new Cards())
	    .setDiscard(new Cards(COPPER),
	    			new Cards(COPPER, PROVINCE))
		.setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");
	  userInput.append("YES\n");
	  
	  // Opponent reveals reaction. Nothing happens.
	  opponents[0] = new PlayerBuilder()
		.setHand(	new Cards(MILITIA, MOAT),
					new Cards(MILITIA, MOAT))
		.setDeck(	new Cards(ESTATE),
		   			new Cards(ESTATE))
	    .setDiscard(new Cards(COPPER, ESTATE),
	    			new Cards(COPPER, ESTATE))
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
	    .build("P2");
	  userInput.append("YES\n");
	  
	  // Opponent decides to not reveal reaction. Player chooses to put card back on deck.
	  opponents[1] = new PlayerBuilder()
		.setHand(	new Cards(MILITIA, MOAT),
					new Cards(MILITIA, MOAT))
		.setDeck(	new Cards(ESTATE),
		    		new Cards(ESTATE))
	    .setDiscard(new Cards(COPPER),
	    			new Cards(COPPER))
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
	    .build("P3");
	  userInput.append("NO\nNO\n");
	  
	  // Opponent does not have reaction to reveal. Player chooses to put card in discard.
	  opponents[2] = new PlayerBuilder()
		.setHand(	new Cards(MILITIA, COPPER),
					new Cards(MILITIA, COPPER))
		.setDeck(	new Cards(ESTATE, GOLD),
		   			new Cards(ESTATE))
	    .setDiscard(new Cards(COPPER),
	    			new Cards(COPPER, GOLD))
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
	    .build("P4");
	  userInput.append("YES\n");

	  run(userInput.toString());
	  
	  // Verify player putting card back on deck.
	  player = new PlayerBuilder()
		.setHand(	new Cards(SPY, ESTATE),
					new Cards(ESTATE, DUKE))
		.setDeck(	new Cards(PROVINCE, DUKE),
		    		new Cards(PROVINCE))
	    .setDiscard(new Cards(COPPER),
	    			new Cards(COPPER))
		.setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");
	  userInput = new StringBuilder("NO\n");
	  
	  // Verify that opponent with no more cards left on deck, has the cards from his discard reshuffled. Player chooses to keep card on deck.
	  opponents = new TestPlayer[1];
	  opponents[0] = new PlayerBuilder()
		.setHand(	new Cards(ESTATE),
					new Cards(ESTATE))
		.setDeck(	new Cards(),
		    		new Cards(CURSE, COPPER))
	    .setDiscard(new Cards(CURSE, COPPER),
	    			new Cards())
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
	    .build("P2");
	  userInput.append("NO\n");
	  
	  run(userInput.toString());
	}
	
	@Test
	public void testThief() {
	  // Each other player reveals the top 2 cards of his deck. If they revealed any Treasure cards, they trash one of them that you choose.
	  // You may gain any or all of these trashed cards. They discard the other revealed cards.

	  Cards deck = new Cards(COPPERSMITH);
	  opponents = new TestPlayer[4];
	  StringBuilder userInput = new StringBuilder();
	  cardToPlay = THIEF;
	  
	  // Opponent doesn't have Treasure cards & has to pick up from Discard.
	  opponents[0] = new PlayerBuilder()
		.setHand(	new Cards(MOAT),
					new Cards(MOAT))
		.setDeck(	new Cards(MILITIA),
		   			new Cards(COPPER, MILITIA))
	    .setDiscard(new Cards(ESTATE, PROVINCE),
	    			new Cards())
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
	    .build("P1");
	  
	  // Opponent reveals reaction. Nothing happens.
	  opponents[0] = new PlayerBuilder()
		.setHand(	new Cards(MOAT),
					new Cards(MOAT))
		.setDeck(	new Cards(COPPER, MILITIA),
		   			new Cards(COPPER, MILITIA))
	    .setDiscard(new Cards(DUCHY),
	    			new Cards(DUCHY))
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
	    .build("P2");
	  userInput.append("YES\n");
	  
	  // Opponent decides to not reveal reaction. Player chooses to trash card.
	  opponents[1] = new PlayerBuilder()
		.setHand(	new Cards(MOAT),
					new Cards(MOAT))
		.setDeck(	new Cards(COPPER, MILITIA),
		    		new Cards())
	    .setDiscard(new Cards(DUCHY),
	    			new Cards(DUCHY, MILITIA))
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
	    .build("P3");
	  userInput.append("NO\nTRASH\n");
	  
	  // Opponent does not have reaction to reveal. Player chooses to take his treasure.
	  opponents[2] = new PlayerBuilder()
		.setHand(	new Cards(FEAST),
					new Cards(FEAST))
		.setDeck(	new Cards(GOLD, MILITIA),
		   			new Cards(MILITIA))
	    .setDiscard(new Cards(DUCHY),
	    			new Cards(DUCHY, MILITIA))
		.setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P4");
	  userInput.append("TAKE\n");
	  
	  // Opponent has 2 treasures. Player choose to take one of them.
	  opponents[3] = new PlayerBuilder()
		.setHand(	new Cards(FEAST),
					new Cards(FEAST))
		.setDeck(	new Cards(SILVER, COPPER),
		   			new Cards(MILITIA))
	    .setDiscard(new Cards(DUCHY),
	    			new Cards(DUCHY, COPPER))
		.setTp(opponentTp, opponentExpectedTp)
		.setNextTp(opponentNextTp, opponentExpectedNextTp)
	    .build("P5");
	  userInput.append("SILVER\nTAKE\n");
	  
	  // Player gains opponent's Treasure.
	  player = new PlayerBuilder()
		.setHand(	new Cards(THIEF, ESTATE),
					new Cards(ESTATE))
	    .setDeck(deck)
	    .setDiscard(new Cards(ESTATE),
	    			new Cards(ESTATE, GOLD, SILVER))
		.setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P6");

	  trash = new Trash(COLONY);
	  expectedTrash = new Trash(COLONY, COPPER);
	  run(userInput.toString());
	}
	
	@Test
	public void testThroneRoom() {
	  Cards deck = new Cards(MILITIA);
	  trash = new Trash(COLONY);
	  expectedTrash = trash;
	  cardToPlay = THRONE_ROOM;
	  actionCards = new Cards(CHAPEL);
	  
	  // Player has no action cards.
	  player = new PlayerBuilder()
		.setHand(	new Cards(THRONE_ROOM, ESTATE),
					new Cards(ESTATE))
	    .setDeck(deck)
	    .setDiscard(new Cards(COPPER),
	    			new Cards(COPPER))
	    .build("P1");
	  runSimple("");
	  
	  // Player has 1 action card (workshop - gain 2 cards costing up to 4).
	  player = new PlayerBuilder()
		.setHand(	new Cards(THRONE_ROOM, ESTATE, WORKSHOP),
					new Cards(ESTATE))
	    .setDeck(deck)
	    .setDiscard(new Cards(COPPER),
	    			new Cards(COPPER, SILVER, CHAPEL))
	    .build("P1");
	  runSimple("SILVER\nCHAPEL");
	  
	  // Player has 2 action cards, but chooses to play workshop (gain 2 cards costing up to 4).
	  player = new PlayerBuilder()
		.setHand(	new Cards(THRONE_ROOM, ESTATE, WORKSHOP, MILITIA),
					new Cards(ESTATE, MILITIA))
	    .setDeck(deck)
	    .setDiscard(new Cards(COPPER),
	    			new Cards(COPPER, SILVER, CHAPEL))
	    .build("P1");
	  runSimple("WORKSHOP\nSILVER\nCHAPEL");
	  
	  // TODO(ekon): add test to ensure when playing card that has +coins/+buys/+actions/etc. is counted properly.
	}
	
	@Test
	public void testCouncilRoom() {
	  Cards discard = new Cards(MILITIA);
	  opponents = new TestPlayer[2];
	  trash = new Trash(COLONY);
	  expectedTrash = trash;
	  expectedTp.addCards(4);
	  expectedTp.addBuys(1);
	  cardToPlay = COUNCIL_ROOM;

	  player = new PlayerBuilder()
	  	// The player doesn't actually gain 4 cards here, but rather later.
	  // TODO(ekon): is this the right type of logic? Shouldn't we acknowledge card pick ups right away?
		.setHand(	new Cards(MILITIA, COUNCIL_ROOM),
					new Cards(MILITIA, ESTATE, GOLD, SILVER, ESTATE))
	    .setDeck(new Cards(ESTATE, GOLD, SILVER, ESTATE),
	    		 new Cards())
	    .setDiscard(discard)
		.setTp(tp, expectedTp)
	    .build("P1");
	  
	  opponents[0] = new PlayerBuilder()
		.setHand(	new Cards(MILITIA),
					new Cards(MILITIA, ESTATE))
	    .setDeck(new Cards(ESTATE),
	    		 new Cards())
	    .setDiscard(discard)
	    .build("P2");
	  
	  opponents[1] = new PlayerBuilder()
		.setHand(	new Cards(MILITIA),
					new Cards(MILITIA, ESTATE))
	    .setDeck(new Cards(ESTATE),
	    		 new Cards())
	    .setDiscard(discard)
	    .build("P3");
	  
	  run("");
	}
	
	@Test
	public void testFestival() {
	  Cards deck = new Cards(COLONY);
	  Cards discard = new Cards(MILITIA);
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  expectedTp.addActions(2);
	  expectedTp.addBuys(1);
	  expectedTp.addCoins(2);
	  cardToPlay = FESTIVAL;	 
	  
	  player = new PlayerBuilder()
		.setHand(new Cards(FESTIVAL, COPPER),
				 new Cards(COPPER))
	    .setDiscard(discard)
	    .setDeck(deck)
	    .setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");
	  runSimple();
	}
	
	@Test
	public void testLaboratory() {
	  Cards discard = new Cards(MILITIA);
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  expectedTp.addCards(2);
	  expectedTp.addActions(1);
	  cardToPlay = LABORATORY;	 
	  
	  player = new PlayerBuilder()
		.setHand(new Cards(LABORATORY, COPPER),
				 new Cards(COPPER, ESTATE, DUKE))
	    .setDiscard(discard)
	    .setDeck(new Cards(ESTATE, DUKE),
	    		 new Cards())
	    .setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");
	  runSimple();
	}

	
	@Test
	public void testMarket() {
	  Cards discard = new Cards(MILITIA);
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  expectedTp.addCards(1);
	  expectedTp.addActions(1);
	  expectedTp.addCoins(1);
	  expectedTp.addBuys(1);
	  cardToPlay = MARKET;	 
	  
	  player = new PlayerBuilder()
		.setHand(new Cards(MARKET, COPPER),
				 new Cards(COPPER, DUKE))
	    .setDiscard(discard)
	    .setDeck(new Cards(DUKE),
	    		 new Cards())
	    .setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");
	  runSimple();
	}
	
	@Test
	public void testLibrary() {
	  // Draw until you have 7 cards in hand. You may set aside any action cards drawn this way, as you draw them;
	  // discard the set aside cards after you finish drawing.
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  cardToPlay = LIBRARY;
	  
	  // Test first 2 cards are treasure.
	  player = new PlayerBuilder()
		.setHand(new Cards(PLATINUM, ESTATE, ESTATE, ESTATE, LIBRARY),
				 new Cards(PLATINUM, ESTATE, ESTATE, ESTATE, COPPER, SILVER))
	    .setDiscard(new Cards(PROVINCE),
	    			new Cards(PROVINCE))
	    .setDeck(new Cards(COPPER, SILVER),
	    		 new Cards())
	    .build("P1");
	  runSimple();
	  
	  // Two victory cards.
	  player = new PlayerBuilder()
		.setHand(new Cards(PLATINUM, ESTATE, ESTATE, ESTATE, LIBRARY),
				 new Cards(PLATINUM, ESTATE, ESTATE, ESTATE, DUCHY, DUCHY))
	    .setDiscard(new Cards(PROVINCE),
	    			new Cards(PROVINCE))
	    .setDeck(new Cards(DUCHY, DUCHY),
	    		 new Cards())
	    .build("P1");
	  runSimple();
	  
	  // There's an action card, that the player chooses to discard.
	  player = new PlayerBuilder()
		.setHand(new Cards(PLATINUM, ESTATE, ESTATE, ESTATE, LIBRARY),
				 new Cards(PLATINUM, ESTATE, ESTATE, ESTATE, DUCHY, DUCHY))
	    .setDiscard(new Cards(PROVINCE),
	    			new Cards(PROVINCE, MILITIA))
	    .setDeck(new Cards(DUCHY, DUCHY, MILITIA),
	    		 new Cards())
	    .build("P1");
	  runSimple("Discard\n");
	  
	  // There's an action card that the player chooses to pick up.
	  player = new PlayerBuilder()
		.setHand(new Cards(PLATINUM, ESTATE, ESTATE, ESTATE, LIBRARY),
				 new Cards(PLATINUM, ESTATE, ESTATE, ESTATE, MILITIA, DUCHY))
	    .setDiscard(new Cards(PROVINCE),
	    			new Cards(PROVINCE))
	    .setDeck(new Cards(MILITIA, DUCHY),
	    		 new Cards())
	    .build("P1");
	  runSimple("Pick up\n");
	}
	
	@Test
	public void testMine() {
	  // Trash treasure, get another costing up to 3 more.
	  Cards discard = new Cards(MILITIA);
	  Cards deck = new Cards(DUKE);
	  cardToPlay = MINE;
	  
	  // Trash copper, gain silver. Board has Copper and Silver available to buy.
	  player = new PlayerBuilder()
		.setHand(new Cards(MINE, COPPER),
				 new Cards(SILVER))
	    .setDiscard(discard)
	    .setDeck(deck)
	    .build("P1");

	  trash = new Trash(ESTATE);
	  expectedTrash = new Trash(ESTATE, COPPER);
	  opponents = new TestPlayer[] { new TestPlayer("P2"), new TestPlayer("P3") };
	  setUp("SILVER");
	  int initialCopperCountOnBoard = board.getCardCount(SILVER);
	  CardUtil.playCard(MINE, player, board);
	  verify();
	  assertEquals(initialCopperCountOnBoard - 1, board.getCardCount(SILVER));
	  
	  // No treasures to trash.
	  player = new PlayerBuilder()
		.setHand(new Cards(MINE),
				 new Cards())
	    .setDiscard(discard)
	    .setDeck(deck)
	    .build("P1");
	  
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  runSimple("SILVER");
	  
	  // Only 1 option available on board.
	  player = new PlayerBuilder()
		.setHand(new Cards(MINE, COPPER),
				 new Cards(COPPER))
	    .setDiscard(discard)
	    .setDeck(deck)
	    .build("P1");

	  trash = new Trash(ESTATE);
	  expectedTrash = new Trash(ESTATE, COPPER); // Card from board got trashed.
	  opponents = new TestPlayer[] { new TestPlayer("P2"), new TestPlayer("P3") };
	  setUp("COPPER");
	  initialCopperCountOnBoard = board.getCardCount(COPPER);
	  board.buy(SILVER, board.getCardCount(SILVER));
	  assertEquals(0, board.getCardCount(SILVER));
	  CardUtil.playCard(MINE, player, board);
	  verify();
	  assertEquals(initialCopperCountOnBoard - 1, board.getCardCount(COPPER));
	  
	  // No cards available in board to get.
	  player = new PlayerBuilder()
		.setHand(new Cards(MINE, COPPER),
				 new Cards(COPPER))
	    .setDiscard(discard)
	    .setDeck(deck)
	    .build("P1");

	  trash = new Trash(ESTATE);
	  expectedTrash = trash; // Nothing got trashed.
	  opponents = new TestPlayer[] { new TestPlayer("P2"), new TestPlayer("P3") };
	  setUp("");
	  board.buy(COPPER, board.getCardCount(COPPER));
	  board.buy(SILVER, board.getCardCount(SILVER));
	  assertEquals(0, board.getCardCount(COPPER));
	  assertEquals(0, board.getCardCount(SILVER));
	  CardUtil.playCard(MINE, player, board);
	  verify();
	}
	
	@Test
	public void testAdventurer() {
	  // Reveal cards from  your deck until you reveal 2 Treasure cards.
	  // Put those treasure cards into your hand and discard the other revealed cards.
	  trash = new Trash(ESTATE);
	  expectedTrash = trash;
	  cardToPlay = ADVENTURER;
	  
	  // Test first 2 cards are treasure.
	  player = new PlayerBuilder()
		.setHand(new Cards(ADVENTURER, PLATINUM),
				 new Cards(PLATINUM, COPPER, SILVER))
	    .setDiscard(new Cards(PROVINCE),
	    			new Cards(PROVINCE))
	    .setDeck(new Cards(COPPER, SILVER),
	    		 new Cards())
	    .build("P1");
	  runSimple();
	  
	  // Test no treasures in deck, reshuffle to get 1 more treasure (no others left).
	  player = new PlayerBuilder()
		.setHand(new Cards(ADVENTURER, PLATINUM),
				 new Cards(PLATINUM, COPPER, SILVER))
	    .setDiscard(new Cards(SILVER),
	    			new Cards())
	    .setDeck(new Cards(COPPER),
	    		 new Cards())
	    .build("P1");
	  runSimple();
	  
	  // Test no treasures in hand or deck.
	  player = new PlayerBuilder()
		.setHand(new Cards(ADVENTURER, PLATINUM),
				 new Cards(PLATINUM))
	    .setDiscard(new Cards(PROVINCE),
	    			new Cards(PROVINCE, COLONY))
	    .setDeck(new Cards(COLONY),
	    		 new Cards())
	    .build("P1");
	  runSimple();
	}
	
	private TestPlayer recreateRemodelPlayer(Cards deck) {
	  return new PlayerBuilder()
		.setHand( 	new Cards(REMODEL, COPPER),
					new Cards())
	    .setDiscard(new Cards(MILITIA),
	    			new Cards(MILITIA, MOAT))
	    .setDeck(deck)
	    .setTp(tp, expectedTp)
		.setNextTp(nextTp, expectedNextTp)
	    .build("P1");
	}
	
	private void runSimple() {
	  runSimple("");
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
