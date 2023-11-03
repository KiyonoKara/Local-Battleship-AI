package kiyo.battleship.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Represents the Board data
 */
public class Board {
  public final char[][] grid;
  public final Map<Coord, Ship> shipLocations;
  public List<Ship> standingShips;

  /**
   * Board constructor
   *
   * @param height Height of board
   * @param width  Width of board
   */
  public Board(int height, int width, List<Ship> ships) {
    this.grid = new char[width][height];

    for (char[] chars : this.grid) {
      Arrays.fill(chars, '-');
    }

    shipLocations = new HashMap<>();
    for (Ship ship : ships) {
      for (Coord coord : ship.coords()) {
        shipLocations.put(coord, ship);
        this.grid[coord.x()][coord.y()] = ship.shipType().name().charAt(0);
      }
    }
    this.standingShips = ships;
  }

  /**
   * Sets the board's shots
   *
   * @param shots      The coordinates of the shots taken
   * @param impactType The type of impact, hit or miss, H / M
   */
  public void setShots(List<Coord> shots, Impact impactType) {
    if (impactType == Impact.HIT) {
      for (Coord coord : shots) {
        if (this.grid[coord.x()][coord.y()] != 'H') {
          this.grid[coord.x()][coord.y()] = impactType.name().charAt(0);
          this.shipLocations.remove(coord);
        }
      }
      this.standingShips = new HashSet<>(this.shipLocations.values()).stream().toList();
    }

    if (impactType == Impact.MISS) {
      for (Coord coord : shots) {
        if (this.grid[coord.x()][coord.y()] != 'H') {
          this.grid[coord.x()][coord.y()] = 'M';
        }
      }
    }
  }
}
