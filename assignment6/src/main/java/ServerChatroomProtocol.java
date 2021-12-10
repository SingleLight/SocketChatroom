import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

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
  private final DataInputStream in;
  private final DataOutputStream out;
  private final ConcurrentHashMap<String, DataOutputStream> sharedBuffers;
  private boolean running = true;

  public ServerChatroomProtocol(DataInputStream in, DataOutputStream out,
      ConcurrentHashMap<String, DataOutputStream> sharedBuffers) {
    this.in = in;
    this.out = out;
    this.sharedBuffers = sharedBuffers;
  }

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
      message = "Connection failed because the username is already used";
      running = false;
    } else {
      sharedBuffers.put(name, out);
      success = true;
      System.out.println("User " + new String(username) + " is connected");
      message =
          "Connection successful " + name + ", there are " + (sharedBuffers.keySet().size() - 1)
              + " other connected clients.";
    }
    synchronized (out) {
      out.writeInt(CONNECT_RESPONSE);
      out.writeChar(' ');
      out.writeBoolean(success);
      out.writeChar(' ');
      byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
      out.writeInt(messageBytes.length);
      out.writeChar(' ');
      out.write(messageBytes);
    }
  }

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
      out.writeChar(' ');
      if (correctUsername) {
        String message = "Success! You are no longer connected";
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        out.writeInt(messageBytes.length);
        out.writeChar(' ');
        out.write(messageBytes);
        sharedBuffers.remove(username);
        running = false;
      } else {
        String message = "Failure! You are not disconnected due to incorrectly provided username";
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        out.writeInt(messageBytes.length);
        out.writeChar(' ');
        out.write(messageBytes);
      }
    }
  }

  private void processQueryUsers() throws IOException {
    in.readChar();
    int usernameSize = in.readInt();
    in.readChar();
    byte[] usernameInBytes = new byte[usernameSize];
    in.read(usernameInBytes);
    String username = new String(usernameInBytes);
    ArrayList<String> users = new ArrayList<>();
    for (String key : sharedBuffers.keySet()) {
      if (!key.equals(username)){
        users.add(key);
      }
    }
    synchronized (out) {
      out.writeInt(QUERY_USER_RESPONSE);
      out.writeChar(' ');
      int numberOfOtherUsers = users.size();
      out.writeInt(numberOfOtherUsers);
      out.writeChar(' ');
      for (String user : users) {
        out.writeInt(user.getBytes(StandardCharsets.UTF_8).length);
        out.writeChar(' ');
        out.write(user.getBytes(StandardCharsets.UTF_8));
        out.writeChar(' ');
      }
    }
  }

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
      sendDirectMessage(new String(fromUsernameInBytes), new String(toUsernameInBytes), new String(messageInBytes));
    } else {
      String failureMessage = "The username you provided is not correct";
      sendFailedMessage(new String(fromUsernameInBytes), failureMessage);
    }
  }


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
      String failure = "the provided username is incorrect";
      System.out.println(failure);
      sendFailedMessage(new String(fromUsernameInBytes), failure);
    } else {
      String insult = new String(fromUsernameInBytes) + " -> " + new String(toUsernameInBytes)
          + InsultGenerator.randomInsult();
      sendBroadcastMessage(new String(fromUsernameInBytes), insult);
    }
  }

  public void sendFailedMessage(String to, String message) throws IOException {
    DataOutputStream out = sharedBuffers.get(to);
    System.out.println("Sending failure");
    synchronized (out) {
      System.out.println("Sending failure in block");
      out.writeInt(FAIL_MESSAGE);
      out.writeChar(' ');
      out.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
      out.writeChar(' ');
      out.write(message.getBytes(StandardCharsets.UTF_8));
      System.out.println("Sending failure at end of block");
    }
    System.out.println("Sending failure out of block");
  }

  public void sendDirectMessage(String from, String to, String message) throws IOException {
    DataOutputStream out = sharedBuffers.get(to);
    synchronized (out) {
      out.writeInt(DIRECT_MESSAGE);
      out.writeChar(' ');
      out.writeInt(from.getBytes(StandardCharsets.UTF_8).length);
      out.writeChar(' ');
      out.write(from.getBytes(StandardCharsets.UTF_8));
      out.writeChar(' ');
      out.writeInt(to.getBytes(StandardCharsets.UTF_8).length);
      out.writeChar(' ');
      out.write(to.getBytes(StandardCharsets.UTF_8));
      out.writeChar(' ');
      out.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
      out.writeChar(' ');
      out.write(message.getBytes(StandardCharsets.UTF_8));
    }
  }

  public void sendBroadcastMessage(String from, String message) throws IOException {
    for (String key : sharedBuffers.keySet()) {
      sendDirectMessage(from, key, message);
    }
  }

}
