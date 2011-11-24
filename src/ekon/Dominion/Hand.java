package ekon.dominion;

import java.util.Stack;

import junit.framework.Assert;
import ekon.dominion.Card.CardType;

public class Hand {

	private Cards cards;
	private Stack<Card> actionsInPlay;
	
	public Hand(Cards cards) {
		Assert.assertNotNull(cards);
		Assert.assertEquals("Hand should be initialized with only 5 cards.", 5, cards.size());
		this.cards = cards;
		this.actionsInPlay = new Stack<Card>();
	}
	
	public Cards cards() { return cards; }
	
	public Stack<Card> actionsInPlay() { return actionsInPlay; }
	public void playAction(Card card) { actionsInPlay.push(card); }
	public void clearActionsInPlay() { actionsInPlay = new Stack<Card>(); }
	
	// TODO(ekon): Feels weird to have both getAvailableCards and get cards. Seems error-prone.
	public Cards availableCards() {
		Cards availableCards = new Cards(cards);
		if (actionsInPlay != null) {
			availableCards.remove(actionsInPlay);
		}
		return availableCards;
	}
	
	public boolean contains(Card card) {
		return cards.contains(card);
	}
	
	public Cards getCards(CardType type) {
		return cards.getCards(type);
	}
	
	public boolean contains(CardType type) {
		return cards.contains(type);
	}
	
	public boolean hasActionCards() {
		for (Card card : cards) {
			if (card.types().contains(CardType.ACTION)) {
				return true;
			}
		}
		return false;
	}
	
	public void remove(Cards cardsToRemove) throws GameException {
		// Note: not using removeAll, because want error checking to ensure each removed card was actually in the hand before.
		for (Card card : cardsToRemove) {
			remove(card);
		}
	}
	
	public void remove(Card card) throws GameException {
		if (!cards.remove(card)) {
			throw new GameException(GameException.Type.INVALID_MOVE, "Card " + card.name() + " is not hand to remove.");
		}
	}
	
	public void add(Cards cardsToAdd) {
		cards.add(cardsToAdd);
	}
	
	public void add(Card card) {
		cards.add(card);
	}

	@Override
	public String toString() {
		return cards.toString();
	}
	
	public void initForTesting(Cards cards) {
		SecurityUtil securityUtil = new SecurityUtil();
		securityUtil.verifyCallingClassIsTest();
		
		this.cards = cards;
	}
}
