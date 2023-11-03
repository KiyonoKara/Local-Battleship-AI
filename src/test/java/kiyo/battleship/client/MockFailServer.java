package kiyo.battleship.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Purposefully failing server for the close method
 */
public class MockFailServer extends MockSocket {

  /**
   * @param fromClient Content from the client to the server
   * @param fromServer Content from the server to the client
   */
  public MockFailServer(ByteArrayOutputStream fromClient, List<String> fromServer) {
    super(fromClient, fromServer);
  }

  @Override
  public void close() throws IOException {
    throw new IOException("Intentional fail");
  }
}
