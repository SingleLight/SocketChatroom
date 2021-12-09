import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientInterface {


  private static final String LOG_OFF = "logoff";
  private static final String WHO = "who";
  private static final String DM_USER = "@user";
  private static final String INSULT = "!user";
  private final String HOST_NAME;
  private final int PORT_NUMBER;
  private final String CLIENT_NAME;
  private DataOutputStream out;
  private DataInputStream in;
  private ClientChatroomProtocol protocol;

  public ClientInterface(String hostName, int portNumber, String clientName) {
    this.HOST_NAME = hostName;
    this.PORT_NUMBER = portNumber;
    this.CLIENT_NAME = clientName;
    connect();
    System.out.println("Socket connection established");
  }

  public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);
    String line;
    ClientInterface ci = new ClientInterface("localhost", 3000, "Bob");
  }

  private void connect() {
    try {
      Socket socket = new Socket(HOST_NAME, PORT_NUMBER);
      out = new DataOutputStream(socket.getOutputStream());
      in = new DataInputStream(socket.getInputStream());
      this.protocol = new ClientChatroomProtocol(in, out, socket);
      this.protocol.connect(CLIENT_NAME);
      Thread clientListener = new Thread(new ClientListeningThread(socket, in, out, protocol));
      clientListener.start();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Connection failed");
    }
  }

  private void logOff() throws IOException {
    protocol.logOff(CLIENT_NAME);
  }

  private void listAllUsers(String username) throws IOException {
    protocol.listAllUsers(username);
  }

  private void directMessage(String from, String to, String message) throws IOException {
    protocol.directMessage(from, to, message);
  }

  private void broadcastMessage(String from, String message) throws IOException {
    protocol.broadcastMessage(from, message);
  }

  private void sendInsult(String from, String to) throws IOException {
    protocol.sendInsult(from, to);
  }

  private void help() {

  }

  private void commandParser(String command) throws IOException {
    String[] commandArr = command.split(" ");
    if (command.startsWith(LOG_OFF)) {
      logOff();
    } else if (command.startsWith(WHO)) {
      listAllUsers(CLIENT_NAME);
    } else if (command.startsWith(DM_USER)) {
      directMessage(commandArr[1], commandArr[2], commandArr[3]);
    } else if (command.startsWith(INSULT)) {
      sendInsult(commandArr[1], commandArr[2]);
    } else if (command.startsWith("?")) {
      help();
    } else {
      broadcastMessage(commandArr[0], commandArr[1]);
    }
  }

}
