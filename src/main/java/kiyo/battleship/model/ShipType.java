package kiyo.battleship.model;

/**
 * Represents the valid types of ships with their sizes
 */
public enum ShipType {
  CARRIER(6),
  BATTLESHIP(5),
  DESTROYER(4),
  SUBMARINE(3);

  final int size;

  /**
   * Size for the ship type
   *
   * @param size The size as an integer
   */
  ShipType(int size) {
    this.size = size;
  }

  public int size() {
    return this.size;
  }
}
