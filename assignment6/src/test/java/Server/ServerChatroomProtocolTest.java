package Server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerChatroomProtocolTest {

  private ServerChatroomProtocol protocol;
  private ConcurrentHashMap<String, DataOutputStream> sharedBuffers = new ConcurrentHashMap<>();
  private ByteArrayOutputStream bo = new ByteArrayOutputStream();
  private DataOutputStream out = new DataOutputStream(bo);

  @BeforeEach
  void setUp() throws IOException {
  }

  @Test
  void serverProcessConnectMessage() throws IOException {
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArray = new ByteArrayOutputStream();
    DataOutputStream outputTest = new DataOutputStream(outputTestArray);
    protocol = new ServerChatroomProtocol(in, outputTest, sharedBuffers);
    protocol.serverProcess();
    DataInputStream nowWeRead = new DataInputStream(
        new ByteArrayInputStream(outputTestArray.toByteArray()));
    assertEquals(nowWeRead.readInt(), 20);
    assertEquals(nowWeRead.readChar(), ' ');
  }

  @Test
  void serverProcessConnectMessageWithDuplicateUserName() throws IOException {
    sharedBuffers.put("Bob", new DataOutputStream(new ByteArrayOutputStream()));
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArray = new ByteArrayOutputStream();
    DataOutputStream outputTest = new DataOutputStream(outputTestArray);
    protocol = new ServerChatroomProtocol(in, outputTest, sharedBuffers);
    protocol.serverProcess();
    DataInputStream nowWeRead = new DataInputStream(
        new ByteArrayInputStream(outputTestArray.toByteArray()));
    assertEquals(nowWeRead.readInt(), 20);
    assertEquals(nowWeRead.readChar(), ' ');
  }

  @Test
  void serverProcessDisconnectMessage() throws IOException {
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArray = new ByteArrayOutputStream();
    DataOutputStream outputTest = new DataOutputStream(outputTestArray);
    protocol = new ServerChatroomProtocol(in, outputTest, sharedBuffers);
    protocol.serverProcess();
    DataInputStream nowWeRead = new DataInputStream(
        new ByteArrayInputStream(outputTestArray.toByteArray()));
    assertEquals(nowWeRead.readInt(), 20);
    assertEquals(nowWeRead.readChar(), ' ');
  }

  @Test
  void serverProcessQueryUsers() throws IOException {
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));

    out.writeInt(22);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));

    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArray = new ByteArrayOutputStream();
    DataOutputStream outputTest = new DataOutputStream(outputTestArray);
    protocol = new ServerChatroomProtocol(in, outputTest, sharedBuffers);
    protocol.serverProcess();
    DataInputStream nowWeRead = new DataInputStream(
        new ByteArrayInputStream(outputTestArray.toByteArray()));
    assertEquals(nowWeRead.readInt(), 20);
    assertEquals(nowWeRead.readChar(), ' ');
  }

  @Test
  void serverProcessDirectMessage() throws IOException {
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));

    out.writeInt(25);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    String message = "Hello me";
    out.writeChar(' ');
    out.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
    out.writeChar(' ');
    out.write(message.getBytes(StandardCharsets.UTF_8));

    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArray = new ByteArrayOutputStream();
    DataOutputStream outputTest = new DataOutputStream(outputTestArray);
    protocol = new ServerChatroomProtocol(in, outputTest, sharedBuffers);
    protocol.serverProcess();
    DataInputStream nowWeRead = new DataInputStream(
        new ByteArrayInputStream(outputTestArray.toByteArray()));
    assertEquals(nowWeRead.readInt(), 20);
    assertEquals(nowWeRead.readChar(), ' ');
  }

  @Test
  void serverProcessDirectMessageWithIncorrectUserName() throws IOException {
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));

    out.writeInt(25);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    out.writeChar(' ');
    out.writeInt(4);
    out.writeChar(' ');
    out.write("Bobb".getBytes(StandardCharsets.UTF_8));
    String message = "Hello me";
    out.writeChar(' ');

    out.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
    out.writeChar(' ');
    out.write(message.getBytes(StandardCharsets.UTF_8));

    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArray = new ByteArrayOutputStream();
    DataOutputStream outputTest = new DataOutputStream(outputTestArray);
    protocol = new ServerChatroomProtocol(in, outputTest, sharedBuffers);
    protocol.serverProcess();
    DataInputStream nowWeRead = new DataInputStream(
        new ByteArrayInputStream(outputTestArray.toByteArray()));
    assertEquals(nowWeRead.readInt(), 20);
    assertEquals(nowWeRead.readChar(), ' ');
  }

  @Test
  void serverProcessBroadcastMessage() throws IOException {
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));

    out.writeInt(24);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    String message = "Hello me";
    out.writeChar(' ');
    out.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
    out.writeChar(' ');
    out.write(message.getBytes(StandardCharsets.UTF_8));

    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArray = new ByteArrayOutputStream();
    DataOutputStream outputTest = new DataOutputStream(outputTestArray);
    protocol = new ServerChatroomProtocol(in, outputTest, sharedBuffers);
    protocol.serverProcess();
    DataInputStream nowWeRead = new DataInputStream(
        new ByteArrayInputStream(outputTestArray.toByteArray()));
    assertEquals(nowWeRead.readInt(), 20);
    assertEquals(nowWeRead.readChar(), ' ');
  }

  @Test
  void serverProcessSendInsult() throws IOException {
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));

    out.writeInt(27);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));

    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArray = new ByteArrayOutputStream();
    DataOutputStream outputTest = new DataOutputStream(outputTestArray);
    protocol = new ServerChatroomProtocol(in, outputTest, sharedBuffers);
    protocol.serverProcess();
    DataInputStream nowWeRead = new DataInputStream(
        new ByteArrayInputStream(outputTestArray.toByteArray()));
    assertEquals(nowWeRead.readInt(), 20);
    assertEquals(nowWeRead.readChar(), ' ');
  }

  @Test
  void testEquals() throws IOException {
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArray = new ByteArrayOutputStream();
    DataOutputStream outputTest = new DataOutputStream(outputTestArray);
    protocol = new ServerChatroomProtocol(in, outputTest, sharedBuffers);

    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Ann".getBytes(StandardCharsets.UTF_8));
    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Ann".getBytes(StandardCharsets.UTF_8));
    DataInputStream inn = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArrayy = new ByteArrayOutputStream();
    DataOutputStream outputTestt = new DataOutputStream(outputTestArrayy);
    ServerChatroomProtocol protocoll = new ServerChatroomProtocol(inn, outputTestt, sharedBuffers);

    assertEquals(protocol, protocol);
    assertNotEquals(protocol, null);
    assertNotEquals(protocol, 1);
    assertNotEquals(protocol, protocoll);
  }

  @Test
  void testEqualsAnother() throws Exception {
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream innn = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArrayyy = new ByteArrayOutputStream();
    DataOutputStream outputTesttt = new DataOutputStream(outputTestArrayyy);
    ServerChatroomProtocol protocolll = new ServerChatroomProtocol(innn, outputTesttt,
        new ConcurrentHashMap<>());
    assertNotEquals(protocol, protocolll);
  }

  @Test
  void testHashCode() throws IOException {
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bob".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bo.toByteArray()));
    ByteArrayOutputStream outputTestArray = new ByteArrayOutputStream();
    DataOutputStream outputTest = new DataOutputStream(outputTestArray);
    protocol = new ServerChatroomProtocol(in, outputTest, sharedBuffers);
    assertEquals(protocol.hashCode(), protocol.hashCode());
  }
}