package Client;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientListeningThreadTest {
  private ClientListeningThread clientListeningThread;

  @BeforeEach
  void setUp() {
    DataInputStream in = new DataInputStream(new ByteArrayInputStream("abc".getBytes(
        StandardCharsets.UTF_8)));
    DataOutputStream out = new DataOutputStream(new ByteArrayOutputStream());
    ClientChatroomProtocol protocol = new ClientChatroomProtocol(in, out);
    this.clientListeningThread = new ClientListeningThread(in, out, protocol);
  }

  @Test
  void testEquals() {
    assertEquals(clientListeningThread, clientListeningThread);
    assertNotEquals(clientListeningThread, null);
    assertNotEquals(clientListeningThread, 1);
    assertNotEquals(clientListeningThread, new ClientListeningThread(null, null, null));
  }

  @Test
  void testHashCode() {
    assertEquals(clientListeningThread.hashCode(), clientListeningThread.hashCode());
  }
}