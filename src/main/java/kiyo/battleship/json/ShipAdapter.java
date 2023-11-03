package kiyo.battleship.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import kiyo.battleship.model.Coord;
import kiyo.battleship.model.Direction;
import kiyo.battleship.model.Ship;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * Serializable structure for ships
 */
public class ShipAdapter {
  private final Coord coord;
  private final int length;
  private final Direction direction;

  /**
   * Constructor for ShipAdapter by passing in a Ship
   *
   * @param ship The Ship
   */
  public ShipAdapter(Ship ship) {
    Direction direction =
        Arrays.stream(ship.coords()).allMatch(coord -> coord.y() == ship.coords()[0].y())
            ? Direction.HORIZONTAL : Direction.VERTICAL;
    Optional<Coord> start;
    if (direction == Direction.HORIZONTAL) {
      start = Arrays.stream(ship.coords()).max(Comparator.comparingInt(Coord::y));
    } else {
      start = Arrays.stream(ship.coords()).min(Comparator.comparingInt(Coord::x));
    }

    assert start.isPresent();
    this.coord = start.get();
    this.length = ship.coords().length;
    this.direction = direction;
  }

  /**
   * Constructor for ShipAdapter by passing in a ship's attributes
   *
   * @param coord     Starting coordinate
   * @param length    Length of ship (or size)
   * @param direction Direction of the ship
   */
  @JsonCreator
  public ShipAdapter(
      @JsonProperty("coord") Coord coord,
      @JsonProperty("length") int length,
      @JsonProperty("direction") Direction direction) {
    this.coord = coord;
    this.length = length;
    this.direction = direction;
  }

  /**
   * Gets the starting coordinate
   *
   * @return The starting coordinate
   */
  public Coord getCoord() {
    return this.coord;
  }

  /**
   * Gets the length of the ship
   *
   * @return The length of the ship
   */
  public int getLength() {
    return this.length;
  }

  /**
   * Gets the direction
   *
   * @return The direction of the ship
   */
  public Direction getDirection() {
    return this.direction;
  }
}
