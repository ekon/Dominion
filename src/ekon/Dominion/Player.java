package ekon.dominion;

import static ekon.dominion.Card.*;
import static ekon.dominion.Card.CardType.*;
import static ekon.dominion.Player.CardPlace.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

import ekon.dominion.Card.CardType;
import ekon.dominion.board.Board;

public class Player implements Comparable<Player> {
  
  public static UIUtil uiUtil = UIUtil.getUIUtil();
  
  private final Cards INITIAL_CARD_SET = new Cards(ESTATE, ESTATE, ESTATE,
	  COPPER, COPPER, COPPER, COPPER, COPPER, COPPER, COPPER);
  
  private final String name;
  private Hand hand;
  private Stack<Card> deck = new Stack<Card>();
  private Cards discard = new Cards(); // Discard doesn't have to be a stack, since we don't care what went on top.
  private Players opponents;
  private Board board;
  
  private TurnProperties nextTurn;
  
  private int victoryTokens;
  
  // @formatter:off
  public Player(String name) { this.name = name; }
  
  public String name() { return name; }
  public Hand hand() { return hand; }  
  public Stack<Card> deck() { return deck; }
  public Cards discard() { return discard; }
  public Players opponents() { return opponents; }
  public TurnProperties tp() { return turn; }
  public TurnProperties nextTP() { return nextTurn; }
  public CardMover mover() { return new CardMover(); }
  
  // @formatter:on
  public void initPlayer(Players opponents, Board board) {
	this.opponents = opponents;
	this.board = board;
	
	// add cards to deck and shuffle.
	deck.addAll(INITIAL_CARD_SET.asList());
	Collections.shuffle(deck);
	pickUpNewHand();
	turn = new TurnProperties();
  }
  
  private TurnProperties turn;
  
  public void takeTurn() {
	uiUtil.tellUser(name + "'s turn. " + toString());
	
	nextTurn = new TurnProperties();
	playCards();
	
	victoryTokens += turn.victoryTokens();
	
	// Put cards from hand in discard.
	discard.add(hand.cards());
	
	// Pick up a new hand.
	pickUpNewHand();
	turn = new TurnProperties();
	// turn.addFromPreviousTurn(nextTurn); // TODO(ekon): uncomment when need to add duration cards. I think there may be a bug here.
	
	uiUtil.tellUser(name + " done. " + toString());
  }
  
  private void playCards() {
	// Play action cards first.
	while ((turn.actionsLeft() > 0) && hand.hasActionCards()) {
	  Card cardToPlay = uiUtil.getCardFromUser("What card do you want to play? or NONE.", hand().availableCards().getCards(ACTION), true);
	  if (cardToPlay != null) {
		if (turn.cards() > 0) {
		  pickUpCardsFromDeckToHand(turn.cards());
		  turn.useCards();
		}
		CardUtil.playCard(cardToPlay, this, board);	
		turn.useAction();
	  } else {
		return;
	  }
	}
	
	countTreasure();
	
	while ((turn.coins() > 0) && (turn.buys() > 0)) {
	  Card cardToBuy = uiUtil.getCardFromUser("What card do you want to buy with " + turn.coins() + " coins?",
		  board.getAvailableCardCostingUpTo(turn.coins()), true);
	  if (cardToBuy != null) {
		buyCard(cardToBuy);
	  } else {
		return;
	  }
	}
	
	// Move used up action cards to discard.
	mover().from(HAND).to(DISCARD).move(hand.actionsInPlay());
	hand.clearActionsInPlay();
  }
  
  private void countTreasure() {
	for (Card card : hand.cards()) {
	  if (card.types().contains(CardType.TREASURE)) {
		turn.addCoins(card.actions().coins());
	  }
	}
  }
  
  private void buyCard(Card card) {
	// Buy cards from board & put in discard pile.
	turn.useCoins(card.cost());
	turn.useBuy();
	board.buy(card);
	
	mover().to(DISCARD).move(card);
  }
  
  private void pickUpNewHand() {
	Cards newCards = pickUpCardsFromDeck(5);
	hand = new Hand(newCards);
  }
  
  public void pickUpCardsFromDeckToHand(int numToPickUp) {
	Cards cards = pickUpCardsFromDeck(numToPickUp);
	hand.add(cards);
  }
  
  /** @returns given number of cards picked up from deck. Re-shuffles discard & puts into deck, if necessary. */
  public Cards pickUpCardsFromDeck(int numToPickUp) {
	int numLeftToPickUp = numToPickUp;
	Cards pickedUpCards = new Cards();
	
	for (int i = 0; i < numToPickUp && (deck.size() > 0); i++, numLeftToPickUp--) {
	  pickedUpCards.add(deck.pop());
	}
	
	if (numLeftToPickUp > 0) {
	  // Re-shuffle deck and pick up more.
	  discard.shuffle();
	  deck.addAll(discard.asList());
	  discard = new Cards();
	  for (int i = 0; i < numLeftToPickUp && (deck.size() > 0); i++) {
		pickedUpCards.add(deck.pop());
	  }
	}
	
	return pickedUpCards;
  }
  
