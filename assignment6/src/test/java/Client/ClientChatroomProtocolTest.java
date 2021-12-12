package Client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientChatroomProtocolTest {

  private ClientChatroomProtocol protocol;
  private DataInputStream in;
  private DataOutputStream out;

  @BeforeEach
  void setUp() {
    in = new DataInputStream(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
    out = new DataOutputStream(new ByteArrayOutputStream());
    this.protocol = new ClientChatroomProtocol(in, out);
  }

  @Test
  void testEquals() {
    assertEquals(protocol, protocol);
    assertNotEquals(protocol, null);
    assertNotEquals(protocol, 1);
  }

  @Test
  void testHashCode() {
  }
}