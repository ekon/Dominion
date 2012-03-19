package ekon.dominion;

import junit.framework.TestCase;

import org.junit.Test;

import ekon.dominion.board.Board;

public class PlayerTest extends TestCase {
	Player player;
	Players opponents;
	
	@Override
	public void setUp() throws Exception {
	  	super.setUp();
		player = new Player("P1");
		Player opponent1 = new Player("P2");
		Player opponent2 = new Player("P3");
		opponents = new Players(opponent1, opponent2);
		
		Cards actionCards = new Cards();
		Board board = new Board(actionCards, 3);
		
		player.initPlayer(opponents, board);
		opponent1.initPlayer(new Players(player, opponent2), board);
		opponent2.initPlayer(new Players(player, opponent1), board);
	}
	
	@Test
	public void testTakeTurn() {
		
	}
}
