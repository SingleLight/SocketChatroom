package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * chatroom protocol for server
 */
public class ServerChatroomProtocol {

  private static final int CONNECT_MESSAGE = 19;
  private static final int CONNECT_RESPONSE = 20;
  private static final int DISCONNECT_MESSAGE = 21;
  private static final int QUERY_CONNECTED_USERS = 22;
  private static final int QUERY_USER_RESPONSE = 23;
  private static final int BROADCAST_MESSAGE = 24;
  private static final int DIRECT_MESSAGE = 25;
  private static final int FAIL_MESSAGE = 26;
  private static final int SEND_INSULT = 27;
  private static final String USERNAME_ALREADY_USED = "Connection failed because the username is already used";
  private static final String USER = "User ";
  private static final String IS_CONNECTED = " is connected";
  private static final String CONNECTION_SUCCESSFUL = "Connection successful ";
  private static final String THERE_ARE = ", there are ";
  private static final String OTHER_CONNECTED_CLIENTS = " other connected clients.";
  private static final String DISCONNECTION_SUCCESSFUL_MESSAGE = "Success! You are no longer connected";
  private static final String DISCONNECTION_FAILURE_MESSAGE = "Failure! You are not disconnected due to incorrectly provided username";
  private static final String INCORRECT_USERNAME_MESSAGE = "The username you provided is not correct";
  private static final String RIGHT_ARROW = " -> ";
  private static final char WHITESPACE_CHAR = ' ';

  private final DataInputStream in;
  private final DataOutputStream out;
  private final ConcurrentHashMap<String, DataOutputStream> sharedBuffers;
  private boolean running = true;

  /**
   * constructor
   *
   * @param in            input stream
   * @param out           output stream
   * @param sharedBuffers map of all the output streams
   */
  public ServerChatroomProtocol(DataInputStream in, DataOutputStream out,
      ConcurrentHashMap<String, DataOutputStream> sharedBuffers) {
    this.in = in;
    this.out = out;
    this.sharedBuffers = sharedBuffers;
  }

  /**
   * keeps listening for incoming byte streams
   *
   * @throws IOException error in stream read write
   */
  public void serverProcess() throws IOException {
    while (running) {
      int messageType = in.readInt();
      switch (messageType) {
        case CONNECT_MESSAGE -> processConnectMessage();
        case DISCONNECT_MESSAGE -> processDisconnectMessage();
        case QUERY_CONNECTED_USERS -> processQueryUsers();
        case DIRECT_MESSAGE -> processDirectMessage();
        case BROADCAST_MESSAGE -> processBroadcastMessage();
        case SEND_INSULT -> processSendInsult();
      }
    }
  }

  /**
   * process a connect message from the client
   *
   * @throws IOException error in stream read write
   */
  public void processConnectMessage()
      throws IOException {
    in.readChar();
    int usernameSize = in.readInt();
    in.readChar();
    byte[] username = new byte[usernameSize];
    in.read(username);
    String name = new String(username, StandardCharsets.UTF_8);
    boolean success = false;
    String message = "";

    if (sharedBuffers.containsKey(name)) {
      message = USERNAME_ALREADY_USED;
      running = false;
    } else {
      sharedBuffers.put(name, out);
      success = true;
      System.out.println(USER + new String(username) + IS_CONNECTED);
      message =
          CONNECTION_SUCCESSFUL + name + THERE_ARE + (sharedBuffers.keySet().size() - 1)
              + OTHER_CONNECTED_CLIENTS;
    }
    synchronized (out) {
      out.writeInt(CONNECT_RESPONSE);
      out.writeChar(WHITESPACE_CHAR);
      out.writeBoolean(success);
      out.writeChar(WHITESPACE_CHAR);
      byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
      out.writeInt(messageBytes.length);
      out.writeChar(WHITESPACE_CHAR);
      out.write(messageBytes);
    }
  }

  /**
   * process a disconnect message from the client
   *
   * @throws IOException error in stream read write
   */
  public void processDisconnectMessage() throws IOException {
    in.readChar();
    int usernameSize = in.readInt();
    in.readChar();
    byte[] usernameInBytes = new byte[usernameSize];
    in.read(usernameInBytes);
    String username = new String(usernameInBytes);
    boolean correctUsername = this.sharedBuffers.containsKey(username);
    synchronized (out) {
      out.writeInt(CONNECT_RESPONSE);
      out.writeChar(WHITESPACE_CHAR);
      if (correctUsername) {
        String message = DISCONNECTION_SUCCESSFUL_MESSAGE;
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        out.writeInt(messageBytes.length);
        out.writeChar(WHITESPACE_CHAR);
        out.write(messageBytes);
        sharedBuffers.remove(username);
        running = false;
      } else {
        String message = DISCONNECTION_FAILURE_MESSAGE;
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        out.writeInt(messageBytes.length);
        out.writeChar(WHITESPACE_CHAR);
        out.write(messageBytes);
      }
    }
  }

