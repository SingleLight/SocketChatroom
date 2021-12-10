package Client;

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
  private static final String ESTABLISHED_CONNECTION = "Socket connection established";
  private static final String CONNECTION_FAILED = "Connection failed";
  private static final String LOG_OFF_PROMPT = "Use logoff to logoff";
  private static final String QUERY_USER_PROMPT = "Use who to see the connected users";
  private static final String DM_PROMPT = "Use @user [username] [message] to send a direct message to a user";
  private static final String ALL_PROMPT = "Type anything else in the console to send to all users";
  private static final String INSULT_PROMPT = "Use !user [username] to send an insult to a user";
  private static final String WHITESPACE = " ";
  private static final String HELP = "?";
  private static final int DM_SPLIT = 3;
  private static final int INSULT_SPLIT = 2;
  private final String HOST_NAME;
  private final int PORT_NUMBER;
  private final String CLIENT_NAME;
  private DataOutputStream out;
  private DataInputStream in;
  private ClientChatroomProtocol protocol;
  private Boolean running = true;

  public ClientInterface(String hostName, int portNumber, String clientName) {
    this.HOST_NAME = hostName;
    this.PORT_NUMBER = portNumber;
    this.CLIENT_NAME = clientName;
    connect();
    System.out.println(ESTABLISHED_CONNECTION);
  }

  public static void main(String[] args) throws IOException {
    ClientInterface ci = new ClientInterface(args[0], Integer.parseInt(args[1]), args[2]);
    ci.task();
  }

  public void task() throws IOException {
    Scanner scanner = new Scanner(System.in);
    String line;
    while (running) {
      line = scanner.nextLine();
      commandParser(line);
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
      System.out.println(CONNECTION_FAILED);
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
    System.out.println(LOG_OFF_PROMPT);
    System.out.println(QUERY_USER_PROMPT);
    System.out.println(DM_PROMPT);
    System.out.println(INSULT_PROMPT);
    System.out.println(ALL_PROMPT);
  }

  private void commandParser(String command) throws IOException {
    if (command.startsWith(LOG_OFF)) {
      logOff();
    } else if (command.startsWith(WHO)) {
      listAllUsers(CLIENT_NAME);
    } else if (command.startsWith(DM_USER)) {
      String[] commandArr = command.split(WHITESPACE, DM_SPLIT);
      directMessage(CLIENT_NAME, commandArr[1], commandArr[2]);
    } else if (command.startsWith(INSULT)) {
      String[] commandArr = command.split(WHITESPACE, INSULT_SPLIT);
      sendInsult(CLIENT_NAME, commandArr[1]);
    } else if (command.startsWith(HELP)) {
      help();
    } else {
      broadcastMessage(CLIENT_NAME, command);
    }
  }
}
