package kiyo.battleship.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.List;

/**
 * Mock Socket to test ProxyController, connects to a mock server.
 */
public class MockSocket extends Socket {

  private final InputStream testInputs;
  private final ByteArrayOutputStream fromClient;

  /**
   * @param fromClient Content from the client to the server
   * @param fromServer Content from the server to the client
   */
  public MockSocket(ByteArrayOutputStream fromClient, List<String> fromServer) {
    this.fromClient = fromClient;
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    for (String message : fromServer) {
      printWriter.println(message);
    }
    this.testInputs = new ByteArrayInputStream(stringWriter.toString().getBytes());
  }

  @Override
  public InputStream getInputStream() {
    return this.testInputs;
  }

  @Override
  public OutputStream getOutputStream() {
    return this.fromClient;
  }
}