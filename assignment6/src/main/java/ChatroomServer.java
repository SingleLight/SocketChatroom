import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatroomServer {

  private static final int MAX_THREAD_NUMBER = 10;
  private static final String NO_PORT = "A port should be provided when running the server";
  private static final String ON_PORT = "The server is listening on port ";

  public static void main(String[] args) throws IOException {
    ChatroomServer server = new ChatroomServer();
    server.task(args);
  }

  public void task(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println(NO_PORT);
      System.exit(1);
    }
    ConcurrentHashMap<String, DataOutputStream> sharedBuffers = new ConcurrentHashMap<>();
    int portNumber = Integer.parseInt(args[0]);
    boolean listening = true;
    ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);
    try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
      while (listening) {
        System.out.println(ON_PORT + portNumber);
        Socket socket = serverSocket.accept();
        Thread newConnection = new Thread(new ChatroomServerThread(sharedBuffers, socket));
        executor.submit(newConnection);
      }
    }
    executor.shutdown();
  }

}
