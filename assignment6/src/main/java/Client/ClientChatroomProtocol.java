package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * manages client communication protocols
 */
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
  private static final String FAILURE_PRINT_START = "Connection failed";
  private static final String SUCCESS_PRINT_START = "Success";
  private static final String MESSAGE_FROM = "Message from ";
  private static final String MESSAGE_TO = " to ";
  private static final String COLON = " : ";
  private static final String MESSAGE_USERS = "Users: ";
  private static final String WHITESPACE = " ";
  private static final char EMPTY_CHAR = ' ';
  private final DataInputStream in;
  private final DataOutputStream out;
  private boolean running = true;
  private String username = "";
  private String usernameToBe = "";
  private state myState = state.NOT_CONNECTED;

  /**
   * constructor
   *
   * @param in  input stream
   * @param out output stream
   */
  public ClientChatroomProtocol(DataInputStream in, DataOutputStream out) {
    this.in = in;
    this.out = out;
  }

  /**
   * listens for incoming byte stream and parse it to call the appropriate method
   *
   * @throws IOException error in stream read write
   */
  public void clientProcess() throws IOException {
    while (running) {
      int messageType = in.readInt();
      switch (messageType) {
        case CONNECT_RESPONSE -> processConnectResponse();
        case DIRECT_MESSAGE -> processDirectMessage();
        case QUERY_USER_RESPONSE -> processQueryUsers();
        case FAIL_MESSAGE -> processFailedMessage();
      }
    }
  }

  /**
   * process the connect response and notify the user
   *
   * @return if the connection is successful
   * @throws IOException error in stream read write
   */
  private boolean processConnectResponse() throws IOException {
    if (myState == state.NOT_CONNECTED) {
      in.readChar();
      boolean isConnected = in.readBoolean();
      in.readChar();
      int messageSize = in.readInt();
      in.readChar();
      byte[] messageInBytes = new byte[messageSize];
      in.read(messageInBytes);
      System.out.println(new String(messageInBytes));
      if (new String(messageInBytes).startsWith(FAILURE_PRINT_START)) {
        running = false;
        System.exit(0);
      }
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
      if (message.startsWith(SUCCESS_PRINT_START)) {
        running = false;
        this.myState = state.NOT_CONNECTED;
        System.exit(0);
        return true;
      } else {
        return false;
      }
    }
  }

  /**
   * process direct message from server and print to user
   *
   * @throws IOException error in stream read write
   */
  private void processDirectMessage() throws IOException {
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
    System.out.println(
        MESSAGE_FROM + new String(fromUsernameInBytes) + MESSAGE_TO + new String(toUsernameInBytes)
            + COLON + new String(messageInBytes));
  }

  /**
   * process query user response from server and print to usere
   *
   * @throws IOException error in stream read write
   */
  private void processQueryUsers() throws IOException {
    in.readChar();
    int numberOfOtherUsers = in.readInt();
    in.readChar();
    StringBuilder allTheUsers = new StringBuilder(MESSAGE_USERS);
    for (int i = 0; i < numberOfOtherUsers; i++) {
      int usernameSize = in.readInt();
      in.readChar();
      byte[] usernameInBytes = new byte[usernameSize];
      in.read(usernameInBytes);
      in.readChar();
      allTheUsers.append(new String(usernameInBytes)).append(WHITESPACE);
    }
    System.out.println(allTheUsers);
  }

  /**
   * process failed message from server and send to user
   *
   * @throws IOException error in stream read write
   */
  private void processFailedMessage() throws IOException {
    in.readChar();
    int sizeOfMessage = in.readInt();
    in.readChar();
    byte[] messageInBytes = new byte[sizeOfMessage];
    in.read(messageInBytes);
    System.out.println(new String(messageInBytes));
  }

  /**
   * connect to the server
   *
   * @param username the username of the client
   * @throws IOException error in stream read write
   */
  public void connect(String username) throws IOException {
    synchronized (out) {
      out.writeInt(CONNECT_MESSAGE);
      out.writeChar(EMPTY_CHAR);
      byte[] usernameInBytes = username.getBytes(StandardCharsets.UTF_8);
      out.writeInt(usernameInBytes.length);
      out.writeChar(EMPTY_CHAR);
      out.write(usernameInBytes);
    }
    usernameToBe = username;
  }

  /**
   * logoff from the server
   *
   * @param name the name of the client
   * @throws IOException error in stream read write
   */
  public void logOff(String name) throws IOException {
    synchronized (out) {
      out.writeInt(DISCONNECT_MESSAGE);
      out.writeChar(EMPTY_CHAR);
      byte[] usernameInBytes = name.getBytes(StandardCharsets.UTF_8);
      out.writeInt(usernameInBytes.length);
      out.writeChar(EMPTY_CHAR);
      out.write(usernameInBytes);
    }
  }

  /**
   * ask the server to query all users
   *
   * @param username the name of the client
   * @throws IOException error in stream read write
   */
  public void listAllUsers(String username) throws IOException {
    synchronized (out) {
      out.writeInt(QUERY_CONNECTED_USERS);
      out.writeChar(EMPTY_CHAR);
      byte[] usernameInBytes = username.getBytes(StandardCharsets.UTF_8);
      out.writeInt(usernameInBytes.length);
      out.writeChar(EMPTY_CHAR);
      out.write(usernameInBytes);
    }
  }

  /**
   * send a direct message to the server
   *
   * @param from    from username
   * @param to      to username
   * @param message the content of message
   * @throws IOException error in stream read write
   */
  public void directMessage(String from, String to, String message)
      throws IOException {
    synchronized (out) {
      out.writeInt(DIRECT_MESSAGE);
      out.writeChar(EMPTY_CHAR);
      byte[] fromBytes = from.getBytes(StandardCharsets.UTF_8);
      byte[] toBytes = to.getBytes(StandardCharsets.UTF_8);
      byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
      out.writeInt(fromBytes.length);
      out.writeChar(EMPTY_CHAR);
      out.write(fromBytes);
      out.writeChar(EMPTY_CHAR);
      out.writeInt(toBytes.length);
      out.writeChar(EMPTY_CHAR);
      out.write(toBytes);
      out.writeChar(EMPTY_CHAR);
      out.writeInt(messageBytes.length);
      out.writeChar(EMPTY_CHAR);
      out.write(messageBytes);
    }
  }

  /**
   * send a broadcast message to the server
   *
   * @param from    from username
   * @param message the content of the message
   * @throws IOException error in stream read write
   */
  public void broadcastMessage(String from, String message) throws IOException {
    synchronized (out) {
      out.writeInt(BROADCAST_MESSAGE);
      out.writeChar(EMPTY_CHAR);
      byte[] fromInBytes = from.getBytes(StandardCharsets.UTF_8);
      out.writeInt(fromInBytes.length);
      out.writeChar(EMPTY_CHAR);
      out.write(fromInBytes);
      out.writeChar(EMPTY_CHAR);
      byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
      out.writeInt(messageBytes.length);
      out.writeChar(EMPTY_CHAR);
      out.write(messageBytes);
    }
  }

  /**
   * send an insult to a user
   *
   * @param from from username
   * @param to   to username
   * @throws IOException error in stream read write
   */
  public void sendInsult(String from, String to) throws IOException {
    synchronized (out) {
      out.writeInt(SEND_INSULT);
      out.writeChar(EMPTY_CHAR);
      out.writeInt(from.getBytes(StandardCharsets.UTF_8).length);
      out.writeChar(EMPTY_CHAR);
      out.write(from.getBytes(StandardCharsets.UTF_8));
      out.writeChar(EMPTY_CHAR);
      out.writeInt(to.getBytes(StandardCharsets.UTF_8).length);
      out.writeChar(EMPTY_CHAR);
      out.write(to.getBytes(StandardCharsets.UTF_8));
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
    ClientChatroomProtocol that = (ClientChatroomProtocol) o;
    return running == that.running && in.equals(that.in) && out.equals(that.out) && username.equals(
        that.username) && usernameToBe.equals(that.usernameToBe)
        && myState == that.myState;
  }

  @Override
  public int hashCode() {
    return Objects.hash(in, out, running, username, usernameToBe, myState);
  }

  /**
   * connection status
   */
  public enum state {
    CONNECTED,
    NOT_CONNECTED
  }
}
