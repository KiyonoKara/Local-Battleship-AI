package kiyo.battleship.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import kiyo.battleship.MockAiPlayer;
import kiyo.battleship.MockManualPlayer;
import kiyo.battleship.model.AiPlayer;
import kiyo.battleship.model.Coord;
import kiyo.battleship.model.MoveData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BattleSalvoControllerTest {
  ByteArrayOutputStream out;
  BattleSalvoController bsc;
  MockAiPlayer mockAiPlayer;
  MockManualPlayer mockManualPlayer;

  private static final String ANSI_RESET = "\u001B[0m";
  //private static final String ANSI_BLUE = "\033[38;5;39m";
  private static final String ANSI_GREEN = "\u001B[38;5;36m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_YELLOW = "\033[0;33m";

  /**
   * Set up variables
   */
  @BeforeEach
  void setUp() {
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    mockAiPlayer = new MockAiPlayer();
    mockManualPlayer = new MockManualPlayer();
  }

  /**
   * Reset after each
   */
  @AfterEach
  void resetAfter() {
    out.reset();
    mockAiPlayer = new MockAiPlayer();
    mockManualPlayer = new MockManualPlayer();
  }

  /**
   * Tests runGame and battleSession methods since required inputs are similar
   */
  @Test
  void testRunGameAndBattleSession() {
    // Test a functional game
    String str = """
        6 6
        1 1 1 1""";
    InputStream input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(new AiPlayer(), new AiPlayer(), input);
    assertDoesNotThrow(() -> bsc.runGame());
    out.reset();
  }

  /**
   * Tests the dimension prompt
   */
  @Test
  void testPromptDim() {
    // Test empty
    String str = "";
    InputStream input = new ByteArrayInputStream(str.getBytes());

    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    assertThrows(NoSuchElementException.class, () -> bsc.promptDim(0, 0, false));

    // Test valid input
    str = """
        6 6""";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    String expected =
        (ANSI_YELLOW + "Enter a valid height and width (bounds are 6 to 15, inclusive)"
            + System.lineSeparator() + ANSI_RESET).repeat(2);
    bsc.promptDim(0, 0, false);
    assertEquals(expected, out.toString());
    out.reset();

    // Test exit
    str = "EXIT";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(0, 0, false);
    expected = ANSI_YELLOW + "Enter a valid height and width (bounds are 6 to 15, inclusive)"
        + System.lineSeparator() + ANSI_RESET;
    assertEquals(expected, out.toString());
    out.reset();

    // Test invalid input
    str = """
        1 1
        9 9""";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    expected += ANSI_RED
        + "Dimensions cannot be less than 6 nor greater than "
        + "15 and must be valid integers, try again" + ANSI_RESET + System.lineSeparator()
        + expected;
    bsc.promptDim(1, 1, false);
    assertEquals(expected, out.toString());
    out.reset();

    // Test invalid input
    str = """
        1 16
        9 9""";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(1, 1, false);
    assertEquals(expected, out.toString());
    out.reset();

    // Test invalid input on left
    str = """
        g 6
        9 9""";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(1, 1, false);
    assertEquals(expected, out.toString());
    out.reset();

    // Test invalid on right
    str = """
        6 g
        9 9""";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(1, 1, false);
    assertEquals(expected, out.toString());
    out.reset();

    // Test perfect cases
    bsc.promptDim(15, 15, false);
    assertEquals("", out.toString());
    bsc.promptDim(6, 6, true);
    assertEquals("", out.toString());

    // Test invalid input that becomes valid later
    str = """
        10 1
        1 10
        1 1
        20 20
        1 20
        20 1
        6 6""";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    expected =
        (ANSI_YELLOW + "Enter a valid height and width (bounds are 6 to 15, inclusive)"
            + System.lineSeparator() + ANSI_RESET
            + ANSI_RED
            + "Dimensions cannot be less than 6 nor greater than "
            + "15 and must be valid integers, try again" + ANSI_RESET
            + System.lineSeparator()).repeat(6)
            + ANSI_YELLOW + "Enter a valid height and width (bounds are 6 to 15, inclusive)"
            + System.lineSeparator() + ANSI_RESET;
    bsc.promptDim(1, 10, false);
    assertEquals(expected, out.toString());
    out.reset();

    // Test more invalid inputs
    expected = ANSI_YELLOW + "Enter a valid height and width (bounds are 6 to 15, inclusive)"
        + System.lineSeparator() + ANSI_RESET + ANSI_RED
        + "Dimensions cannot be less than 6 nor greater than "
        + "15 and must be valid integers, try again" + ANSI_RESET + System.lineSeparator()
        + ANSI_YELLOW + "Enter a valid height and width (bounds are 6 to 15, inclusive)"
        + System.lineSeparator() + ANSI_RESET;

    // Coverage for the while loop
    str = """
        20 10
        10 10""";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(10, 2, false);
    assertEquals(expected, out.toString());
    out.reset();

    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(2, 10, false);
    assertEquals(expected, out.toString());
    out.reset();

    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(20, 10, false);
    assertEquals(expected, out.toString());
    out.reset();

    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(10, 20, false);
    assertEquals(expected, out.toString());
    out.reset();
  }

  /**
   * Test the fleet prompt
   */
  @Test
  void testPromptFleet() {
    // Test normal input
    String str = """
        3 2 2 1""";
    InputStream input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(8, 8, false);
    out.reset();

    String expected = ANSI_YELLOW
        + "Please enter your fleet in the order [Carrier, Battleship, Destroyer, Submarine]."
        + System.lineSeparator() + "Remember, your fleet may not exceed size 8"
        + System.lineSeparator() + ANSI_RESET;
    bsc.promptFleet();
    assertEquals(expected, out.toString());
    out.reset();

    // Test invalid characters
    str = """
        g 2 2 1
        3 2 2 1""";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(8, 8, false);

    expected += ANSI_RED + "Invalid fleet sizes were entered, try again"
        + ANSI_RESET + System.lineSeparator();
    bsc.promptFleet();
    assertEquals(expected, out.toString());
    out.reset();

    // Test invalid numbers and fleet sizes
    str = """
        0 2 2 1
        3 2 2 1""";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(8, 8, false);

    bsc.promptFleet();
    assertEquals(expected, out.toString());
    out.reset();

    str = """
        8 2 2 1
        3 2 2 1""";
    input = new ByteArrayInputStream(str.getBytes());
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer, input);
    bsc.promptDim(8, 8, false);

    bsc.promptFleet();
    assertEquals(expected, out.toString());
    out.reset();
  }

  /**
   * Test turnStats
   */
  @Test
  void testTurnStats() {
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer);

    // Test empty
    MoveData move = new MoveData(new ArrayList<>(), new ArrayList<>(),
        new ArrayList<>(), new ArrayList<>());
    String expected =
        ANSI_GREEN + "Shots fired by Mock Manual which hit Mock Ai's ships: " + ANSI_RESET
            + System.lineSeparator() + System.lineSeparator()
            + ANSI_RED + "Shots fired by Mock Manual which did not hit Mock Ai's ships: "
            + ANSI_RESET
            + System.lineSeparator() + System.lineSeparator()
            + ANSI_RED + "Shots fired by Mock Ai which hit Mock Manual's ships: " + ANSI_RESET
            + System.lineSeparator() + System.lineSeparator()
            + ANSI_GREEN + "Shots fired by Mock Ai which did not hit Mock Manual's ships: "
            + ANSI_RESET
            + System.lineSeparator() + System.lineSeparator();
    bsc.turnStats(move);
    assertEquals(expected, out.toString());
    out.reset();

    ArrayList<Coord> temp = new ArrayList<>();
    temp.add(new Coord(0, 0));
    move = new MoveData(temp, temp, temp, temp);

    expected =
        ANSI_GREEN + "Shots fired by Mock Manual which hit Mock Ai's ships: " + ANSI_RESET
            + System.lineSeparator() + "(0, 0)" + System.lineSeparator()
            + ANSI_RED + "Shots fired by Mock Manual which did not hit Mock Ai's ships: "
            + ANSI_RESET
            + System.lineSeparator() + "(0, 0)" + System.lineSeparator()
            + ANSI_RED + "Shots fired by Mock Ai which hit Mock Manual's ships: " + ANSI_RESET
            + System.lineSeparator() + "(0, 0)" + System.lineSeparator()
            + ANSI_GREEN + "Shots fired by Mock Ai which did not hit Mock Manual's ships: "
            + ANSI_RESET
            + System.lineSeparator() + "(0, 0)" + System.lineSeparator();

    bsc.turnStats(move);
    assertEquals(expected, out.toString());
    out.reset();
  }

  /**
   * Tests battleResult and battleOutcome, they share similar outputs
   */
  @Test
  void testBattleResultAndOutcome() {
    // Initialize controller
    bsc = new BattleSalvoController(mockManualPlayer, mockAiPlayer);

    // Test cases for winners and losers of the game
    String expected = ANSI_GREEN + "Mock Manual wins the game!" + ANSI_RESET
        + System.lineSeparator() + "Mock Manual sunk all of Mock Ai's ships"
        + System.lineSeparator() + ANSI_RED + "Mock Ai lost..."
        + ANSI_RESET + System.lineSeparator() + "All of Mock Ai's ships were sunken by Mock Manual"
        + System.lineSeparator();

    bsc.battleResult(mockManualPlayer, mockAiPlayer, 1, 0);
    assertEquals(expected, out.toString());
    out.reset();

    bsc.battleOutcome(mockManualPlayer, mockAiPlayer, false);
    assertEquals(expected, out.toString());
    out.reset();

    // Test other way around
    expected = ANSI_GREEN + "Mock Ai wins the game!" + ANSI_RESET
        + System.lineSeparator() + "Mock Ai sunk all of Mock Manual's ships"
        + System.lineSeparator() + ANSI_RED + "Mock Manual lost..."
        + ANSI_RESET + System.lineSeparator() + "All of Mock Manual's ships were sunken by Mock Ai"
        + System.lineSeparator();

    bsc.battleResult(mockManualPlayer, mockAiPlayer, 0, 1);
    assertEquals(expected, out.toString());
    out.reset();

    bsc.battleOutcome(mockAiPlayer, mockManualPlayer, false);
    assertEquals(expected, out.toString());
    out.reset();

    // Test draw
    expected = "Mock Manual had a draw."
        + System.lineSeparator() + "Mock Manual had a draw with Mock Ai"
        + System.lineSeparator() + "Mock Ai had a draw."
        + System.lineSeparator() + "Mock Ai had a draw with Mock Manual"
        + System.lineSeparator();

    bsc.battleResult(mockManualPlayer, mockAiPlayer, 0, 0);
    assertEquals(expected, out.toString());
    out.reset();

    bsc.battleOutcome(mockManualPlayer, mockAiPlayer, true);
    assertEquals(expected, out.toString());
    out.reset();
  }
}