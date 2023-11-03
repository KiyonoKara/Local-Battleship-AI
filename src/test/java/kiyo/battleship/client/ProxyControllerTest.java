package kiyo.battleship.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kiyo.battleship.json.CoordinatesJson;
import kiyo.battleship.json.EndGameJson;
import kiyo.battleship.json.JsonUtils;
import kiyo.battleship.json.MessageJson;
import kiyo.battleship.json.SetupAdapter;
import kiyo.battleship.model.BetterAiPlayer;
import kiyo.battleship.model.Coord;
import kiyo.battleship.model.Direction;
import kiyo.battleship.model.GameResult;
import kiyo.battleship.model.ShipType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the ProxyController
 */
class ProxyControllerTest {
  private ByteArrayOutputStream log;
  private ProxyController pc;
  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * Sets up the outputs
   */
  @BeforeEach
  void setUp() {
    assertDoesNotThrow(JsonUtils::new);
    this.log = new ByteArrayOutputStream(2048);
  }

  /**
   * Tests the client's join response
   */
  @Test
  void testJoin() {
    MessageJson serverJoinJson = new MessageJson("join", this.mapper.createObjectNode());
    JsonNode serverJoinMessage = JsonUtils.serializeRecord(serverJoinJson);

    // Create client
    MockSocket socket = new MockSocket(this.log, List.of(serverJoinMessage.toString()));

    try {
      this.pc = new ProxyController(socket, new BetterAiPlayer());
    } catch (IOException e) {
      fail();
    }

    this.pc.run();
    try {
      socket.close();
    } catch (IOException e) {
      fail();
    }

    assertEquals(
        "{\"method-name\":\"join\","
            + "\"arguments\":{\"name\":\"Better AI Player\",\"game-type\":\"SINGLE\"}}\n",
        this.log.toString());
  }

  /**
   * Tests the client's setup response
   */
  @Test
  void testSetup() {
    String serverSetupMessage = this.createTestSetup();

    // Create client
    MockSocket socket = new MockSocket(this.log, List.of(serverSetupMessage));

    try {
      this.pc = new ProxyController(socket, new BetterAiPlayer());
    } catch (IOException e) {
      fail();
    }

    this.pc.run();
    try {
      socket.close();
    } catch (IOException e) {
      fail();
    }

    String result = this.log.toString();
    assertTrue(result.contains("\"length\":6"));
    assertTrue(result.contains("\"length\":5"));
    assertTrue(result.contains("\"length\":4"));
    assertTrue(result.contains("\"length\":3"));
    assertTrue(result.contains("fleet"));
    assertTrue(result.contains(Direction.HORIZONTAL.name())
        || result.contains(Direction.VERTICAL.name()));
  }

  /**
   * Tests the client's takeShots response
   */
  @Test
  void testTakeShots() {
    StringBuilder completeMessage = new StringBuilder(this.createTestSetup());
    MessageJson serverTakeShotsJson = new MessageJson("take-shots",
        this.mapper.createObjectNode());
    JsonNode serverTakeShotsMessage = JsonUtils.serializeRecord(serverTakeShotsJson);

    completeMessage.append(serverTakeShotsMessage);
    // Create client
    MockSocket socket = new MockSocket(this.log, List.of(completeMessage.toString()));

    try {
      this.pc = new ProxyController(socket, new BetterAiPlayer());
    } catch (IOException e) {
      fail();
    }

    this.pc.run();
    try {
      socket.close();
    } catch (IOException e) {
      fail();
    }

    String result = this.log.toString();
    assertTrue(result.contains("\"x\""));
    assertTrue(result.contains("\"y\""));
    assertTrue(result.contains("coordinates"));
  }

  /**
   * Tests the client's reportDamage response
   */
  @Test
  void testReportDamage() {

    List<Coord> testCoords = new ArrayList<>();
    testCoords.add(new Coord(0, 0));
    testCoords.add(new Coord(1, 1));
    testCoords.add(new Coord(2, 2));
    CoordinatesJson coords = new CoordinatesJson(testCoords);
    JsonNode serializedCoords = JsonUtils.serializeRecord(coords);

    StringBuilder completeMessage = new StringBuilder(this.createTestSetup());
    MessageJson serverReportDamageJson = new MessageJson("report-damage",
        serializedCoords);
    JsonNode serverReportDamageMessage = JsonUtils.serializeRecord(serverReportDamageJson);

    completeMessage.append(serverReportDamageMessage);
    // Create client
    MockSocket socket = new MockSocket(this.log, List.of(completeMessage.toString()));

    try {
      this.pc = new ProxyController(socket, new BetterAiPlayer());
    } catch (IOException e) {
      fail();
    }

    this.pc.run();
    try {
      socket.close();
    } catch (IOException e) {
      fail();
    }

    String result = this.log.toString();
    assertTrue(result.contains("\"x\""));
    assertTrue(result.contains("\"y\""));
    assertTrue(result.contains("coordinates"));
  }

