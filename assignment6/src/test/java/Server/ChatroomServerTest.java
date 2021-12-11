package Server;

import static org.junit.jupiter.api.Assertions.*;


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
  void task() {
  }

  @Test
  void testEquals() {
  }
}