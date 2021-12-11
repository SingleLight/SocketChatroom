package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * server thread that handles a socket connection
 */
public class ChatroomServerThread implements Runnable {

  private static final String SOCKET_CLOSED = "Socket closed";
  private final ConcurrentHashMap<String, DataOutputStream> sharedBuffers;
  private final Socket socket;
  private final ServerChatroomProtocol protocol;

  /**
   * constructor
   *
   * @param sharedBuffers shared map of output streams
   * @param socket        socket connection
   * @throws IOException error in stream read write
   */
  public ChatroomServerThread(
      ConcurrentHashMap<String, DataOutputStream> sharedBuffers, Socket socket) throws IOException {
    this.sharedBuffers = sharedBuffers;
    this.socket = socket;
    this.protocol = new ServerChatroomProtocol(new DataInputStream(socket.getInputStream()),
        new DataOutputStream(socket.getOutputStream()), sharedBuffers);
  }

  /**
   * run server process closes afterwards
   */
  @Override
  public void run() {
    try {
      protocol.serverProcess();
      socket.close();
      System.out.println(SOCKET_CLOSED);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChatroomServerThread that = (ChatroomServerThread) o;
    return sharedBuffers.equals(that.sharedBuffers) && socket.equals(that.socket)
        && protocol.equals(
        that.protocol);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sharedBuffers, socket, protocol);
  }
}
