package ekon.dominion.board;

import java.util.Objects;

import ekon.dominion.Card;
import ekon.dominion.Cards;
import ekon.dominion.SecurityUtil;
import ekon.dominion.UIUtil;

public class Trash {
	private Cards trashedCards;
	private UIUtil uiUtil = UIUtil.getUIUtil();
	
	public Trash(Card card) {
	  SecurityUtil securityUtil = new SecurityUtil();
	  securityUtil.verifyCallingClassIsTest();
	  trashedCards = new Cards(card);
	}
	
	public Trash(Card... cards) {
	  SecurityUtil securityUtil = new SecurityUtil();
	  securityUtil.verifyCallingClassIsTest();
	  trashedCards = new Cards(cards);
	}
	
	public Trash(Trash trash) {
	  SecurityUtil securityUtil = new SecurityUtil();
	  securityUtil.verifyCallingClassIsTest();
	  this.trashedCards = new Cards(trash.cards());
	}
	
	public Trash() {
	  trashedCards = new Cards();
	}
	
	public Cards cards() { return trashedCards; }
	public int size() { return trashedCards.size(); }
	public boolean isEmpty() { return (size() == 0); }
	
	public void add(Card card) {
	  trashedCards.add(card);
	  uiUtil.tellUser("	Trashed card:" + card.name());
	}
	
	public void add(Cards cards) {
	  trashedCards.add(cards);
	  uiUtil.tellUser("	Trashed cards: " + cards.toString());
	}
	
	@Override
	public int hashCode() {
	  return Objects.hashCode(trashedCards);
	}
	
	@Override
	public boolean equals(Object obj) {
	  if ((obj != null) && (obj.getClass().equals(this.getClass()))) {
		Trash otherTrash = (Trash)obj;
		return trashedCards.equals(otherTrash.cards());
	  }
	  return false;
	}
	
	@Override
	public String toString() {
	  return "Cards in trash: " + trashedCards.toString();
	}
}