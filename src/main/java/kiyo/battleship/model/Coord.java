package kiyo.battleship.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a coordinate
 *
 * @param x The x-coordinate, represents horizontal
 * @param y The y-coordinate represents vertical
 */
public record Coord(int x, int y) {
  @JsonCreator
  public Coord(@JsonProperty("x") int x,
               @JsonProperty("y") int y) {
    this.x = x;
    this.y = y;
  }

}