package ekon.dominion;

// @formatter:off
import static ekon.dominion.Player.CardPlace.*;
import static ekon.dominion.Card.CardType.*;
// @formatter:on
import ekon.dominion.board.Board;

// @formatter:off
public class CardUtil {
	public static UIUtil uiUtil = UIUtil.getUIUtil();
	
	public static int calculateVictoryPoints(Cards cards) {
		int points = 0;
		
		// keep track of the number of duchies, in case he has the duchess card.
		int numDuchies = 0;
		int numGardens = 0;
		int numDukes = 0;
		
		for (Card card : cards) {
			if (card.types().contains(VICTORY)) {
				points += card.victoryPoints();
				
				if (card == Card.GARDENS) { numGardens++; }
				if (card == Card.DUKE) { numDukes++; }
				if (card == Card.DUCHY) { numDuchies++; }
			}
		}
		
		if (numGardens > 0) {
			int numPointsPerGarden = cards.size() % 10;
			points += (numGardens * numPointsPerGarden);
		}
		
		if (numDukes > 0) {
			points += (numDukes * numDuchies);
		}
		
		return points;
	}
	
	public static void playCard(Card card, Player player, Board board) {
		player.hand().playAction(card);
		uiUtil.writeCard(card);
		
		switch(card) {
			case CELLAR: playCellar(player); break;
			case CHAPEL: playChapel(player); break;
			case MOAT: break;
			case CHANCELLOR: playChancellor(player); break;
			case WORKSHOP: playWorkshop(player, board); break;
			case BEAUROCRAT: playBeaurocrat(player, board); break;
			case FEAST: playFeast(card, player, board); break;
			case MILITIA: playMilitia(player); break;
			case MONEYLANDER: playMoneylander(player, board); break;
			case REMODEL: playRemodel(player, board); break;
			case SPY: playSpy(player); break;
			case THIEF: playThief(player, board); break;
			case THRONE_ROOM: playThroneRoom(player); break;
			default:
				throw new GameException(GameException.Type.CODE_ISSUE, "Card " + card.name() + " doesn't have playing instructions.");
		}
	}

	private static void playCellar(Player player) {
		Cards cards = uiUtil.getCardsFromUser("Card(s) to discard? or DONE?", player.hand().availableCards(), true);
		if (cards != null) {
			player.mover().from(HAND).to(DISCARD).move(cards);
			player.pickUpCardsFromDeckToHand(cards.size());
			uiUtil.tellUser("	" + player.name() + "'s new hand: " + player.hand().toString());
		}
	}
	
	private static void playChapel(Player player) {
		Cards cards = uiUtil.getCardsFromUser("Card(s) to trash? or DONE?", player.hand().availableCards(), true);
		if (cards != null) {
			player.mover().from(HAND).to(TRASH).move(cards);
			uiUtil.tellUser("	" + player.name() + " trashed: " + cards.toString());
		}
	}
	
	private static void playChancellor(Player player) {
		boolean discardDeck = uiUtil.getBooleanFromUser("Discard deck? YES/NO?");
		if (discardDeck == true) {
			player.mover().from(HAND).to(DISCARD).move(player.hand().availableCards());
		}
	}
	
	private static void playWorkshop(Player player, Board board) {
		Card cardToGain = uiUtil.getCardCostingUpTo(4, board.getAvailableCardCostingUpTo(4));
		player.mover().to(DISCARD).move(cardToGain);
	}
	
