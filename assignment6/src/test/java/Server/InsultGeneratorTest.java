package Server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsultGeneratorTest {

  private InsultGenerator insultGenerator;

  @BeforeEach
  void setUp() {
    insultGenerator = new InsultGenerator();
  }

  @Test
  void randomInsult() {
    assertTrue(InsultGenerator.randomInsult().length() > 0);
  }

  @Test
  void testHashCode() {
    assertEquals(insultGenerator.hashCode(), insultGenerator.hashCode());
  }

  @Test
  void testEquals() {
    assertEquals(insultGenerator, insultGenerator);
    assertNotEquals(insultGenerator, null);
    assertNotEquals(insultGenerator, 1);
  }

  @Test
  void testToString() {
    assertEquals("InsultGenerator{}", insultGenerator.toString());
  }
}