package kiyo.battleship.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for all members of Player and its abstraction
 */
class PlayerTest {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_BLUE = "\033[38;5;39m";
  private static final String ANSI_GREEN = "\u001B[38;5;36m";
  private static final String ANSI_RED = "\u001B[31m";
  //private static final String ANSI_YELLOW = "\033[0;33m";

  ByteArrayOutputStream out;

  Player mp;
  Player ai;
  Player bai;

  /**
   * Sets the players and streams to defaults
   */
  @BeforeEach
  void setUp() {
    mp = new ManualPlayer();
    ai = new AiPlayer();
    bai = new BetterAiPlayer();
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
  }

  /**
   * Resets the streams
   */
  @AfterEach
  void resetAfter() {
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
  }

  /**
   * Tests the name function of all player implementations
   */
  @Test
  void testName() {
    assertEquals("Player Manual", mp.name());
    assertEquals("PLAYER MANUAL", mp.name().toUpperCase());

    assertEquals("Player Ai", ai.name());
    assertEquals("PLAYER AI", ai.name().toUpperCase());

    assertEquals("Better AI Player", bai.name());
    assertEquals("BETTER AI PLAYER", bai.name().toUpperCase());
  }

  /**
   * Tests the takeShots method
   */
  @Test
  void testTakeShotsManual() {
    // Test coord prompt input
    String coordsInput = """
        0 0
        0 1
        0 2
        0 3
        """;
    InputStream inputStream = new ByteArrayInputStream(coordsInput.getBytes());
    mp = new ManualPlayer(inputStream);

    // Mock specifications of ships
    HashMap<ShipType, Integer> specs = new HashMap<>();
    specs.put(ShipType.CARRIER, 1);
    specs.put(ShipType.BATTLESHIP, 1);
    specs.put(ShipType.DESTROYER, 1);
    specs.put(ShipType.SUBMARINE, 1);

    mp.setup(8, 8, specs);
    List<Coord> takenShots = mp.takeShots();
    assertTrue(out.toString().contains(
        ANSI_BLUE + "Enter the coordinates for your shots." + ANSI_RESET + System.lineSeparator()
            + "You have 4 available shots." + System.lineSeparator()));
    assertEquals(4, takenShots.size());
    out.reset();

    mp.setup(8, 8, new HashMap<>());
    takenShots = mp.takeShots();
    assertEquals(0, takenShots.size());
    out.reset();

    // Test all occupied spots
    inputStream = new ByteArrayInputStream(coordsInput.getBytes());
    AbstractPlayer p1 = new ManualPlayer(inputStream);
    p1.setup(8, 8, specs);
    for (int i = 0; i < p1.alreadyTaken.length; i++) {
      Arrays.fill(p1.alreadyTaken[i], true);
    }

    takenShots = p1.takeShots();
    assertTrue(out.toString().contains(
        ANSI_BLUE + "Enter the coordinates for your shots." + ANSI_RESET + System.lineSeparator()
            + "You have 0 available shots." + System.lineSeparator()));
    assertEquals(0, takenShots.size());
    out.reset();

    AbstractPlayer p2 = new AiPlayer();
    p2.setup(8, 8, specs);
    for (int i = 0; i < p2.alreadyTaken.length; i++) {
      Arrays.fill(p2.alreadyTaken[i], true);
    }

    takenShots = p2.takeShots();
    assertEquals(new ArrayList<Coord>(), takenShots);
  }