  /**
   * process a query user request from the client
   *
   * @throws IOException error in stream read write
   */
  private void processQueryUsers() throws IOException {
    in.readChar();
    int usernameSize = in.readInt();
    in.readChar();
    byte[] usernameInBytes = new byte[usernameSize];
    in.read(usernameInBytes);
    String username = new String(usernameInBytes);
    ArrayList<String> users = new ArrayList<>();
    for (String key : sharedBuffers.keySet()) {
      if (!key.equals(username)) {
        users.add(key);
      }
    }
    synchronized (out) {
      out.writeInt(QUERY_USER_RESPONSE);
      out.writeChar(WHITESPACE_CHAR);
      int numberOfOtherUsers = users.size();
      out.writeInt(numberOfOtherUsers);
      out.writeChar(WHITESPACE_CHAR);
      for (String user : users) {
        out.writeInt(user.getBytes(StandardCharsets.UTF_8).length);
        out.writeChar(WHITESPACE_CHAR);
        out.write(user.getBytes(StandardCharsets.UTF_8));
        out.writeChar(WHITESPACE_CHAR);
      }
    }
  }

  /**
   * process a direct message request from the client
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
    if (sharedBuffers.containsKey(new String(toUsernameInBytes))) {
      sendDirectMessage(new String(fromUsernameInBytes), new String(toUsernameInBytes),
          new String(messageInBytes));
    } else {
      String failureMessage = INCORRECT_USERNAME_MESSAGE;
      sendFailedMessage(new String(fromUsernameInBytes), failureMessage);
    }
  }

  /**
   * process a broadcast message from the client
   *
   * @throws IOException error in stream read write
   */
  public void processBroadcastMessage() throws IOException {
    in.readChar();
    int fromUsernameSize = in.readInt();
    in.readChar();
    byte[] fromUsernameInByte = new byte[fromUsernameSize];
    in.read(fromUsernameInByte);
    in.readChar();
    int messageSize = in.readInt();
    in.readChar();
    byte[] messageInBytes = new byte[messageSize];
    in.read(messageInBytes);
    sendBroadcastMessage(new String(fromUsernameInByte), new String(messageInBytes));
  }

  /**
   * process an insult request from the client
   *
   * @throws IOException error in stream read write
   */
  public void processSendInsult() throws IOException {
    in.readChar();
    int fromUsernameSize = in.readInt();
    System.out.println(fromUsernameSize);
    in.readChar();
    byte[] fromUsernameInBytes = new byte[fromUsernameSize];
    in.read(fromUsernameInBytes);
    System.out.println(new String(fromUsernameInBytes));
    in.readChar();
    int toUsernameSize = in.readInt();
    System.out.println(toUsernameSize);
    in.readChar();
    byte[] toUsernameInBytes = new byte[toUsernameSize];
    in.read(toUsernameInBytes);
    System.out.println(new String(toUsernameInBytes));
    String toUsername = new String(toUsernameInBytes);
    if (!sharedBuffers.containsKey(toUsername)) {
      String failure = INCORRECT_USERNAME_MESSAGE;
      System.out.println(failure);
      sendFailedMessage(new String(fromUsernameInBytes), failure);
    } else {
      String insult = new String(fromUsernameInBytes) + RIGHT_ARROW + new String(toUsernameInBytes)
          + WHITESPACE_CHAR + InsultGenerator.randomInsult();
      sendBroadcastMessage(new String(fromUsernameInBytes), insult);
    }
  }

  /**
   * send a failed message to the client
   *
   * @param to      to username
   * @param message message content
   * @throws IOException error in stream read write
   */
  public void sendFailedMessage(String to, String message) throws IOException {
    DataOutputStream out = sharedBuffers.get(to);
    synchronized (out) {
      out.writeInt(FAIL_MESSAGE);
      out.writeChar(WHITESPACE_CHAR);
      out.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
      out.writeChar(WHITESPACE_CHAR);
      out.write(message.getBytes(StandardCharsets.UTF_8));
    }
  }

  /**
   * send a direct message to the client
   *
   * @param from    from username
   * @param to      to username
   * @param message message content
   * @throws IOException error in stream read write
   */
  public void sendDirectMessage(String from, String to, String message) throws IOException {
    DataOutputStream out = sharedBuffers.get(to);
    synchronized (out) {
      out.writeInt(DIRECT_MESSAGE);
      out.writeChar(WHITESPACE_CHAR);
      out.writeInt(from.getBytes(StandardCharsets.UTF_8).length);
      out.writeChar(WHITESPACE_CHAR);
      out.write(from.getBytes(StandardCharsets.UTF_8));
      out.writeChar(WHITESPACE_CHAR);
      out.writeInt(to.getBytes(StandardCharsets.UTF_8).length);
      out.writeChar(WHITESPACE_CHAR);
      out.write(to.getBytes(StandardCharsets.UTF_8));
      out.writeChar(WHITESPACE_CHAR);
      out.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
      out.writeChar(WHITESPACE_CHAR);
      out.write(message.getBytes(StandardCharsets.UTF_8));
    }
  }

  /**
   * send broadcast message to the clients
   *
   * @param from    from username
   * @param message message content
   * @throws IOException error in stream read write
   */
  public void sendBroadcastMessage(String from, String message) throws IOException {
    for (String key : sharedBuffers.keySet()) {
      sendDirectMessage(from, key, message);
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
    ServerChatroomProtocol that = (ServerChatroomProtocol) o;
    return running == that.running && in.equals(that.in) && out.equals(that.out)
        && sharedBuffers.equals(that.sharedBuffers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(in, out, sharedBuffers, running);
  }
}
