package Server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatroomServerThreadTest {

  private Socket socket = mock(Socket.class);
  private ChatroomServerThread thread;
  private ConcurrentHashMap<String, DataOutputStream> sharedBuffers = new ConcurrentHashMap<>();

  @BeforeEach
  void setUp() throws IOException {
    thread = new ChatroomServerThread(sharedBuffers, socket);
    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bOut);
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
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bOut.toByteArray()));
    when(socket.getInputStream()).thenReturn(in);
    ByteArrayOutputStream bOuter = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(bOuter));
  }

  @Test
  void testEquals() throws IOException {
    ChatroomServerThread myThread = new ChatroomServerThread(sharedBuffers, socket);
    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bOut);
    out.writeInt(19);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bie".getBytes(StandardCharsets.UTF_8));
    out.writeInt(21);
    out.writeChar(' ');
    out.writeInt(3);
    out.writeChar(' ');
    out.write("Bie".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bOut.toByteArray()));
    when(socket.getInputStream()).thenReturn(in);
    ByteArrayOutputStream bOuter = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(new DataOutputStream(bOuter));

    assertEquals(thread, thread);
    assertNotEquals(thread, myThread);
    assertNotEquals(thread, null);
    assertNotEquals(thread, 1);
  }

  @Test
  void testHashCode() {
    assertEquals(thread.hashCode(), thread.hashCode());
  }

}