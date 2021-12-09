import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatroomServer {

  private static final int MAX_THREAD_NUMBER = 10;

  public static void main(String[] args) throws IOException {
/*    ChatroomServer server = new ChatroomServer();
    server.task(args);
    ByteBuffer bb = ByteBuffer.allocate(1024);
    bb.putInt(1);
    bb.putChar(' ');
    bb.put("abc".getBytes(StandardCharsets.UTF_8));
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bb.array()));
    System.out.println(in.readInt());
    in.readChar();
    byte[] temp = new byte[3];
    in.read(temp);
    System.out.println(new String(temp));*/
    String[] arg = {"3000"};
    ChatroomServer server = new ChatroomServer();
    server.task(arg);
  }

  public void task(String[] args) throws IOException {
    System.out.println(args.length);
    if (args.length != 1) {
      System.out.println("A port should be provided when running the server");
      System.exit(1);
    }
    int portNumber = Integer.parseInt(args[0]);
    boolean listening = true;

    ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);


    try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
      while (listening) {
        System.out.println("The server is listening on port " + portNumber);
        Socket socket = serverSocket.accept();
        Thread newConnection = new Thread(new ChatroomServerThread(new ConcurrentHashMap<>(), socket));
        executor.submit(newConnection);
      }
    }
    executor.shutdown();
  }

}
