import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ChatroomServerThread implements Runnable {

  private final ConcurrentHashMap<String, DataOutputStream> sharedBuffers;
  private final Socket socket;
  private final ServerChatroomProtocol protocol;

  public ChatroomServerThread(
      ConcurrentHashMap<String, DataOutputStream> sharedBuffers, Socket socket) throws IOException {
    this.sharedBuffers = sharedBuffers;
    this.socket = socket;
    this.protocol = new ServerChatroomProtocol(new DataInputStream(socket.getInputStream()),
        new DataOutputStream(socket.getOutputStream()), sharedBuffers);
  }

  @Override
  public void run() {
    try {
      protocol.serverProcess();
      socket.close();
      System.out.println("Socket closed");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
