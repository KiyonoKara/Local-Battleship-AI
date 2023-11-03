package kiyo.battleship.model;

import kiyo.battleship.view.BattleSalvoView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Abstraction of the Player implementation
 */
public abstract class AbstractPlayer implements Player {
  private final Random random;
  protected Board board;
  protected boolean[][] alreadyTaken;
  protected List<Coord> coordsToShootAtFirst;
  protected List<Coord> coordsToShootAt;
  protected List<Coord> previouslyShotAt;
  protected List<Coord> shootableCoords;
  private boolean doNormal;
  private int maxIterations = 900;

  /**
   * Constructor for our abstract player
   */
  public AbstractPlayer() {
    this.random = new Random();
    this.coordsToShootAt = new ArrayList<>();
    this.coordsToShootAtFirst = new ArrayList<>();
    this.previouslyShotAt = new ArrayList<>();
    this.shootableCoords = new ArrayList<>();
    this.doNormal = false;
  }

  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  @Override
  public abstract String name();

  /**
   * Given the specifications for a BattleSalvo board, return a list of ships with their locations
   * on the board.
   *
   * @param height         the height of the board, range: [6, 15] inclusive
   * @param width          the width of the board, range: [6, 15] inclusive
   * @param specifications a map of ship type to the number of occurrences each ship should
   *                       appear on the board
   * @return the placements of each ship on the board
   */
  @Override
  public List<Ship> setup(int height, int width, Map<ShipType, Integer> specifications) {
    List<Ship> fleet = new ArrayList<>();
    System.out.println(height);
    System.out.println(width);
    System.out.println(specifications);

    final ShipType carrier = ShipType.CARRIER;
    final ShipType battleship = ShipType.BATTLESHIP;
    final ShipType destroyer = ShipType.DESTROYER;
    final ShipType submarine = ShipType.SUBMARINE;
    int numOfTilesOccupied = specifications.get(carrier) * (carrier.size * 2 + 2)
        + specifications.get(battleship) * (battleship.size * 2 + 2)
        + specifications.get(destroyer) * (destroyer.size * 2 + 2)
        + specifications.get(submarine) * (submarine.size * 2 + 2);
    if (((numOfTilesOccupied / (height * width)) * 100) > 74) {
      this.doNormal = true;
      System.out.println("True Time");
    }
    System.out.println("Tiles occupied " + numOfTilesOccupied);
    System.out.println("area " + height * width);
    for (ShipType st : specifications.keySet()) {
      int size = st.size();
      for (int i = 0; i < specifications.get(st); i++) {
        Coord[] coords = generateShipCoords(height, width, size);
        while (overlaps(fleet, coords)) {
          coords = generateShipCoords(height, width, size);
        }
        fleet.add(new Ship(st, coords));
      }
    }
    this.board = new Board(height, width, fleet);
    this.alreadyTaken = new boolean[width][height];
    this.createShootableCoords(height, width);
    BattleSalvoView bsv = new BattleSalvoView();
    bsv.displayBoard("", this.board, false);
    for (boolean[] bool : this.alreadyTaken) {
      Arrays.fill(bool, false);
    }
    return fleet;
  }

  /**
   * Creates a lists of every other coord on the board
   *
   * @param height the height of the board
   * @param width  the width of the board
   */
  private void createShootableCoords(int height, int width) {
    boolean placeCoord;
    int alternate = 1;
    for (int i = 0; i < height; i++) {
      placeCoord = alternate % 2 == 1;
      alternate++;
      for (int j = 0; j < width; j++) {
        if (placeCoord) {
          shootableCoords.add(new Coord(j, i));
          placeCoord = false;
        } else {
          placeCoord = true;
        }
      }
    }
    placeMiddleDiagonalFirst(height, width);
  }

  private void placeMiddleDiagonalFirst(int height, int width) {
    int middleX = height / 2;
    int middleY = width / 2;
    int celingCounter = middleX;
    int wallCounter = middleY;
    while (celingCounter > 0 && wallCounter > 0) {
      coordsToShootAtFirst.add(new Coord(wallCounter, celingCounter));
      celingCounter--;
      wallCounter--;
    }
    int celingCounterV2 = middleX;
    int wallCounterV2 = middleY;
    while (wallCounterV2 > board.grid.length - 1 && celingCounterV2 > board.grid[0].length - 1) {
      coordsToShootAtFirst.add(new Coord(wallCounterV2, celingCounterV2));
      celingCounterV2--;
      wallCounterV2--;
    }
  }

  /**
   * Generates coordinates within the game's bounds for a single ship
   *
   * @param height Board height
   * @param width  Board width
   * @param size   Ship size
   * @return Fixed array of coords
   */
  private Coord[] generateShipCoords(int height, int width, int size) {
    Coord[] coords = new Coord[size];
    int x;
    int y;

    // True = Vertical
    // False = Horizontal
    boolean orientation = this.random.nextBoolean();

    // Vertical
    if (orientation) {
      x = this.random.nextInt(width);
      y = this.random.nextInt(height - size + 1);
      for (int i = 0; i < size; i++) {
        coords[i] = new Coord(x, y + i);
      }
      // Horizontal
    } else {
      x = this.random.nextInt(width - size + 1);
      y = this.random.nextInt(height);
      for (int i = 0; i < size; i++) {
        coords[i] = new Coord(x + i, y);
      }
    }
    return coords;
  }