  /**
   * Tests the client's successfulHits response
   */
  @Test
  void testSuccessfulHits() {
    List<Coord> testCoords = new ArrayList<>();
    testCoords.add(new Coord(0, 0));
    testCoords.add(new Coord(1, 1));
    testCoords.add(new Coord(2, 2));
    CoordinatesJson coords = new CoordinatesJson(testCoords);
    JsonNode serializedCoords = JsonUtils.serializeRecord(coords);

    StringBuilder completeMessage = new StringBuilder(this.createTestSetup());
    MessageJson serverSuccessfulHitsJson = new MessageJson("successful-hits",
        serializedCoords);
    JsonNode serverSuccessfulHitsMessage = JsonUtils.serializeRecord(serverSuccessfulHitsJson);

    completeMessage.append(serverSuccessfulHitsMessage);
    // Create client
    MockSocket socket = new MockSocket(this.log, List.of(completeMessage.toString()));

    try {
      this.pc = new ProxyController(socket, new BetterAiPlayer());
    } catch (IOException e) {
      fail();
    }

    this.pc.run();
    try {
      socket.close();
    } catch (IOException e) {
      fail();
    }

    assertTrue(
        this.log.toString().contains("{\"method-name\":\"successful-hits\",\"arguments\":{}}"));
  }

  /**
   * Tests the client's endGame response
   */
  @Test
  void testEndgame() {
    EndGameJson winJson = new EndGameJson(GameResult.WIN, "You won.");
    JsonNode winMessage = JsonUtils.serializeRecord(winJson);
    MessageJson endGameWinJson = new MessageJson("end-game", winMessage);
    JsonNode endGameWinMessage = JsonUtils.serializeRecord(endGameWinJson);

    // Create client
    MockSocket socket = new MockSocket(this.log, List.of(endGameWinMessage.toString()));

    try {
      this.pc = new ProxyController(socket, new BetterAiPlayer());
    } catch (IOException e) {
      fail();
    }

    this.pc.run();
    try {
      socket.close();
    } catch (IOException e) {
      fail();
    }

    String result = this.log.toString();
    assertEquals("{\"method-name\":\"end-game\",\"arguments\":{}}" + System.lineSeparator(),
        result);
  }

  /**
   * Test invalid method name from the server
   */
  @Test
  void testDefaultDelegation() {
    MessageJson endGameWinJson = new MessageJson("non-existent-method",
        this.mapper.createObjectNode());
    JsonNode endGameWinMessage = JsonUtils.serializeRecord(endGameWinJson);

    // Create client
    MockSocket socket = new MockSocket(this.log, List.of(endGameWinMessage.toString()));

    try {
      this.pc = new ProxyController(socket, new BetterAiPlayer());
    } catch (IOException e) {
      fail();
    }

    this.pc.run();
    try {
      socket.close();
    } catch (IOException e) {
      fail();
    }
  }


  /**
   * Tests the delegateMessage method with a failing close
   */
  @Test
  void testFailedClose() {
    EndGameJson loseJson = new EndGameJson(GameResult.LOSE, "You lost.");
    JsonNode loseMessage = JsonUtils.serializeRecord(loseJson);
    MessageJson endGameLoseJson = new MessageJson("end-game", loseMessage);
    JsonNode endGameWinMessage = JsonUtils.serializeRecord(endGameLoseJson);

    // Create client
    MockFailServer socket = new MockFailServer(this.log, List.of(endGameWinMessage.toString()));

    try {
      this.pc = new ProxyController(socket, new BetterAiPlayer());
    } catch (IOException e) {
      fail();
    }

    this.pc.run();
  }

  /**
   * Tests the state if the server is closed before any messages can be delegated
   */
  @Test
  void testClosed() {
    MockSocket socket = new MockSocket(this.log, List.of(""));

    try {
      this.pc = new ProxyController(socket, new BetterAiPlayer());
    } catch (IOException e) {
      fail();
    }

    try {
      socket.close();
    } catch (IOException e) {
      fail();
    }
    this.pc.run();

    assertEquals("", this.log.toString());
  }

  /**
   * Creates a test set up for volleys
   *
   * @return JSON setup
   */
  private String createTestSetup() {
    Map<ShipType, Integer> testFleetSpec = new HashMap<>();
    testFleetSpec.put(ShipType.CARRIER, 3);
    testFleetSpec.put(ShipType.BATTLESHIP, 2);
    testFleetSpec.put(ShipType.DESTROYER, 2);
    testFleetSpec.put(ShipType.SUBMARINE, 1);
    SetupAdapter setupJson = new SetupAdapter(10, 10, testFleetSpec);
    JsonNode setupFleetSpec = JsonUtils.serializeRecord(setupJson);
    MessageJson serverSetUpJson = new MessageJson("setup", setupFleetSpec);
    JsonNode serverSetUpMessage = JsonUtils.serializeRecord(serverSetUpJson);
    return serverSetUpMessage.toString();
  }
}