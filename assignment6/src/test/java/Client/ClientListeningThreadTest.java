package Client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientListeningThreadTest {
  private ClientListeningThread clientListeningThread;

  @BeforeEach
  void setUp() {
    DataInputStream in = mock(DataInputStream.class);
    DataOutputStream out = mock(DataOutputStream.class);
    ClientChatroomProtocol protocol = mock(ClientChatroomProtocol.class);
    clientListeningThread = new ClientListeningThread(in, out, protocol);
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