package Server;

import static org.junit.jupiter.api.Assertions.*;


import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatroomServerTest {
  private ChatroomServer chatroomServer;

  @BeforeEach
  void setUp() {
    this.chatroomServer = new ChatroomServer();
  }

  @Test
  void testHashCode() {
    assertEquals(chatroomServer.hashCode(),chatroomServer.hashCode());
  }

  @Test
  void task() throws IOException {
    assertThrows(IllegalArgumentException.class, () -> chatroomServer.task(new String[]{}));
  }

  @Test
  void testEquals() {
    assertEquals(chatroomServer, chatroomServer);
    assertNotEquals(chatroomServer, new ChatroomServer());
    assertNotEquals(chatroomServer, null);
    assertNotEquals(chatroomServer, 1);
  }
}