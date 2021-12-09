import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientChatroomProtocol {

  private static final int CONNECT_MESSAGE = 19;
  private static final int CONNECT_RESPONSE = 20;
  private static final int DISCONNECT_MESSAGE = 21;
  private static final int QUERY_CONNECTED_USERS = 22;
  private static final int QUERY_USER_RESPONSE = 23;
  private static final int BROADCAST_MESSAGE = 24;
  private static final int DIRECT_MESSAGE = 25;
  private static final int FAIL_MESSAGE = 26;
  private static final int SEND_INSULT = 27;
  private final DataInputStream in;
  private final DataOutputStream out;
  private final Socket socket;
  private boolean running = true;
  private String username = "";
  private String usernameToBe = "";
  private state myState = state.NOT_CONNECTED;

  public ClientChatroomProtocol(DataInputStream in, DataOutputStream out, Socket socket) {
    this.in = in;
    this.out = out;
    this.socket = socket;
  }

  public synchronized void clientProcess() throws IOException {
    while (running) {
      int messageType = in.readInt();
      switch (messageType) {
        case CONNECT_RESPONSE -> processConnectResponse();
        case DIRECT_MESSAGE -> processDirectMessage();
      }
    }
  }

  private synchronized boolean processConnectResponse() throws IOException {
    if (myState == state.NOT_CONNECTED) {
      in.readChar();
      boolean isConnected = in.readBoolean();
      in.readChar();
      int messageSize = in.readInt();
      in.readChar();
      byte[] messageInBytes = new byte[messageSize];
      in.read(messageInBytes);
      System.out.println(new String(messageInBytes));
      this.myState = (isConnected) ? state.CONNECTED : state.NOT_CONNECTED;
      if (isConnected) {
        username = usernameToBe;
      }
      return true;
    } else {
      in.readChar();
      int messageSize = in.readInt();
      in.readChar();
      byte[] messageInBytes = new byte[messageSize];
      in.read(messageInBytes);
      String message = new String(messageInBytes);
      System.out.println(message);
      if (message.startsWith("Success")) {
        in.close();
        out.close();
        socket.close();
        running = false;
        this.myState = state.NOT_CONNECTED;
        System.exit(0);
        return true;
      } else {
        return false;
      }
    }
  }

  private synchronized void processDirectMessage() throws IOException {
    in.readChar();
    int fromUsernameSize = in.readInt();
    in.readChar();
    byte[] fromUsernameInBytes = new byte[fromUsernameSize];
    in.read(fromUsernameInBytes);
    in.readChar();
    int toUsernameSize = in.readInt();
    in.readChar();
    byte[] toUsernameInBytes = new byte[toUsernameSize];
    in.read(toUsernameInBytes);
    in.readChar();
    int messageSize = in.readInt();
    in.readChar();
    byte[] messageInBytes = new byte[messageSize];
    in.read(messageInBytes);
    System.out.println("Message from" + new String(fromUsernameInBytes) + " to " + new String(toUsernameInBytes) + " : " + new String(messageInBytes));
  }

  public synchronized void connect(String username) throws IOException {
    out.writeInt(CONNECT_MESSAGE);
    out.writeChar(' ');
    byte[] usernameInBytes = username.getBytes(StandardCharsets.UTF_8);
    out.writeInt(usernameInBytes.length);
    out.writeChar(' ');
    out.write(usernameInBytes);
    usernameToBe = username;
  }

  public synchronized void logOff(String name) throws IOException {
    out.writeInt(DISCONNECT_MESSAGE);
    out.writeChar(' ');
    byte[] usernameInBytes = name.getBytes(StandardCharsets.UTF_8);
    out.writeInt(usernameInBytes.length);
    out.writeChar(' ');
    out.write(usernameInBytes);
  }

  public synchronized void listAllUsers(String username) throws IOException {
    out.writeInt(QUERY_CONNECTED_USERS);
    out.writeChar(' ');
    byte[] usernameInBytes = username.getBytes(StandardCharsets.UTF_8);
    out.writeInt(usernameInBytes.length);
    out.writeChar(' ');
    out.write(usernameInBytes);
  }

  public synchronized void directMessage(String from, String to, String message)
      throws IOException {
    out.writeInt(DIRECT_MESSAGE);
    out.writeChar(' ');
    byte[] fromBytes = from.getBytes(StandardCharsets.UTF_8);
    byte[] toBytes = to.getBytes(StandardCharsets.UTF_8);
    byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
    out.writeInt(fromBytes.length);
    out.writeChar(' ');
    out.write(fromBytes);
    out.writeChar(' ');
    out.writeInt(toBytes.length);
    out.writeChar(' ');
    out.write(toBytes);
    out.writeChar(' ');
    out.write(messageBytes.length);
    out.writeChar(' ');
    out.write(messageBytes);
  }

  public synchronized void broadcastMessage(String from, String message) throws IOException {
    out.writeInt(BROADCAST_MESSAGE);
    out.writeChar(' ');
    byte[] fromInBytes = from.getBytes(StandardCharsets.UTF_8);
    out.writeInt(fromInBytes.length);
    out.writeChar(' ');
    out.write(fromInBytes);
    out.writeChar(' ');
    byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
    out.writeInt(messageBytes.length);
    out.writeChar(' ');
    out.write(messageBytes);
  }

  public synchronized void sendInsult(String from, String to) throws IOException {
    out.writeInt(SEND_INSULT);
    out.writeChar(' ');
    out.writeInt(from.getBytes(StandardCharsets.UTF_8).length);
    out.writeChar(' ');
    out.write(from.getBytes(StandardCharsets.UTF_8));
    out.writeChar(' ');
    out.writeInt(to.getBytes(StandardCharsets.UTF_8).length);
    out.writeChar(' ');
    out.write(to.getBytes(StandardCharsets.UTF_8));
  }


  public enum state {
    CONNECTED,
    NOT_CONNECTED
  }

}