  /**
   * Checks if coordinates overlap with other ships' coordinates
   *
   * @param ships  List of ships
   * @param coords Fixed array of coordinates
   * @return Whether there is an overlap or not
   */
  private boolean overlaps(List<Ship> ships, Coord[] coords) {
    for (Ship s : ships) {
      for (Coord shipCoords : s.coords()) {
        for (Coord coord : coords) {

          if (!this.doNormal && maxIterations > 0) {
            if ((shipCoords.x() + 1 == coord.x()
                && shipCoords.y() + 1 == coord.y())

                || (shipCoords.y() - 1 == coord.y()
                && shipCoords.x() - 1 == coord.x())

                || (shipCoords.x() + 1 == coord.x()
                && shipCoords.y() - 1 == coord.y())

                || (shipCoords.y() + 1 == coord.y()
                && shipCoords.x() - 1 == coord.x())

                || (shipCoords.x() + 1 == coord.x()
                && shipCoords.y() == coord.y())

                || (shipCoords.y() + 1 == coord.y()
                && shipCoords.x() == coord.x())

                || (shipCoords.x() - 1 == coord.x()
                && shipCoords.y() == coord.y())

                || (shipCoords.y() - 1 == coord.y()
                && shipCoords.x() == coord.x())) {
              return true;
            }
            maxIterations--;
          }
          if (shipCoords.x() == coord.x() && shipCoords.y() == coord.y()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk.
   *
   * @return the locations of shots on the opponent's board
   */
  @Override
  public abstract List<Coord> takeShots();

  /**
   * Given the list of shots the opponent has fired on this player's board, report which
   * shots hit a ship on this player's board.
   *
   * @param opponentShotsOnBoard the opponent's shots on this player's board
   * @return a filtered list of the given shots that contain all locations of shots that hit a
   * ship on this board
   */
  @Override
  public List<Coord> reportDamage(List<Coord> opponentShotsOnBoard) {
    // Hits
    List<Coord> hits = new ArrayList<>();
    // Hits that affect the player
    List<Coord> myHits = new ArrayList<>();
    // Misses that affect the player
    List<Coord> misses = new ArrayList<>();

    for (Coord shipCoord : this.board.shipLocations.keySet()) {
      for (Coord oppCoord : opponentShotsOnBoard) {
        if (shipCoord.x() == oppCoord.x() && shipCoord.y() == oppCoord.y()) {
          myHits.add(oppCoord);
          hits.add(shipCoord);
        } else {
          misses.add(oppCoord);
        }
      }
    }

    this.board.setShots(myHits, Impact.HIT);
    this.board.setShots(misses, Impact.MISS);

    return hits;
  }

  /**
   * Reports to this player what shots in their previous volley returned from takeShots()
   * successfully hit an opponent's ship. (This method isn't needed, so it is empty)
   *
   * @param shotsThatHitOpponentShips the list of shots that successfully hit the opponent's ships
   */
  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    for (Coord c : shotsThatHitOpponentShips) {
      boolean hitNothingAround = true;
      previouslyShotAt.add(c);
      if (previouslyShotAt.contains(new Coord(c.x() - 1, c.y()))) {
        if (c.x() < board.grid.length - 1) {
          coordsToShootAt.add(new Coord(c.x() + 1, c.y()));
        }
        hitNothingAround = false;
      }
      if (previouslyShotAt.contains(new Coord(c.x() + 1, c.y()))) {
        if (c.x() > 0) {
          coordsToShootAt.add(new Coord(c.x() - 1, c.y()));
        }
        hitNothingAround = false;
      }
      if (previouslyShotAt.contains(new Coord(c.x(), c.y() - 1))) {
        if (c.y() < board.grid[0].length - 1) {
          coordsToShootAt.add(new Coord(c.x(), c.y() + 1));
        }
        hitNothingAround = false;
      }
      if (previouslyShotAt.contains(new Coord(c.x(), c.y() + 1))) {
        if (c.y() > 0) {
          coordsToShootAt.add(new Coord(c.x(), c.y() - 1));
        }
        hitNothingAround = false;
      }
      if (hitNothingAround) {
        hitEverythingAround(c);
      }
    }
    Set<Coord> uniqueSet = new HashSet<>(coordsToShootAt);
    coordsToShootAt = new ArrayList<>(uniqueSet);
  }

  private void hitEverythingAround(Coord c) {
    if (c.x() > 0) {
      coordsToShootAt.add(new Coord(c.x() - 1, c.y()));
    }
    if (c.x() < board.grid.length - 1) {
      coordsToShootAt.add(new Coord(c.x() + 1, c.y()));
    }
    if (c.y() > 0) {
      coordsToShootAt.add(new Coord(c.x(), c.y() - 1));
    }
    if (c.y() < board.grid[0].length - 1) {
      coordsToShootAt.add(new Coord(c.x(), c.y() + 1));
    }
  }


  /**
   * Notifies the player that the game is over.
   * Win, lose, and draw should all be supported
   *
   * @param result if the player has won, lost, or forced a draw
   * @param reason the reason for the game ending
   */
  @Override
  public void endGame(GameResult result, String reason) {
    BattleSalvoView bsv = new BattleSalvoView();
    switch (result) {
      case WIN -> bsv.showSuccess(this.name() + " wins the game!");
      case LOSE -> bsv.showFailure(this.name() + " lost...");
      default -> bsv.showText(this.name() + " had a draw.");
    }
    bsv.showText(reason);
  }
}