  public int getVictoryPoints() {
	Cards allCards = new Cards();
	allCards.add(deck);
	allCards.add(discard);
	allCards.add(hand.cards()); // since player is ready for next turn with hand, when game ends he'll have cards in his hand.
	
	return victoryTokens + CardUtil.calculateVictoryPoints(allCards);
  }
  
  public void initForTesting(Hand hand, Stack<Card> deck, Cards discard) {
	SecurityUtil securityUtil = new SecurityUtil();
	securityUtil.verifyCallingClassIsTest();
	
	this.hand = hand;
	this.discard = discard;
	this.deck = deck;
  }
  
  // TODO(ekon): Look at where else this needs to be called.
  private void replenishDeckFromDiscard() {
	Collections.shuffle(discard.asList());	
	for (Card card : discard.asList()) {
	  deck.push(card);
	}
	discard = new Cards();
  }

  public enum CardPlace {
	HAND,
	DECK,
	DISCARD,
	TRASH,
	NO_MOVE;
  }
  
  class CardMover {
	
	private CardPlace from = NO_MOVE;
	private CardPlace to = NO_MOVE;
	
	public CardMover from(CardPlace location) {
	  from = location;
	  return this;
	}
	
	public CardMover to(CardPlace location) {
	  to = location;
	  return this;
	}
	
	// @formatter:off
	public CardPlace from() { return from; }
	public CardPlace to() { return to; }
	
	// @formatter:off
	public void move(Card card) {
	  move(card, false);
	}
	
	public void move(Card card, boolean isNullCardAnOption) {
	  if (card == null) {
		if (isNullCardAnOption) { return; }
		else throw new IllegalArgumentException("Null Card is not an option.");
	  }
	  
	  switch (from) {
    	case HAND: 	  	hand.remove(card); break;
    	case DECK: 	  	throw new UnsupportedOperationException("Did you mean to use move() instead?");
    	case DISCARD: 	discard.remove(card); break;
    	case TRASH:   	throw new UnsupportedOperationException("moving a card from trash is not yet implemented."); // TODO(ekon): fix this.
    	case NO_MOVE: 	break;
    	default: 	  	throw new IllegalArgumentException("Unexpected place " + from.name() + " to move a card from.");
	  }
	  
	  switch (to) {
		case HAND: 		hand.add(card); break;
		case DECK: 		deck.push(card); break;
		case DISCARD: 	discard.add(card); break;
		case TRASH: 	board.trash(card); break;
		case NO_MOVE:	break;
  	  	default: 		throw new IllegalArgumentException("Unexpected place " + to.name() + " to move a card to.");
	  }
	}
	
	public void move(Cards cards) {
	  move(cards, false);
	}
	
	public void move(Cards cards, boolean isNullCardsAnOption) {
	  for (Card card : cards) {
		move(card, isNullCardsAnOption);
	  }
	}
	
	public Card move() {
	  if (to != NO_MOVE) {
		throw new UnsupportedOperationException("Did you mean to use move(Card) instead?");
	  }
	  
	  switch(from) {
		case DECK:
		  if (deck.size() > 0) {
			return deck.pop();
		  } else {
			replenishDeckFromDiscard();
			return deck.pop();
		  }
		default: throw new UnsupportedOperationException("Did you mean to use move(Card) instead?");
	  }
	}
	
	public Cards move(int numCards) {
	  Cards cards = new Cards();
	  for (int i=0; i<numCards; i++) {
		cards.add(move());
	  }
	  return cards;
	}
  }
  
  
  @Override
  public int hashCode() {
	return Arrays.hashCode(new long[] { name.hashCode(), hand.hashCode(), deck.hashCode(), discard.hashCode(),
		opponents.hashCode(), victoryTokens, board.hashCode() });
  }
  
  @Override
  public boolean equals(Object obj) {
	if (this == obj)
	  return true;
	if (obj == null)
	  return false;
	if (getClass() != obj.getClass())
	  return false;
	Player other = (Player) obj;
	if (INITIAL_CARD_SET == null) {
	  if (other.INITIAL_CARD_SET != null)
		return false;
	} else if (!INITIAL_CARD_SET.equals(other.INITIAL_CARD_SET))
	  return false;
	if (board == null) {
	  if (other.board != null)
		return false;
	} else if (!board.equals(other.board))
	  return false;
	if (deck == null) {
	  if (other.deck != null)
		return false;
	} else if (!deck.equals(other.deck))
	  return false;
	if (discard == null) {
	  if (other.discard != null)
		return false;
	} else if (!discard.equals(other.discard))
	  return false;
	if (hand == null) {
	  if (other.hand != null)
		return false;
	} else if (!hand.equals(other.hand))
	  return false;
	if (name == null) {
	  if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	  return false;
	if (opponents == null) {
	  if (other.opponents != null)
		return false;
	} else if (!opponents.equals(other.opponents))
	  return false;
	if (victoryTokens != other.victoryTokens)
	  return false;
	return true;
  }
  
  @Override
  public String toString() {
	return "{name=" + name + " | hand=" + hand.toString() + " | discard=" + discard.toString()
		+ " | deck=" + deck.toString() + " | VP=" + getVictoryPoints() + "}";
  }
  
  @Override
  public int compareTo(Player other) {
	return this.equals(other) ? 0 : 1;
  }
}
