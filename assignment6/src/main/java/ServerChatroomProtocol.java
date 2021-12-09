import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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
  private boolean running = true;
  private final DataInputStream in;
  private final DataOutputStream out;
  private final ConcurrentHashMap<String, DataOutputStream> sharedBuffers;

  public ServerChatroomProtocol(DataInputStream in, DataOutputStream out,
      ConcurrentHashMap<String, DataOutputStream> sharedBuffers) {
    this.in = in;
    this.out = out;
    this.sharedBuffers = sharedBuffers;
  }

  public synchronized static void writeToUserConsole(String username,
      ConcurrentHashMap<String, DataOutputStream> sharedBuffers, String message) {
    try {
      DataOutputStream eachOut = sharedBuffers.get(username);
      synchronized (eachOut) {
        eachOut.write(message.getBytes(StandardCharsets.UTF_8));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
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
      }
    }
  }

  public void processConnectMessage()
      throws IOException {
    in.readChar();
    int usernameSize = in.readInt();
    System.out.println(usernameSize);
    in.readChar();
    byte[] username = new byte[usernameSize];
    in.read(username);
    String name = new String(username, StandardCharsets.UTF_8);
    System.out.println(name);
    boolean success = false;
    String message = "";

    if (sharedBuffers.containsKey(name)) {
      writeToUserConsole(new String(username), sharedBuffers, "The user name is already used");
      message = "Connection failed because the username is already used";
    } else {
      sharedBuffers.put(name, out);
      success = true;
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
    in.read(usernameInBytes, 0, usernameSize);
    String username = new String(usernameInBytes);
    boolean correctUsername = this.sharedBuffers.containsKey(username);
    synchronized (out) {
      out.writeInt(CONNECT_RESPONSE);
      out.writeChar(' ');
      if (correctUsername) {
        String message = "Success! You are no longer disconnected";
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        out.writeInt(messageBytes.length);
        out.writeChar(' ');
        out.write(messageBytes);
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
    byte[] sender = "Server".getBytes(StandardCharsets.UTF_8);
    synchronized (out) {
      out.write(DIRECT_MESSAGE);
      out.writeChar(' ');
      out.writeInt(sender.length);
      out.writeChar(' ');
      out.write(sender);
      out.writeChar(' ');
      out.writeInt(usernameSize);
      out.writeChar(' ');
      out.write(usernameInBytes);
      StringBuilder output = new StringBuilder();
      for (String key : sharedBuffers.keySet()) {
        output.append(key).append(' ');
      }
      byte[] outputInBytes = output.toString().getBytes(StandardCharsets.UTF_8);
      out.writeChar(' ');
      out.writeInt(outputInBytes.length);
      out.writeChar(' ');
      out.write(outputInBytes);
    }
  }

  private void processDirectMessage() throws IOException {
    in.readChar();
    int fromUsernameSize = in.readInt();
    in.readChar();
    byte[] fromUsernameInBytes = new byte[fromUsernameSize];
    in.readChar();
    in.read(fromUsernameInBytes, 0, fromUsernameSize);
    in.readChar();
    int toUsernameSize = in.readInt();
    in.readChar();
    byte[] toUsernameInBytes = new byte[toUsernameSize];
    in.readChar();
    int messageSize = in.readInt();
    in.readChar();
    byte[] messageInBytes = new byte[messageSize];
    in.read(messageInBytes, 0, messageSize);

    if (sharedBuffers.containsKey(new String(toUsernameInBytes))) {
      DataOutputStream directOut = sharedBuffers.get(new String(toUsernameInBytes));
      synchronized (directOut) {
        directOut.write(DIRECT_MESSAGE);
        directOut.writeChar(' ');
        directOut.writeInt(fromUsernameSize);
        directOut.writeChar(' ');
        directOut.write(fromUsernameInBytes);
        directOut.writeChar(' ');
        directOut.writeInt(toUsernameSize);
        directOut.writeChar(' ');
        directOut.writeInt(messageSize);
        directOut.writeChar(' ');
        directOut.write(messageInBytes, 0, messageSize);
      }
    } else {
      out.writeInt(FAIL_MESSAGE);
      String failureMessage = "The username you provided is not correct";
      byte[] failureMessageInBytes = failureMessage.getBytes(StandardCharsets.UTF_8);
      out.writeChar(' ');
      out.write(failureMessageInBytes.length);
      out.writeChar(' ');
      out.write(failureMessageInBytes, 0, failureMessageInBytes.length);
    }
  }


  public void processBroadcastMessage() throws IOException {
    in.readChar();
    int fromUsernameSize = in.readInt();
    in.readChar();
    byte[] fromUsernameInByte = new byte[fromUsernameSize];
    in.read(fromUsernameInByte, 0, fromUsernameSize);
    in.readChar();
    int messageSize = in.readInt();
    in.readChar();
    byte[] messageInBytes = new byte[messageSize];
    in.read(messageInBytes, 0, messageSize);

    for (String key : sharedBuffers.keySet()) {
      DataOutputStream broadcastOutput = sharedBuffers.get(key);
      synchronized (broadcastOutput) {
        broadcastOutput.write(DIRECT_MESSAGE);
        broadcastOutput.writeChar(' ');
        String from = "Server";
        byte[] fromInBytes = from.getBytes(StandardCharsets.UTF_8);
        broadcastOutput.write(fromInBytes.length);
        broadcastOutput.writeChar(' ');
        broadcastOutput.write(fromInBytes, 0, fromInBytes.length);
        broadcastOutput.write(' ');
        broadcastOutput.writeInt(key.getBytes().length);
        broadcastOutput.writeChar(' ');
        broadcastOutput.write(key.getBytes(StandardCharsets.UTF_8), 0,
            key.getBytes(StandardCharsets.UTF_8).length);
        broadcastOutput.writeChar(' ');
        broadcastOutput.writeInt(messageSize);
        broadcastOutput.write(' ');
        broadcastOutput.write(messageInBytes, 0, messageSize);
      }
    }
  }

}
