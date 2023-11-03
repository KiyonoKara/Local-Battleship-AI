package kiyo.battleship.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import kiyo.battleship.model.Board;
import kiyo.battleship.model.Coord;
import kiyo.battleship.model.Ship;
import kiyo.battleship.model.ShipType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testing for the view component of the application
 */
class BattleSalvoViewTest {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_BLUE = "\033[38;5;39m";
  private static final String ANSI_GREEN = "\u001B[38;5;36m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_YELLOW = "\033[0;33m";

  private ByteArrayOutputStream out;
  private BattleSalvoView bsv;

  /**
   * Set up the view component and output stream
   */
  @BeforeEach
  void setUp() {
    bsv = new BattleSalvoView();
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
  }

  /**
   * Tests the board display
   */
  @Test
  void displayBoard() {
    ArrayList<Ship> ships = new ArrayList<>();
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
    Board testBoard = new Board(6, 6, ships);
    bsv.displayBoard("visible", testBoard, false);
    String boardData = "visible"
        + System.lineSeparator() + "C B - - - -"
        + System.lineSeparator() + "C B - - - -"
        + System.lineSeparator() + "C B D S S -"
        + System.lineSeparator() + "C B D S S -"
        + System.lineSeparator() + "C B D S S -"
        + System.lineSeparator() + "C - D - S -"
        + System.lineSeparator() + System.lineSeparator();
    assertEquals(boardData, out.toString());
    out.reset();

    String boardDataNotVisible = "not visible"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + System.lineSeparator();
    bsv.displayBoard("not visible", testBoard, true);
    assertEquals(boardDataNotVisible, out.toString());
    out.reset();

    testBoard.grid[0][0] = 'H';
    testBoard.grid[0][1] = 'M';
    bsv.displayBoard("some visibility", testBoard, true);
    String boardDataSomeVisible = "some visibility"
        + System.lineSeparator() + "H M - - - -"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + "- - - - - -"
        + System.lineSeparator() + System.lineSeparator();
    assertEquals(boardDataSomeVisible, out.toString());
  }

  /**
   * Tests the prompts for the coordinates
   */
  @Test
  void promptCoords() {
    // Test coord prompt input
    String coordsInput = """
        0 0
        0 1
        0 2
        0 3
        0 4
        0 5
        """;
    InputStream inputStream = new ByteArrayInputStream(coordsInput.getBytes());
    bsv = new BattleSalvoView(inputStream);

    // Mock ship list
    List<Ship> ships = new ArrayList<>();
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
            new Coord(2, 1),
            new Coord(3, 1),
            new Coord(4, 1),
            new Coord(5, 1)}));
    ships.add(new Ship(ShipType.SUBMARINE,
        new Coord[] {
            new Coord(2, 4),
            new Coord(3, 4),
            new Coord(4, 4)}));

    List<Coord> fromPrompt =
        bsv.promptCoords("Prompt", new Board(6, 6, ships), 6);
    assertEquals(
        ANSI_BLUE + "Prompt" + ANSI_RESET + System.lineSeparator()
            + "You have 4 available shots." + System.lineSeparator(), out.toString());
    assertEquals(4, fromPrompt.size());
    out.reset();

    coordsInput = """
        18 18
        2 0
        3 0
        4 0
        5 0
        0 0""";
    inputStream = new ByteArrayInputStream(coordsInput.getBytes());
    bsv = new BattleSalvoView(inputStream);
    fromPrompt =
        bsv.promptCoords("Prompt", new Board(6, 6, ships), 6);
    assertEquals(
        ANSI_BLUE + "Prompt" + ANSI_RESET + System.lineSeparator()
            + "You have 4 available shots." + System.lineSeparator()
            + "Invalid coordinates, try different coordinates." + System.lineSeparator(),
        out.toString());
    assertEquals(4, fromPrompt.size());
    out.reset();

    coordsInput = """
        0 0
        2 0
        7 4
        4 0
        5 0
        1 0""";
    inputStream = new ByteArrayInputStream(coordsInput.getBytes());
    bsv = new BattleSalvoView(inputStream);
    fromPrompt =
        bsv.promptCoords("Prompt", new Board(6, 6, ships), 6);
    assertEquals(
        ANSI_BLUE + "Prompt" + ANSI_RESET + System.lineSeparator()
            + "You have 4 available shots." + System.lineSeparator()
            + "Invalid coordinates, try different coordinates." + System.lineSeparator(),
        out.toString());
    assertEquals(4, fromPrompt.size());
    out.reset();

    coordsInput = """
        0 0
        2 0
        4 7
        4 0
        5 0
        1 0""";
    inputStream = new ByteArrayInputStream(coordsInput.getBytes());
    bsv = new BattleSalvoView(inputStream);
    fromPrompt =
        bsv.promptCoords("Prompt", new Board(6, 6, ships), 6);
    assertEquals(
        ANSI_BLUE + "Prompt" + ANSI_RESET + System.lineSeparator()
            + "You have 4 available shots." + System.lineSeparator()
            + "Invalid coordinates, try different coordinates." + System.lineSeparator(),
        out.toString());
    assertEquals(4, fromPrompt.size());
    out.reset();

    // Test with no ships
    ships.clear();
    fromPrompt =
        bsv.promptCoords("Prompt", new Board(6, 6, ships), 6);
    assertEquals(
        ANSI_BLUE + "Prompt" + ANSI_RESET + System.lineSeparator()
            + "You have 0 available shots." + System.lineSeparator(), out.toString());
    assertEquals(0, fromPrompt.size());
  }

  /**
   * Tests showCoords
   */
  @Test
  void showCoords() {
    // Test empty list of Coord
    ArrayList<Coord> coords = new ArrayList<>();
    bsv.showCoords(coords);
    assertEquals(System.lineSeparator(), out.toString());
    out.reset();

    // Test non-empty list of Coord
    coords.add(new Coord(0, 0));
    coords.add(new Coord(1, 1));
    coords.add(new Coord(2, 2));
    bsv.showCoords(coords);
    assertEquals("(0, 0), (1, 1), (2, 2)" + System.lineSeparator(), out.toString());
    out.reset();
  }

  /**
   * showPreface tests
   */
  @Test
  void showPreface() {
    bsv.showPreface("");
    assertEquals(ANSI_BLUE + ANSI_RESET + System.lineSeparator(), out.toString());
    out.reset();

    bsv.showPreface("This is a test preface.");
    assertEquals(ANSI_BLUE + "This is a test preface." + ANSI_RESET
        + System.lineSeparator(), out.toString());
    out.reset();

    bsv.showPreface("This has line splits" + System.lineSeparator() + "like a preface with"
        + System.lineSeparator() + "a prompt.");
    assertEquals(ANSI_BLUE + "This has line splits" + System.lineSeparator()
        + "like a preface with" + System.lineSeparator() + "a prompt." + ANSI_RESET
        + System.lineSeparator(), out.toString());
  }

  /**
   * showText tests
   */
  @Test
  void showText() {
    bsv.showText("");
    assertEquals(System.lineSeparator(), out.toString());
    out.reset();

    bsv.showText("This is text");
    assertEquals("This is text" + System.lineSeparator(), out.toString());
    out.reset();

    bsv.showText(
        "This has line splits" + System.lineSeparator() + "as text" + System.lineSeparator()
            + "with more lines");
    assertEquals(
        "This has line splits" + System.lineSeparator() + "as text"
            + System.lineSeparator() + "with more lines" + System.lineSeparator(), out.toString());
  }

  /**
   * showPrompt tests
   */
  @Test
  void showPrompt() {
    bsv.showPrompt("");
    assertEquals(ANSI_YELLOW + ANSI_RESET, out.toString());
    out.reset();

    bsv.showPrompt("This is prompt text");
    assertEquals(ANSI_YELLOW + "This is prompt text" + ANSI_RESET, out.toString());
    out.reset();

    bsv.showPrompt(
        "Multi-line" + System.lineSeparator() + "prompt" + System.lineSeparator() + "text");
    assertEquals(
        ANSI_YELLOW + "Multi-line" + System.lineSeparator() + "prompt"
            + System.lineSeparator() + "text" + ANSI_RESET, out.toString());
  }

  /**
   * showSuccess tests
   */
  @Test
  void showSuccess() {
    bsv.showSuccess("");
    assertEquals(ANSI_GREEN + ANSI_RESET + System.lineSeparator(), out.toString());
    out.reset();

    bsv.showSuccess("This is a success message");
    assertEquals(ANSI_GREEN + "This is a success message" + ANSI_RESET
        + System.lineSeparator(), out.toString());
    out.reset();

    bsv.showSuccess(
        "Multi-line" + System.lineSeparator() + "prompt" + System.lineSeparator() + "text");
    assertEquals(
        ANSI_GREEN + "Multi-line" + System.lineSeparator() + "prompt"
            + System.lineSeparator() + "text" + ANSI_RESET + System.lineSeparator(),
        out.toString());
  }

  /**
   * showFailure tests
   */
  @Test
  void showFailure() {
    bsv.showFailure("");
    assertEquals(ANSI_RED + ANSI_RESET + System.lineSeparator(), out.toString());
    out.reset();

    bsv.showFailure("This is a failure message");
    assertEquals(ANSI_RED + "This is a failure message" + ANSI_RESET + System.lineSeparator(),
        out.toString());
    out.reset();

    bsv.showFailure(
        "Multi-line" + System.lineSeparator() + "prompt" + System.lineSeparator() + "text");
    assertEquals(
        ANSI_RED + "Multi-line" + System.lineSeparator() + "prompt"
            + System.lineSeparator() + "text" + ANSI_RESET + System.lineSeparator(),
        out.toString());
  }
}