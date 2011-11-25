package ekon.dominion;

import static ekon.dominion.Card.CURSE;
import static ekon.dominion.Card.CardType.ACTION;
import static ekon.dominion.Card.CardType.TREASURE;
import static ekon.dominion.Card.CardType.VICTORY;
import static ekon.dominion.Player.CardPlace.BOARD;
import static ekon.dominion.Player.CardPlace.DECK;
import static ekon.dominion.Player.CardPlace.DISCARD;
import static ekon.dominion.Player.CardPlace.HAND;
import static ekon.dominion.Player.CardPlace.TRASH;
import ekon.dominion.board.Board;

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
		
		// Add coins,buys,actions, etc. from card.
		Actions actions = card.actions();
		player.tp().add(actions.plusCards(), actions.plusActions(), actions.plusCoins(), actions.plusBuys(),
			actions.plusVictoryTokens());
		
		// If card has any +cards, then pick them up.
		if (actions.plusCards() > 0 ) {
		  player.mover().from(DECK).to(HAND).move(actions.plusCards());
		  //player.tp().useCards(); // TODO(ekon): Do I actually need this?
		}
		
		// For cards that have a description with other actions to follow, do them.
		if (!card.description().equals("")) {
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
    			case THRONE_ROOM: playThroneRoom(player, board); break;
    			case COUNCIL_ROOM: playCouncilRoom(player); break;
    			case LIBRARY: playLibrary(player); break;
    			case MINE: playMine(player, board); break;
    			case WITCH: playWitch(player, board); break;
    			case ADVENTURER: playAdventurer(player); break;
    			default:
    				throw new GameException(GameException.Type.CODE_ISSUE, "Card " + card.name() + " doesn't have playing instructions.");
    		}
		}
	}

	private static void playCellar(Player player) {
	  Cards cards = uiUtil.getCardsFromUser("Card(s) to discard? or DONE?", player.hand().availableCards(), true);
	  if (cards != null) {
		player.mover().from(HAND).to(DISCARD).move(cards);
		player.mover().from(DECK).to(HAND).move(cards.size());
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
			player.mover().from(BOARD).to(DECK).move(Card.SILVER);
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
	  // Each other player reveals the top 2 cards of his deck. If they revealed any Treasure cards, they trash one of them that you choose.
	  // You may gain any or all of these trashed cards. They discard the other revealed cards.
	  
	  for (Player opponent : player.opponents()) {
		uiUtil.tellUser(opponent.name() + " has been attacked with " + Card.THIEF);
		if (!uiUtil.revealReaction(opponent)) {
		  Cards revealedCards = opponent.mover().from(DECK).move(2);
		  Cards cardsToDiscard = new Cards(revealedCards);
		  
		  // TODO(ekon): Does this include treasure-action cards?
		  if (revealedCards.contains(TREASURE)) {
			Cards availableCards = revealedCards.getCards(TREASURE);
			Card card;
			if (availableCards.size() > 1) {
			  card = uiUtil.getCardFromUser(player.name() + "Which card do you wat to trash/take?", availableCards);
			} else {
			  card = availableCards.get(0);
			}
			cardsToDiscard.remove(card);
			boolean toTrash = uiUtil.getBooleanFromUser("What do you want to do with the card?", "TRASH", "TAKE");
			if (toTrash) { opponent.mover().to(TRASH).move(card); }
			else { player.mover().to(DISCARD).move(card); }			
		  }
		  
		  // Discard leftover cards;
		  opponent.mover().to(DISCARD).move(cardsToDiscard);
		}
	  }
	}
	
	// TODO(ekon): Add the rules around playing this card twice & ensure it works properly.
	private static void playThroneRoom(Player player, Board board) {
	  // Choose an action card in your hand. Play it twice.
	  Card cardToPlay = null;
	  
	  Cards availableActions = player.hand().availableCards().getCards(ACTION);
	  if ((availableActions == null) || (availableActions.size() == 0)) {
		uiUtil.tellUser("You have no available actions.");
		return;
	  } else if (availableActions.size() == 1) {
		cardToPlay = availableActions.get(0);
	  } else {
		cardToPlay = uiUtil.getCardFromUser("Choose an action card to play twice.", availableActions);
	  }
	  
	  playCard(cardToPlay, player, board);
	  playCard(cardToPlay, player, board);
	}
	
	private static void playCouncilRoom(Player player) {
	  // Player picks up 4, which was already taken care of. Opponents pick up 1.
	  
	  for (Player opponent : player.opponents()) {
		opponent.mover().from(DECK).to(HAND).move();
	  }
	}
	
	private static void playLibrary(Player player) {
	  // Draw until you have 7 cards in hand. You may set aside any action cards drawn this way, as you draw them;
	  // discard the set aside cards after you finish drawing.
	  
	  // It's remotely possible that entire deck+hand+discard is less than 7 cards.
	  Card card = player.mover().from(DECK).move();
	  while((card != null) && (player.hand().cards().size() < 7)) {
		boolean toPickUp = true;
		if (card.types().contains(ACTION)) {
		  toPickUp = uiUtil.getBooleanFromUser("What do you want to do with " + card + "? Pick up or Discard?", "Pick Up", "Discard");
		}
		
		if (player.hand().cards().size() < 7) {
		  if (toPickUp) {
			player.mover().to(HAND).move(card);
		  } else {
			player.mover().to(DISCARD).move(card);
		  }
		  card = player.mover().from(DECK).move();
		}
	  }
	}
	
	private static void playMine(Player player, Board board) {
	  // Trash a treasure card from your hand. Gain a treasure card costing up to 3 more; put it into your hand.
	  
	  Cards availableTreasuresInHand = player.hand().getCards(TREASURE);
	  Card cardToTrash = null;
	  if (availableTreasuresInHand.size() == 0) {
		uiUtil.tellUser("You don't have any Treasures in your hand to trash.");
		return;
	  }
	  else if (availableTreasuresInHand.size() == 1) {
		cardToTrash = availableTreasuresInHand.get(0);
	  } else {
		cardToTrash = uiUtil.getCardFromUser("Which card would you like to trash?", availableTreasuresInHand);
	  }
		
	  Cards availableTreasureToGet = board.getAvailableCardCostingUpTo(cardToTrash.cost() + 3, TREASURE);
	  if (availableTreasureToGet.size() == 0) {
		uiUtil.tellUser("There are no available cards to get from the board.");
		return;
	  }
		
	  Card cardToGain = uiUtil.getCardFromUser("Which card would you like to replace it with?", availableTreasureToGet);
	  
	  player.mover().from(HAND).to(TRASH).move(cardToTrash);
	  player.mover().from(BOARD).to(HAND).move(cardToGain);
	}
	
	private static void playWitch(Player player, Board board) {
	  // Each opponent gains a curse.
	  for (Player opponent : player.opponents()) {
		uiUtil.tellUser(opponent.name() + "'s been attacked with WITCH.");
		// If not playing reaction, then getting attacked.
		if(!uiUtil.revealReaction(opponent)) {
		  opponent.mover().from(BOARD).to(DISCARD).move(CURSE, true);
		}
	  }
	}
	
	private static void playAdventurer(Player player) {
	  // Reveal cards from  your deck until you reveal 2 Treasure cards.
	  // Put those treasure cards into your hand and discard the other revealed cards.
	  
	  Cards treasure = new Cards();
	  Cards cardsToDiscard = new Cards();
	  
	  Card cardToPickUp = player.mover().from(DECK).move();
	  while((cardToPickUp != null) && (treasure.size() < 2 )) {
		if (cardToPickUp.types().contains(TREASURE)) {
		  treasure.add(cardToPickUp);
		} else {
		  cardsToDiscard.add(cardToPickUp);
		}
		
		if (treasure.size() < 2 ) {
		  cardToPickUp = player.mover().from(DECK).move();
		}
	  }
	  
	  player.mover().to(HAND).move(treasure, true);
	  player.mover().to(DISCARD).move(cardsToDiscard);
	}
}
