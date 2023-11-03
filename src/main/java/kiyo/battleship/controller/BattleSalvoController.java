package kiyo.battleship.controller;

import kiyo.battleship.model.Coord;
import kiyo.battleship.model.GameResult;
import kiyo.battleship.model.MoveData;
import kiyo.battleship.model.Player;
import kiyo.battleship.model.ShipType;
import kiyo.battleship.view.BattleSalvoView;
import java.io.InputStream;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Controller for Battle Salvo
 */
public class BattleSalvoController {
  Scanner scanner;
  Player player;
  Player opponent;
  BattleSalvoView bsv;
  private int height;
  private int width;
  private int maxSize;
  private Map<ShipType, Integer> specs;
  private boolean atomicExit = false;

  /**
   * Default constructor for two players, uses the default input stream
   *
   * @param player The player
   * @param opponent The opponent
   */
  public BattleSalvoController(Player player, Player opponent) {
    this(player, opponent, System.in);
  }

  /**
   * Constructor that also supports a custom input stream
   *
   * @param player The player
   * @param opponent The opponent
   * @param in An InputStream object
   */
  public BattleSalvoController(Player player, Player opponent, InputStream in) {
    this.scanner = new Scanner(in);
    this.player = player;
    this.opponent = opponent;
    this.bsv = new BattleSalvoView(in);
    this.specs = new HashMap<>();
  }

  /**
   * Runs the entire game
   */
  public void runGame() {
    // Show preface
    this.bsv.showPreface("Welcome to Battle Salvo");

    // Prompt player for dimensions
    this.promptDim(0, 0, false);

    // For gracefully exiting the program
    if (atomicExit) {
      return;
    }

    // Prompt the fleet using the max size field
    this.promptFleet();

    // Runs the battle session and shows the outcome
    this.battleSession();
  }

  /**
   * Prompts dimensions
   */
  void promptDim(int height, int width, boolean await) {
    while (height > 15 || width > 15 || height < 6 || width < 6) {
      if (await) {
        this.bsv.showFailure("Dimensions cannot be less than 6 nor greater than 15 and must be "
            + "valid integers, try again");
      }
      this.bsv.showPrompt(
          "Enter a valid height and width (bounds are 6 to 15, inclusive)"
              + System.lineSeparator());
      String potentialInt = this.scanner.next();
      if (potentialInt.equals("EXIT")) {
        this.atomicExit = true;
        return;
      }
      int parsed;
      try {
        parsed = Integer.parseInt(potentialInt);
      } catch (NumberFormatException e) {
        promptDim(-1, -1, true);
        return;
      }
      try {
        height = parsed;
        width = scanner.nextInt();
      } catch (InputMismatchException e) {
        this.scanner.nextLine();
        promptDim(-1, -1, true);
        return;
      }
      if (height > 15 || width > 15 || height < 6 || width < 6) {
        promptDim(-1, -1, true);
        return;
      }
    }
    this.height = height;
    this.width = width;

    // Max size of the fleet determined by min(n, m)
    this.maxSize = Math.min(this.height, this.width);
  }

  /**
   * Prompts user for the fleet
   */
  void promptFleet() {
    this.bsv.showPrompt(
        "Please enter your fleet in the order [Carrier, Battleship, Destroyer, Submarine]."
            + System.lineSeparator() + "Remember, your fleet may not exceed size "
            + this.maxSize + System.lineSeparator());

    int fleetSize = 0;

    Map<ShipType, Integer> specs = new HashMap<>();
    for (int i = 0; i < ShipType.values().length; i++) {
      int shipCount = 0;
      try {
        shipCount = this.scanner.nextInt();
      } catch (InputMismatchException e) {
        this.scanner.nextLine();
      }

      if (shipCount <= 0 || fleetSize + shipCount > this.maxSize) {
        this.bsv.showFailure(
            "Invalid fleet sizes were entered, try again");
        fleetSize = 0;
        i = 0;
      } else {
        specs.put(ShipType.values()[i], shipCount);
        fleetSize += shipCount;
      }
    }

    this.specs = specs;
  }

  /**
   * Hosts a battle session
   */
  void battleSession() {
    // Set up the players
    player.setup(this.height, this.width, this.specs);
    opponent.setup(this.height, this.width, this.specs);

    // Start the battle session
    List<Coord> oppShots = opponent.takeShots();
    List<Coord> myShots = player.takeShots();

    while (myShots.size() != 0) {
      List<Coord> myTakenHits = player.reportDamage(oppShots);
      List<Coord> oppTakenHits = opponent.reportDamage(myShots);
      player.successfulHits(oppTakenHits);
      opponent.successfulHits(myTakenHits);

      List<Coord> missedByMe =
          myShots.stream().filter(coord -> !oppTakenHits.contains(coord)).toList();
      List<Coord> missedByOpponent =
          oppShots.stream().filter(coord -> !myTakenHits.contains(coord)).toList();

      this.turnStats(new MoveData(oppTakenHits, missedByMe, myTakenHits, missedByOpponent));

      oppShots = opponent.takeShots();
      myShots = player.takeShots();
    }

    int myRemaining = this.player.takeShots().size();
    int oppRemaining = this.opponent.takeShots().size();
    this.battleResult(player, opponent, myRemaining, oppRemaining);
  }

  /**
   * Shows the game stats per move
   */
  void turnStats(MoveData move) {
    bsv.showSuccess(
        "Shots fired by " + player.name() + " which hit " + opponent.name() + "'s ships: ");
    bsv.showCoords(move.damageByP1());

    bsv.showFailure("Shots fired by " + player.name() + " which did not hit " + opponent.name()
        + "'s ships: ");
    bsv.showCoords(move.missedByP1());

    bsv.showFailure(
        "Shots fired by " + opponent.name() + " which hit " + player.name() + "'s ships: ");
    bsv.showCoords(move.damageByP2());

    bsv.showSuccess("Shots fired by " + opponent.name() + " which did not hit " + player.name()
        + "'s ships: ");
    bsv.showCoords(move.missedByP2());
  }

  /**
   * Decides the outcome of the battle
   */
  void battleResult(Player p1, Player p2, int remainingP1, int remainingP2) {
    if (remainingP1 > remainingP2) {
      this.battleOutcome(p1, p2, false);
    }
    if (remainingP1 == remainingP2) {
      this.battleOutcome(p1, p2, true);
    }
    if (remainingP1 < remainingP2) {
      this.battleOutcome(p2, p1, false);
    }
  }

  /**
   * Shows the outcome of the battle
   *
   * @param p1   The winning player
   * @param p2   The losing player
   * @param draw If it's a draw, both players are on equal ground and have a draw
   */
  public void battleOutcome(Player p1, Player p2, boolean draw) {
    if (draw) {
      p1.endGame(GameResult.DRAW, p1.name() + " had a draw with " + p2.name());
      p2.endGame(GameResult.DRAW, p2.name() + " had a draw with " + p1.name());
    } else {
      p1.endGame(GameResult.WIN, p1.name() + " sunk all of " + p2.name() + "'s ships");
      p2.endGame(GameResult.LOSE, "All of " + p2.name() + "'s ships were sunken by "
          + p1.name());
    }
  }
}
