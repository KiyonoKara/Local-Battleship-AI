package kiyo.battleship.view;

import kiyo.battleship.model.Board;
import kiyo.battleship.model.Coord;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * View component of Battle Salvo
 */
public class BattleSalvoView {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_BLUE = "\033[38;5;39m";
  private static final String ANSI_GREEN = "\u001B[38;5;36m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_YELLOW = "\033[0;33m";

  private final Scanner scanner;

  public BattleSalvoView() {
    this(System.in);
  }

  /**
   * Accepts an input stream
   *
   * @param in InputStream for piping
   */
  public BattleSalvoView(InputStream in) {
    this.scanner = new Scanner(in);
  }

  /**
   * Dynamically displays a board of any size
   *
   * @param label Label for the board
   * @param board The board of the game
   * @param mask Whether it's hidden from the client or not
   */
  public void displayBoard(String label, Board board, boolean mask) {
    StringBuilder sb = new StringBuilder();
    sb.append(label).append(System.lineSeparator());
    int row = board.grid.length;
    int col = board.grid[0].length;
    if (mask) {
      for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
          if (board.grid[i][j] != 'M' && board.grid[i][j] != 'H') {
            sb.append('-');
          } else {
            sb.append(board.grid[i][j]);
          }

          if (j < col - 1) {
            sb.append(" ");
          }
        }
        sb.append(System.lineSeparator());
      }
    } else {
      for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
          sb.append(board.grid[i][j]);
          if (j < col - 1) {
            sb.append(" ");
          }
        }
        sb.append(System.lineSeparator());
      }
    }
    System.out.println(sb);
  }

  /**
   * Shows a preface
   *
   * @param message The message
   */
  public void showPreface(String message) {
    System.out.println(ANSI_BLUE + message + ANSI_RESET);
  }

  /**
   * Shows plain text
   *
   * @param text The text
   */
  public void showText(String text) {
    System.out.println(text);
  }

  /**
   * Shows a prompt
   *
   * @param prompt The prompt
   */
  public void showPrompt(String prompt) {
    System.out.print(ANSI_YELLOW + prompt + ANSI_RESET);
  }

  /**
   * Shows a success message or indicator
   *
   * @param message The message
   */
  public void showSuccess(String message) {
    System.out.println(ANSI_GREEN + message + ANSI_RESET);
  }

  /**
   * Shows a failure message or indicator
   *
   * @param message The message
   */
  public void showFailure(String message) {
    System.out.println(ANSI_RED + message + ANSI_RESET);
  }

  /**
   * Prompts player for the coords
   *
   * @param message Message prompt
   * @param board The board
   * @return List of coordinates entered
   */
  public List<Coord> promptCoords(String message, Board board, int maxAllowed) {
    System.out.println(ANSI_BLUE + message + ANSI_RESET);
    int availableShots = Math.min(board.standingShips.size(), maxAllowed);
    System.out.println("You have " + availableShots + " available shots.");
    List<Coord> coords = new ArrayList<>();
    int width = board.grid.length;
    int height = board.grid[0].length;

    if (availableShots == 0) {
      return coords;
    }

    for (int i = 0; i < availableShots; i++) {
      int x = this.scanner.nextInt();
      int y = this.scanner.nextInt();

      if (x >= width || y >= height) {
        System.out.println("Invalid coordinates, try different coordinates.");
        i--;
      } else {
        coords.add(new Coord(x, y));
      }
    }
    return coords;
  }

  /**
   * Formats and displays a list of coords
   *
   * @param coords List of Coord
   */
  public void showCoords(List<Coord> coords) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < coords.size(); i++) {
      sb.append("(").append(coords.get(i).x()).append(", ").append(coords.get(i).y()).append(")");
      if (i < coords.size() - 1) {
        sb.append(", ");
      }
    }
    System.out.println(sb);
  }
}