	private static void playBeaurocrat(Player player, Board board) {
		// Gain a silver card, put on top of deck.
		if (board.getCardCount(Card.SILVER) > 0) {
			board.buy(Card.SILVER);
			player.mover().to(DECK).move(Card.SILVER);
		}
		
		// Each opponent reveals victory and puts on top of deck, or reveals hand with no victory.
		for (Player opponent : player.opponents()) {
			Card cardToPutOnDeck = null;
			uiUtil.tellUser(opponent.name() + "'s been attacked with BEAUROCRAT.");
			// If not playing reaction, then getting attacked.
			if(!uiUtil.revealReaction(opponent)) {
				Cards victoryCards = opponent.hand().getCards(VICTORY);
				if (victoryCards.size() == 0) {
					cardToPutOnDeck = null;
				} else if (victoryCards.size() == 1) {
					cardToPutOnDeck = victoryCards.get(0);
				} else {
					cardToPutOnDeck = uiUtil.getCardFromUser("Choose a victory card to put back on deck.", victoryCards);
				}
			}
			opponent.mover().from(HAND).to(DECK).move(cardToPutOnDeck, true);
		}
	}
	
	private static void playFeast(Card card, Player player, Board board) {
		Card cardToGain = uiUtil.getCardCostingUpTo(5, board.getAvailableCardCostingUpTo(5));
		player.mover().to(DISCARD).move(cardToGain);
		player.mover().from(HAND).to(TRASH).move(card);
	}
	
	private static void playMilitia(Player player) {
		// Each opponent discards to 3 cards.
		for (Player opponent : player.opponents()) {
			uiUtil.tellUser(opponent.name() + "'s been attacked with MILITIA.");
			Cards cardsToDiscard = null;
			
			// If not playing reaction, then getting attacked.
			if(!uiUtil.revealReaction(opponent)) {
				Cards availableCards = opponent.hand().cards();
				int numCardsToDiscard = availableCards.size() - 3;
				cardsToDiscard = uiUtil.getCardsFromUser(
					"Choose " + numCardsToDiscard + " cards to discard", availableCards, numCardsToDiscard, false);
			}
			opponent.mover().from(HAND).to(DISCARD).move(cardsToDiscard, true);
		}
	}
	
	private static void playMoneylander(Player player, Board board) {
	  // Trash a copper card from your hand. If you do, +3 Coins.
	  if (player.hand().contains(Card.COPPER)) {
		player.mover().from(HAND).to(TRASH).move(Card.COPPER);
		player.tp().addCoins(3);
		uiUtil.tellUser("	You trashed a copper and gained +3 Coins.");
	  }
	}
	
	private static void playRemodel(Player player, Board board) {
		Card cardToRemodelFrom = uiUtil.getCardFromUser("Which card would you like to remodel?", player.hand().availableCards());
		Card cardToRemodelTo = uiUtil.getCardFromUser("Which card would you like to get?", board.getAvailableCardCostingUpTo(cardToRemodelFrom.cost() + 2));
		
		player.mover().from(HAND).to(TRASH).move(cardToRemodelFrom);
		player.mover().to(DISCARD).move(cardToRemodelTo);
		
		uiUtil.tellUser("	You remodelled " + cardToRemodelFrom + " to " + cardToRemodelTo);
	}
	
	private static void playSpy(Player player) {
	  // Each player (including you) reveals the top card of his deck and either discards it or puts it back, your choice.
	  
	  Card revealedCard = player.mover().from(DECK).move();
	  boolean toDiscard = uiUtil.getBooleanFromUser(player.name() + ": You revealed " + revealedCard + ". Would you like to discard (YES) or put the card back on your deck (NO)?");
	  if (toDiscard) { player.mover().to(DISCARD).move(revealedCard); }
	  else { player.mover().to(DECK).move(revealedCard); }
	  
	  for (Player opponent : player.opponents()) {
		uiUtil.tellUser(opponent.name() + " has been attacked with " + Card.SPY);
		if (!uiUtil.revealReaction(opponent)) {		
		  revealedCard = opponent.mover().from(DECK).move();
		  toDiscard = uiUtil.getBooleanFromUser(opponent.name() + ": revealed " + revealedCard + ". Would you like to discard (YES) or put the card back on your deck (NO)?");
		  if (toDiscard) { opponent.mover().to(DISCARD).move(revealedCard); }
		  else { opponent.mover().to(DECK).move(revealedCard); }
		}
	  }
	}
	
	private static void playThief(Player player, Board board) {
		
	}
	
	private static void playThroneRoom(Player player) {
		
	}
}
