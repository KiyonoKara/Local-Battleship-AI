package kiyo.battleship.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the Board from the model
 */
class BoardTest {
  ArrayList<Ship> ships;
  Board testBoard;

  /**
   * Set up default data and board
   */
  @BeforeEach
  void setUp() {
    ships = new ArrayList<>();
    ships.add(new Ship(ShipType.CARRIER,
        new Coord[] {
            new Coord(0, 0),
            new Coord(1, 0),
            new Coord(2, 0),
            new Coord(3, 0),
            new Coord(4, 0),
            new Coord(5, 0)}));
    ships.add(new Ship(ShipType.BATTLESHIP,
        new Coord[] {
            new Coord(0, 1),
            new Coord(1, 1),
            new Coord(2, 1),
            new Coord(3, 1),
            new Coord(4, 1)}));
    ships.add(new Ship(ShipType.DESTROYER,
        new Coord[] {
            new Coord(2, 2),
            new Coord(3, 2),
            new Coord(4, 2),
            new Coord(5, 2)}));
    ships.add(new Ship(ShipType.SUBMARINE,
        new Coord[] {
            new Coord(2, 3),
            new Coord(3, 3),
            new Coord(4, 3)}));
    ships.add(new Ship(ShipType.SUBMARINE,
        new Coord[] {
            new Coord(3, 4),
            new Coord(4, 4),
            new Coord(5, 4)}));
    ships.add(new Ship(ShipType.SUBMARINE,
        new Coord[] {
            new Coord(2, 4),
            new Coord(3, 4),
            new Coord(4, 4)}));
    testBoard = new Board(6, 6, ships);
  }

  /**
   * Tests the HIT enum for the setShots method
   */
  @Test
  void testSetShotsHit() {
    // Test different ships beforehand
    assertEquals(testBoard.grid[0][0], 'C');
    assertEquals(testBoard.grid[0][1], 'B');
    assertEquals(testBoard.grid[2][2], 'D');
    assertEquals(testBoard.grid[2][3], 'S');
    assertEquals(testBoard.grid[3][4], 'S');
    assertEquals(testBoard.grid[2][4], 'S');

    // Test shots
    ArrayList<Coord> shots = new ArrayList<>();
    shots.add(new Coord(0, 0));
    shots.add(new Coord(0, 1));
    shots.add(new Coord(2, 2));
    shots.add(new Coord(2, 3));
    shots.add(new Coord(3, 4));
    shots.add(new Coord(2, 4));

    testBoard.setShots(shots, Impact.HIT);

    // Test all HIT
    assertEquals(testBoard.grid[0][0], 'H');
    assertEquals(testBoard.grid[0][1], 'H');
    assertEquals(testBoard.grid[2][2], 'H');
    assertEquals(testBoard.grid[2][3], 'H');
    assertEquals(testBoard.grid[3][4], 'H');
    assertEquals(testBoard.grid[2][4], 'H');
  }

  /**
   * Tests the MISS enum for the setShots method
   */
  @Test
  void testSetShotsMiss() {
    // Test setting for MISS
    assertEquals(testBoard.grid[0][2], '-');
    assertEquals(testBoard.grid[0][3], '-');
    assertEquals(testBoard.grid[0][4], '-');
    assertEquals(testBoard.grid[2][5], '-');
    assertEquals(testBoard.grid[3][5], '-');
    assertEquals(testBoard.grid[4][5], '-');

    ArrayList<Coord> shots = new ArrayList<>();
    shots.add(new Coord(0, 2));
    shots.add(new Coord(0, 3));
    shots.add(new Coord(0, 4));
    shots.add(new Coord(2, 5));
    shots.add(new Coord(3, 5));
    shots.add(new Coord(4, 5));

    testBoard.setShots(shots, Impact.MISS);

    // Test for all MISS
    assertEquals(testBoard.grid[0][2], 'M');
    assertEquals(testBoard.grid[0][3], 'M');
    assertEquals(testBoard.grid[0][4], 'M');
    assertEquals(testBoard.grid[2][5], 'M');
    assertEquals(testBoard.grid[3][5], 'M');
    assertEquals(testBoard.grid[4][5], 'M');
  }

  /**
   * Tests expected errors of setShots
   */
  @Test
  void testSetShotsErrors() {
    // Test wrong enums
    assertThrows(IllegalArgumentException.class,
        () -> testBoard.setShots(new ArrayList<>(), Impact.valueOf("hit")));
    assertThrows(IllegalArgumentException.class,
        () -> testBoard.setShots(new ArrayList<>(), Impact.valueOf("miss")));
  }

  /**
   * Test the setShots method that uses the whole board
   */
  @Test
  void testSetShots() {
    // Test overlapping shots
    ArrayList<Coord> shots = new ArrayList<>();
    shots.add(new Coord(0, 0));
    shots.add(new Coord(0, 1));
    shots.add(new Coord(2, 2));
    shots.add(new Coord(2, 3));
    shots.add(new Coord(3, 4));
    shots.add(new Coord(2, 4));

    // Test other kinds of characters
    testBoard.grid[0][0] = 'D';
    testBoard.grid[2][2] = 'C';
    testBoard.grid[2][3] = 'H';

    testBoard.setShots(shots, Impact.HIT);

    assertEquals(testBoard.grid[0][0], 'H');
    assertEquals(testBoard.grid[0][1], 'H');
    assertEquals(testBoard.grid[2][2], 'H');
    assertEquals(testBoard.grid[2][3], 'H');
    assertEquals(testBoard.grid[3][4], 'H');
    assertEquals(testBoard.grid[2][4], 'H');

    // Test with MISS for other characters
    // Test other kinds of characters
    testBoard.grid[0][2] = 'H';
    testBoard.grid[2][5] = 'H';
    testBoard.grid[4][5] = 'H';

    shots.clear();
    shots.add(new Coord(0, 2));
    shots.add(new Coord(0, 3));
    shots.add(new Coord(0, 4));
    shots.add(new Coord(2, 5));
    shots.add(new Coord(3, 5));
    shots.add(new Coord(4, 5));

    testBoard.setShots(shots, Impact.MISS);

    assertEquals(testBoard.grid[0][2], 'H');
    assertEquals(testBoard.grid[0][3], 'M');
    assertEquals(testBoard.grid[0][4], 'M');
    assertEquals(testBoard.grid[2][5], 'H');
    assertEquals(testBoard.grid[3][5], 'M');
    assertEquals(testBoard.grid[4][5], 'H');
  }
}