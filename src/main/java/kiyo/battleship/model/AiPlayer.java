package kiyo.battleship.model;

import kiyo.battleship.view.BattleSalvoView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI player implementation
 */
public class AiPlayer extends AbstractPlayer {

  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  @Override
  public String name() {
    return "Player Ai";
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk.
   *
   * @return the locations of shots on the opponent's board
   */
  @Override
  public List<Coord> takeShots() {
    BattleSalvoView bsv = new BattleSalvoView();
    bsv.displayBoard("Ai Board Data:", super.board, true);

    Random random = new Random();
    int x;
    int y;
    int width = board.grid.length;
    int height = board.grid[0].length;
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
      x = random.nextInt(width);
      y = random.nextInt(height);
      while (this.alreadyTaken[x][y]) {
        x = random.nextInt(width);
        y = random.nextInt(height);
      }
      takenShots.add(new Coord(x, y));
      this.alreadyTaken[x][y] = true;
    }
    return takenShots;
  }
}