package ekon.dominion;

import junit.framework.Assert;
import ekon.dominion.Card.CardType;

public class Hand {

	private Cards cards;
	private Cards actionsInPlay;
	
	public Hand(Cards cards) {
		Assert.assertNotNull(cards);
		Assert.assertEquals("Hand should be initialized with only 5 cards.", 5, cards.size());
		this.cards = cards;
		this.actionsInPlay = new Cards();
	}
	
	public Cards cards() { return cards; }
	
	public Cards actionsInPlay() { return actionsInPlay; }
	public void playAction(Card card) { actionsInPlay.add(card); }
	public void clearActionsInPlay() { actionsInPlay = new Cards(); }
	
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
	
	/** Be careful with using this. Make sure that it's on availableCards in hand, if need be, rather than just on hand itself. */
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
