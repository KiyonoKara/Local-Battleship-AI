package kiyo.battleship;

import kiyo.battleship.model.AbstractPlayer;
import kiyo.battleship.model.Coord;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock Ai player for testing the game and controller
 */
public class MockAiPlayer extends AbstractPlayer {

  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  @Override
  public String name() {
    return "Mock Ai";
  }

  /**
   * Mock version of takeShots, testing for data
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk.
   *
   * @return the locations of shots on the opponent's board
   */
  @Override
  public List<Coord> takeShots() {
    List<Coord> takenShots = new ArrayList<>();

    int maxAllowed = 0;
    for (boolean[] bool : super.alreadyTaken) {
      for (boolean b : bool) {
        if (!b) {
          maxAllowed++;
        }
      }
    }

    for (int i = 0; i < Math.min(board.standingShips.size(), maxAllowed); i++) {
      takenShots.add(new Coord(i, i));
    }

    for (Coord coord : takenShots) {
      this.alreadyTaken[coord.x()][coord.y()] = true;
    }

    return takenShots;
  }
}
