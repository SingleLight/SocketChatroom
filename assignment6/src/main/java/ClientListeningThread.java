import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientListeningThread implements Runnable {

  private final Socket socket;
  private final DataInputStream in;
  private final DataOutputStream out;
  private final ClientChatroomProtocol protocol;

  public ClientListeningThread(Socket socket, DataInputStream in, DataOutputStream out,
      ClientChatroomProtocol protocol) {
    this.socket = socket;
    this.in = in;
    this.protocol = protocol;
    this.out = out;
  }

  @Override
  public void run() {
    try {
      protocol.clientProcess();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
