package kiyo.battleship.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kiyo.battleship.json.CoordinatesJson;
import kiyo.battleship.json.EndGameJson;
import kiyo.battleship.json.FleetJson;
import kiyo.battleship.json.JoinJson;
import kiyo.battleship.json.JsonUtils;
import kiyo.battleship.json.MessageJson;
import kiyo.battleship.json.SetupAdapter;
import kiyo.battleship.json.ShipAdapter;
import kiyo.battleship.model.Coord;
import kiyo.battleship.model.GameType;
import kiyo.battleship.model.Player;
import kiyo.battleship.model.Ship;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Dispatches messages and data from the server; responds to server
 */
public class ProxyController {
  private final Socket server;
  private final InputStream in;
  private final PrintStream out;
  private final Player player;
  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * ProxyController for delegating and responding to server messages
   *
   * @param server The socket server
   * @param player A player
   * @throws IOException If something goes wrong with a stream
   */
  public ProxyController(Socket server, Player player) throws IOException {
    this.server = server;
    this.in = server.getInputStream();
    this.out = new PrintStream(server.getOutputStream());
    this.player = player;
  }

  /**
   * Runs the controller and gets the interactions from the server
   */
  public void run() {
    try {
      JsonParser jsonParser = this.mapper.getFactory().createParser(this.in);

      while (!this.server.isClosed()) {
        MessageJson message = jsonParser.readValueAs(MessageJson.class);
        this.delegateMessage(message);
      }
    } catch (IOException e) {
      System.err.println("Disconnected from server or failed to parse");
    }
  }

  /**
   * Delegates the arguments of the message
   *
   * @param message The deserialized message
   */
  private void delegateMessage(MessageJson message) {
    String methodName = message.methodName();
    JsonNode arguments = message.arguments();

    JsonNode args;
    boolean shouldEnd = false;
    switch (methodName) {
      case "join" -> args = doJoin();
      case "setup" -> args = handleSetup(arguments);
      case "take-shots" -> args = doTakeShots();
      case "report-damage" -> args = handleDamageReport(arguments);
      case "successful-hits" -> args = handleSuccessfulHits(arguments);
      case "end-game" -> {
        args = handleEndgame(arguments);
        shouldEnd = true;
      }
      default -> args = this.mapper.createObjectNode();
    }

    this.out.println(JsonUtils.serializeRecord(new MessageJson(methodName, args)));

    if (shouldEnd) {
      try {
        this.server.close();
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    }
  }

  /**
   * Joins a game
   */
  private JsonNode doJoin() {
    JoinJson joinJson = new JoinJson(this.player.name(), GameType.SINGLE);
    return JsonUtils.serializeRecord(joinJson);
  }

  /**
   * Handles the setup from the server
   *
   * @param arguments The arguments that contain a width, height, and fleet-spec
   */
  private JsonNode handleSetup(JsonNode arguments) {
    SetupAdapter setupArgs = this.mapper.convertValue(arguments, SetupAdapter.class);
    List<Ship> fleet =
        this.player.setup(setupArgs.height(), setupArgs.width(), setupArgs.fleetSpec());

    List<ShipAdapter> adaptedFleet = new ArrayList<>();
    for (Ship ship : fleet) {
      adaptedFleet.add(new ShipAdapter(ship));
    }
    FleetJson fleetJson = new FleetJson(adaptedFleet);
    return JsonUtils.serializeRecord(fleetJson);
  }

  /**
   * Takes shots from the local player
   */
  private JsonNode doTakeShots() {
    CoordinatesJson takeShotsCoordinates = new CoordinatesJson(this.player.takeShots());
    return JsonUtils.serializeRecord(takeShotsCoordinates);
  }

  /**
   * Evaluates the coordinates from the server and responds with coordinates where there
   * were hits.
   *
   * @param arguments The coordinates
   */
  private JsonNode handleDamageReport(JsonNode arguments) {
    CoordinatesJson reportDamageArgs = this.mapper.convertValue(arguments, CoordinatesJson.class);
    List<Coord> damage = this.player.reportDamage(reportDamageArgs.coordinates());
    CoordinatesJson damageJson = new CoordinatesJson(damage);
    return JsonUtils.serializeRecord(damageJson);
  }


  /**
   * Consumes server's coordinates of successful hits
   *
   * @param arguments The coordinates
   */
  private JsonNode handleSuccessfulHits(JsonNode arguments) {
    CoordinatesJson successfulHitsArgs = this.mapper.convertValue(arguments,
        CoordinatesJson.class);
    this.player.successfulHits(successfulHitsArgs.coordinates());
    return this.mapper.createObjectNode();
  }

  /**
   * Handles the endgame with the result and reason
   *
   * @param arguments Result and reason deserialized
   */
  private JsonNode handleEndgame(JsonNode arguments) {
    EndGameJson endGameArgs = this.mapper.convertValue(arguments, EndGameJson.class);
    this.player.endGame(endGameArgs.result(), endGameArgs.reason());
    return this.mapper.createObjectNode();
  }
}