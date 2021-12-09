import java.util.Random;

public class InsultGenerator {

  private static final Random rand = new Random();
  private static final String[] insultList = {
      "You know, you are a classic example of the inverse ratio between the size of the mouth and the size of the brain.",
  "If you spend word for word with me, I shall make your wit bankrupt.", "You clinking, clanking, clattering collection of cartilaginous junk!",
  "I'll explain and I'll use small words so that you'll be sure to understand, you warthog-faced buffoon.",
  "Don't look now, but there's one man too many in this room and I think it's you."};

  public String randomInsult() {
    return insultList[rand.nextInt(5)];
  }

}