  /**
   * Test the Ai player's takeShots method
   */
  @Test
  void testTakeShotsAi() {
    // Mock specifications of ships
    HashMap<ShipType, Integer> specs = new HashMap<>();
    specs.put(ShipType.CARRIER, 1);
    specs.put(ShipType.BATTLESHIP, 1);
    specs.put(ShipType.DESTROYER, 1);
    specs.put(ShipType.SUBMARINE, 1);
    ai.setup(8, 8, specs);
    List<Coord> takenShots = ai.takeShots();
    String boardData = "Ai Board Data:"
        + System.lineSeparator() + "- - - - - - - -"
        + System.lineSeparator() + "- - - - - - - -"
        + System.lineSeparator() + "- - - - - - - -"
        + System.lineSeparator() + "- - - - - - - -"
        + System.lineSeparator() + "- - - - - - - -"
        + System.lineSeparator() + "- - - - - - - -"
        + System.lineSeparator() + "- - - - - - - -"
        + System.lineSeparator() + "- - - - - - - -"
        + System.lineSeparator() + System.lineSeparator();

    assertEquals(boardData, out.toString());
    assertTrue(takenShots.size() > 0);
    out.reset();

    // Test none
    ai.setup(8, 8, new HashMap<>());
    takenShots = ai.takeShots();
    assertEquals(0, takenShots.size());

    specs.put(ShipType.CARRIER, 2);
    specs.put(ShipType.BATTLESHIP, 5);
    specs.put(ShipType.DESTROYER, 5);
    specs.put(ShipType.SUBMARINE, 3);

    // Test large
    ai.setup(15, 15, specs);
    takenShots = ai.takeShots();
    assertEquals(15, takenShots.size());
    for (int i = 0; i < 14; i++) {
      takenShots = ai.takeShots();
      assertTrue(takenShots.size() > 0);
    }
  }

  /**
   * Tests the setup function
   */
  @Test
  void testSetup() {
    // Test filled specifications
    HashMap<ShipType, Integer> specs = new HashMap<>();
    specs.put(ShipType.CARRIER, 1);
    specs.put(ShipType.BATTLESHIP, 1);
    specs.put(ShipType.DESTROYER, 1);
    specs.put(ShipType.SUBMARINE, 1);

    // Test with manual
    List<Ship> ships = mp.setup(8, 8, specs);
    ships.sort(Comparator.comparingInt(s -> s.shipType().ordinal()));
    assertEquals(ShipType.CARRIER, ships.get(0).shipType());
    assertEquals(ShipType.BATTLESHIP, ships.get(1).shipType());
    assertEquals(ShipType.DESTROYER, ships.get(2).shipType());
    assertEquals(ShipType.SUBMARINE, ships.get(3).shipType());

    // Test with Ai player as well
    Player ai = new AiPlayer();
    ships = ai.setup(8, 8, specs);
    ships.sort(Comparator.comparingInt(s -> s.shipType().ordinal()));
    assertEquals(ShipType.CARRIER, ships.get(0).shipType());
    assertEquals(ShipType.BATTLESHIP, ships.get(1).shipType());
    assertEquals(ShipType.DESTROYER, ships.get(2).shipType());
    assertEquals(ShipType.SUBMARINE, ships.get(3).shipType());

    // Test empty specifications
    specs.clear();
    ships = mp.setup(8, 8, specs);
    assertEquals(0, ships.size());
  }

  /**
   * Tests the endGame method
   */
  @Test
  void testEndGame() {
    mp.endGame(GameResult.WIN, "Win");
    assertEquals(ANSI_GREEN + "Player Manual wins the game!" + ANSI_RESET
        + System.lineSeparator() + "Win" + System.lineSeparator(), out.toString());
    out.reset();

    mp.endGame(GameResult.LOSE, "Lose");
    assertEquals(ANSI_RED + "Player Manual lost..." + ANSI_RESET
        + System.lineSeparator() + "Lose" + System.lineSeparator(), out.toString());
    out.reset();

    mp.endGame(GameResult.DRAW, "Draw");
    assertEquals("Player Manual had a draw."
        + System.lineSeparator() + "Draw" + System.lineSeparator(), out.toString());
    out.reset();

    ai.endGame(GameResult.WIN, "Win");
    assertEquals(ANSI_GREEN + "Player Ai wins the game!" + ANSI_RESET
        + System.lineSeparator() + "Win" + System.lineSeparator(), out.toString());
    out.reset();

    ai.endGame(GameResult.LOSE, "Lose");
    assertEquals(ANSI_RED + "Player Ai lost..." + ANSI_RESET
        + System.lineSeparator() + "Lose" + System.lineSeparator(), out.toString());
    out.reset();

    ai.endGame(GameResult.DRAW, "Draw");
    assertEquals("Player Ai had a draw."
        + System.lineSeparator() + "Draw" + System.lineSeparator(), out.toString());
    out.reset();
  }

