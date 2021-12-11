package Server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import Client.ClientChatroomProtocol;
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
  private DataInputStream in = mock(DataInputStream.class);
  private DataOutputStream out = mock(DataOutputStream.class);

  @BeforeEach
  void setUp() throws IOException {
  }

  @Test
  void serverProcessConnectMessage() throws IOException {
    when(in.readInt()).thenReturn(19).thenReturn(3);
    when(in.readChar()).thenReturn(' ');
    when(in.read(any(byte[].class))).thenReturn(3);

    this.protocol = new ServerChatroomProtocol(in, out, sharedBuffers);

  }

  @Test
  void testEquals() {
  }

  @Test
  void testHashCode() {
  }
}