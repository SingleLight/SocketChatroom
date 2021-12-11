package Server;

import java.util.Random;

/**
 * generate random insults
 */
public class InsultGenerator {

  private static final Random rand = new Random();
  private static final String[] insultList = {
      "You know, you are a classic example of the inverse ratio between the size of the mouth and the size of the brain.",
      "If you spend word for word with me, I shall make your wit bankrupt.",
      "You clinking, clanking, clattering collection of cartilaginous junk!",
      "I'll explain and I'll use small words so that you'll be sure to understand, you warthog-faced buffoon.",
      "Don't look now, but there's one man too many in this room and I think it's you."};

  /**
   * returns one of five random insults
   *
   * @return an insult string
   */
  public static String randomInsult() {
    return insultList[rand.nextInt(5)];
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Override
  public String toString() {
    return "InsultGenerator{}";
  }
}
