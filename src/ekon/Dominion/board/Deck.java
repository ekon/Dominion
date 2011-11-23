package ekon.dominion.board;

import java.util.Arrays;

import ekon.dominion.Card;
import ekon.dominion.GameException;
import ekon.dominion.GameException.Type;

class Deck {
	
	private final Card card;
	private final int initialCount;
	private int currentCount;
	
	public Deck(Card card, int initialCount) {
	  this.card = card;
	  this.initialCount = initialCount;
	  this.currentCount = initialCount;
	}
	
	public Card card() { return card; }
	public int initialCount() { return initialCount; }
	public int currentCount() { return currentCount; }

	public boolean isAvailable() { return currentCount > 0; }
	
	public void take() {
	  take(1);
	}
	
	public Card take(int numCards, boolean isNullCardOk) {
	  if (canTake(numCards)) {
		currentCount -= numCards;
		return card;
	  }
	  
	  if (isNullCardOk) {
		return null;
	  } else {
		throw new GameException(Type.CODE_ISSUE, "Tried to take " + numCards + " but only " + currentCount + " are available.");
	  }
	}
	
	public Card take(int numCards) {
	  return take(numCards, false);
	}
	
	private boolean canTake(int numToTake) {
	  return (currentCount >= numToTake);
	}
	
	public void put(int numCards) {
	  if (numCards <= 0) { throw new IllegalArgumentException("Cannot put " + numCards + " cards in deck of " + card.name()); }
	  
	  currentCount += numCards;
	  
	  // TODO(ekon): may want to check about currentCount > initialCount. Is that ever possible?
	}

	@Override
	public String toString() {
	  return "Deck [card=" + card + ", initialCount=" + initialCount + ", currentCount=" + currentCount + "]";
	}

	@Override
	public int hashCode() {
	  return Arrays.hashCode(new long[] { card.hashCode(), initialCount, currentCount });
	}

	@Override
	public boolean equals(Object obj) {
	  if (this == obj) return true;
	  if (obj == null) return false;
	  if (getClass() != obj.getClass()) return false;
	  Deck other = (Deck) obj;
	  if (card != other.card) return false;
	  if (currentCount != other.currentCount) return false;
	  if (initialCount != other.initialCount) return false;
	  return true;
	}
}
