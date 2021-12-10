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
  private boolean running = true;

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
    ClientInterface ci = new ClientInterface("localhost", 3000, "Bobb");
    while (ci.running) {
      line = scanner.nextLine();
      ci.commandParser(line);
    }
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
    running = false;
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
    System.out.println("Use logoff to logoff");
    System.out.println("Use who to see the connected users");
    System.out.println("Use @user [username] [message] to send a direct message to a user");
    System.out.println("Use !user [username] to send an insult to a user");
  }

  private void commandParser(String command) throws IOException {
    if (command.startsWith(LOG_OFF)) {
      logOff();
    } else if (command.startsWith(WHO)) {
      listAllUsers(CLIENT_NAME);
    } else if (command.startsWith(DM_USER)) {
      String[] commandArr = command.split(" ", 3);
      directMessage(CLIENT_NAME, commandArr[1], commandArr[2]);
    } else if (command.startsWith(INSULT)) {
      String[] commandArr = command.split(" ", 2);
      sendInsult(CLIENT_NAME, commandArr[1]);
    } else if (command.startsWith("?")) {
      help();
    } else {
      broadcastMessage(CLIENT_NAME, command);
    }
  }

}
