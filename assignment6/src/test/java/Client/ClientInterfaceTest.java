package Client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientInterfaceTest {

  private DataInputStream in;
  private DataOutputStream out;
  private ClientInterface clientInterface;
  private Socket socket = mock(Socket.class);

  @BeforeEach
  void setUp() throws IOException {

  }

  @Test
  void task() {
  }

  @Test
  void logOff() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream("logoff".getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream("".getBytes(
            StandardCharsets.UTF_8))));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
  }

  @Test
  void listAllUsers() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "who\nlogoff".getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream("".getBytes(
            StandardCharsets.UTF_8))));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
  }

  @Test
  void directMessage() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "@user Bob Gee\nlogoff".getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream("".getBytes(
            StandardCharsets.UTF_8))));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
  }

  @Test
  void broadcastMessage() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "Wat\nlogoff".getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream("".getBytes(
            StandardCharsets.UTF_8))));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
  }

  @Test
  void help() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "?\nlogoff".getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream("".getBytes(
            StandardCharsets.UTF_8))));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
  }

  @Test
  void sendInsult() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "!user Bob\nlogoff".getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream("".getBytes(
            StandardCharsets.UTF_8))));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
  }

  @Test
  void processFailedMessage() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "!user Bob\nlogoff".getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(bout);
    dout.writeInt(26);
    dout.writeChar(' ');
    String message = "BAD!";
    dout.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
    dout.writeChar(' ');
    dout.write(message.getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream(bout.toByteArray())));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
  }

  @Test
  void processConnectResponse() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "!user Bob\nlogoff".getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(bout);
    dout.writeInt(20);
    dout.writeChar(' ');
    dout.writeBoolean(true);
    dout.writeChar(' ');
    String message = "Success!";
    dout.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
    dout.writeChar(' ');
    dout.write(message.getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream(bout.toByteArray())));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
  }

  @Test
  void processDirectMessage() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "!user Bob\nlogoff".getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(bout);
    dout.writeInt(25);
    dout.writeChar(' ');
    dout.writeInt("Bob".getBytes(StandardCharsets.UTF_8).length);
    dout.writeChar(' ');
    dout.write("Bob".getBytes(StandardCharsets.UTF_8));
    dout.writeChar(' ');
    dout.writeInt("Bob".getBytes(StandardCharsets.UTF_8).length);
    dout.writeChar(' ');
    dout.write("Bob".getBytes(StandardCharsets.UTF_8));
    dout.writeChar(' ');
    dout.writeInt("Message".getBytes(StandardCharsets.UTF_8).length);
    dout.writeChar(' ');
    dout.write("Message".getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream(bout.toByteArray())));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
  }

  @Test
  void processQueryUsers() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "!user Bob\nlogoff".getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(bout);
    dout.writeInt(23);
    dout.writeChar(' ');
    dout.writeInt(1);
    dout.writeChar(' ');
    String username = "Bob";
    dout.writeInt(username.getBytes(StandardCharsets.UTF_8).length);
    dout.writeChar(' ');
    dout.write(username.getBytes(StandardCharsets.UTF_8));
    dout.writeChar(' ');
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream(bout.toByteArray())));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
  }

  @Test
  void testEquals() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "!user Bob\nlogoff".getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(bout);
    dout.writeInt(20);
    dout.writeChar(' ');
    dout.writeBoolean(true);
    dout.writeChar(' ');
    String message = "Success!";
    dout.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
    dout.writeChar(' ');
    dout.write(message.getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream(bout.toByteArray())));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
    assertEquals(clientInterface, clientInterface);
    assertNotEquals(clientInterface, null);
    assertNotEquals(clientInterface, 1);
  }

  @Test
  void testHashCode() throws IOException {
    InputStream consoleInput = new ByteArrayInputStream(
        "!user Bob\nlogoff".getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(bout);
    dout.writeInt(20);
    dout.writeChar(' ');
    dout.writeBoolean(true);
    dout.writeChar(' ');
    String message = "Success!";
    dout.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
    dout.writeChar(' ');
    dout.write(message.getBytes(StandardCharsets.UTF_8));
    when(socket.getInputStream()).thenReturn(
        new DataInputStream(new ByteArrayInputStream(bout.toByteArray())));
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(testOut));
    clientInterface = new ClientInterface(socket);
    clientInterface.task(consoleInput);
    assertEquals(clientInterface.hashCode(), clientInterface.hashCode());
  }
}