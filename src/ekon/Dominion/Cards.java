package ekon.dominion;

import ekon.dominion.Card.CardType;

import java.util.List;

public class Cards extends MyList<Card> {
  
  public Cards(Card... card) {
	super(card);
  }
  
  public Cards(List<Card> cardsToAdd) {
	super(cardsToAdd);
  }
  
  public Cards(Cards newCards) {
	this(newCards.items());
  }
  
  public boolean contains(CardType type) {
	for (Card card : this.asList()) {
	  if (card.types().contains(type)) { return true; }
	}
	return false;
  }
  
  public Cards getCards(CardType type) {
	Cards cardsToReturn = new Cards();
	for (Card card : this.asList()) {
	  if (card.types().contains(type)) {
		cardsToReturn.add(card);
	  }
	}
	return cardsToReturn;
  }
}
