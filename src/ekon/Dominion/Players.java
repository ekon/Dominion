package ekon.dominion;

import java.util.ArrayList;
import java.util.List;

import ekon.dominion.board.Board;

public class Players extends MyList<Player> {
  
  // Players need to have unique names.
  List<String> names = new ArrayList<String>();
  
  public Players(Player... player) {
	super(player);
	
	for (Player p : player) {
	  verifyNameUniqueness(p.name());
	}
  }
  
  public Players(List<Player> playersToAdd) {
	super(playersToAdd);
	
	for (Player player : playersToAdd) {
	  verifyNameUniqueness(player.name());
	}
  }
  
  public Players(Players newPlayers) {
	this(newPlayers.items());
  }
  
  public Players(int initialCapacity) {
	super(initialCapacity);
  }
  
  public void initPlayers(Board board) {
	for (Player player : this.asList()) {
	  Players opponents = new Players();
	  for (Player opponent : this.asList()) {
		// TOOD(ekon): PLAYER CAN'T HAVE THE SAME NAME!!!
		if (!player.equals(opponent)) {
		  opponents.add(opponent);
		}
	  }
	  player.initPlayer(opponents, board);
	}
  }
  
  private void verifyNameUniqueness(String name) {
	if (names.contains(name)) {
	  throw new GameException(GameException.Type.TO_REMOVE,	name + " already been registered. Players must have unique names.");
	}
	names.add(name);
  }
}
