package kiyo.battleship.model;

import kiyo.battleship.view.BattleSalvoView;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual player implementation
 */
public class ManualPlayer extends AbstractPlayer {
  InputStream in;

  public ManualPlayer() {
    this(System.in);
  }

  /**
   * For input streams
   *
   * @param in InputStream
   */
  ManualPlayer(InputStream in) {
    this.in = in;
  }



  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  @Override
  public String name() {
    return "Player Manual";
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk.
   *
   * @return the locations of shots on the opponent's board
   */
  @Override
  public List<Coord> takeShots() {
    BattleSalvoView bsv = new BattleSalvoView(this.in);
    if (super.board.standingShips.size() == 0) {
      return new ArrayList<>();
    }

    int maxAllowed = 0;
    for (boolean[] bool : super.alreadyTaken) {
      for (boolean b : bool) {
        if (!b) {
          maxAllowed++;
        }
      }
    }

    bsv.displayBoard("My Board Data:", super.board, false);
    List<Coord> takenShots = bsv.promptCoords("Enter the coordinates for your shots.",
        super.board, maxAllowed);

    for (Coord coord : takenShots) {
      this.alreadyTaken[coord.x()][coord.y()] = true;
    }

    return takenShots;
  }
}
