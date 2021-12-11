package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * thread that listens for incoming message
 */
public class ClientListeningThread implements Runnable {


  private final DataInputStream in;
  private final DataOutputStream out;
  private final ClientChatroomProtocol protocol;

  /**
   * constructor
   *
   * @param in       input stream
   * @param out      output stream
   * @param protocol client protocol
   */
  public ClientListeningThread(DataInputStream in, DataOutputStream out,
      ClientChatroomProtocol protocol) {
    this.in = in;
    this.protocol = protocol;
    this.out = out;
  }

  /**
   * calls the client process method in protocol and keeps listening for stream
   */
  @Override
  public void run() {
    try {
      protocol.clientProcess();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientListeningThread that = (ClientListeningThread) o;
    return in.equals(that.in) && out.equals(that.out)
        && protocol.equals(that.protocol);
  }

  @Override
  public int hashCode() {
    return Objects.hash(in, out, protocol);
  }
}
