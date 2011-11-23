package ekon.dominion;

// @formatter:off
import static ekon.dominion.Card.CardType.ACTION;
import ekon.dominion.board.Board;

// @formatter:on

public class Game {
  
  private Board board;
  private Players players;
  
  public Game(Players players) {
	this.players = new Players(players);
	initBoardCards();
	players.initPlayers(board);
  }
  
  public Board getBoardCards() {
	return board;
  }
  
  public void runGame() {
	// TODO(ekon): If previous game exists, pick the next player.
	players.shuffle();
	
	while (isGameOver()) {
	  for (Player player : players) {
		player.takeTurn();
	  }
	}
  }
  
  private boolean isGameOver() {
	// Game is over if Province card (or Colony are gone)
	int proviceCardCount = board.getCardCount(Card.PROVINCE);
	int colonyCardCount = 0;
	if (board.hasCard(Card.COLONY)) {
	  colonyCardCount = board.getCardCount(Card.COLONY);
	}
	
	if ((proviceCardCount == 0) || (colonyCardCount == 0) || areThreeDecksOut()) { return true; }
	return false;
  }
  
  private boolean areThreeDecksOut() {
	// Get card count for each card in the game.
	// It's probably better to do this with listeners, so that board cards can send an even to game,
	// letting it know
	// each time a deck or province/colony card is out.
	if (board.getDepletedCards().size() >= 3) { return true; }
	return false;
  }
  
  private void initBoardCards() {
	Cards actionCards = getActionCards();
	board = new Board(actionCards, players.size());
  }
  
  private Cards getActionCards() {
	Cards actionCards = new Cards();
	
	// TODO(ekon): Get actionCards
	// pick the first 10 action cards for now.
	for (Card card : Card.values()) {
	  if (card.types().contains(ACTION)) {
		actionCards.add(card);
	  }
	}
	
	return actionCards;
  }
  
  public static void main(String[] args) {
	Player player1 = new Player("Player1");
	Player player2 = new Player("Player2");
	Game game = new Game(new Players(player1, player2));
	
	try {
	  game.runGame();
	} catch (GameException e) {
	  System.out.println(e.getMessage());
	}
  }

  @Override
  public String toString() {
	return "Game [board=" + board + ", players=" + players + "]";
  }
}