  /**
   * Tests reportDamage
   */
  @Test
  void testReportDamage() {
    ArrayList<Ship> ships1 = new ArrayList<>();
    ships1.add(new Ship(ShipType.CARRIER,
        new Coord[] {
            new Coord(0, 0),
            new Coord(1, 0),
            new Coord(2, 0),
            new Coord(3, 0),
            new Coord(4, 0),
            new Coord(5, 0)}));
    ships1.add(new Ship(ShipType.BATTLESHIP,
        new Coord[] {
            new Coord(0, 1),
            new Coord(1, 1),
            new Coord(2, 1),
            new Coord(3, 1),
            new Coord(4, 1)}));
    ships1.add(new Ship(ShipType.DESTROYER,
        new Coord[] {
            new Coord(2, 2),
            new Coord(3, 2),
            new Coord(4, 2),
            new Coord(5, 2)}));
    ships1.add(new Ship(ShipType.SUBMARINE,
        new Coord[] {
            new Coord(2, 3),
            new Coord(3, 3),
            new Coord(4, 3)}));

    AbstractPlayer p1 = new ManualPlayer();
    p1.board = new Board(6, 6, ships1);

    ArrayList<Coord> shots = new ArrayList<>();
    // Hit
    shots.add(new Coord(0, 1));
    // Miss
    shots.add(new Coord(5, 5));
    // Hit
    shots.add(new Coord(1, 1));
    // Miss
    shots.add(new Coord(4, 4));
    // Hit
    shots.add(new Coord(2, 3));
    // Miss
    shots.add(new Coord(4, 5));
    // Test with successful and missed shots
    ArrayList<Coord> damage = new ArrayList<>(p1.reportDamage(shots));
    damage.sort(Comparator.comparingInt(Coord::x));
    assertEquals(shots.get(0), damage.get(0));
    assertEquals(shots.get(2), damage.get(1));
    assertEquals(shots.get(4), damage.get(2));
  }

  /**
   * Tests the successfulHits method, this method was optional, so it's tested for operation
   */
  @Test
  void testSuccessfulHits() {
    assertDoesNotThrow(() -> mp.successfulHits(new ArrayList<>()));
    assertDoesNotThrow(() -> ai.successfulHits(new ArrayList<>()));
  }

  /**
   * Test the Ai player's takeShots method
   */
  @Test
  void testTakeShotsBetterAi() {
    // Mock specifications of ships
    HashMap<ShipType, Integer> specs = new HashMap<>();
    specs.put(ShipType.CARRIER, 1);
    specs.put(ShipType.BATTLESHIP, 1);
    specs.put(ShipType.DESTROYER, 1);
    specs.put(ShipType.SUBMARINE, 1);
    bai.setup(8, 8, specs);
    List<Coord> takenShots = bai.takeShots();
    assertTrue(takenShots.size() > 0);
    out.reset();

    // Test none
    bai.setup(8, 8, new HashMap<>());
    takenShots = bai.takeShots();
    assertEquals(0, takenShots.size());

    specs.put(ShipType.CARRIER, 2);
    specs.put(ShipType.BATTLESHIP, 5);
    specs.put(ShipType.DESTROYER, 5);
    specs.put(ShipType.SUBMARINE, 3);

    // Test large
    bai.setup(15, 15, specs);
    takenShots = bai.takeShots();
    assertEquals(15, takenShots.size());
    for (int i = 0; i < 14; i++) {
      takenShots = bai.takeShots();
      assertTrue(takenShots.size() > 0);
    }

    // Test large
    bai.setup(15, 15, specs);
    List<Coord> mockShots = new ArrayList<>();
    mockShots.add(new Coord(2, 2));
    mockShots.add(new Coord(1, 1));
    bai.successfulHits(mockShots);
    takenShots = bai.takeShots();
    assertEquals(15, takenShots.size());
    assertTrue(takenShots.contains(new Coord(1, 2)));
    assertTrue(takenShots.contains(new Coord(3, 2)));
    assertTrue(takenShots.contains(new Coord(2, 1)));
    assertTrue(takenShots.contains(new Coord(2, 3)));
    assertTrue(takenShots.contains(new Coord(1, 0)));
    assertTrue(takenShots.contains(new Coord(0, 1)));
    for (int i = 0; i < 14; i++) {
      takenShots = bai.takeShots();
      assertTrue(takenShots.size() > 0);
    }
  }

}