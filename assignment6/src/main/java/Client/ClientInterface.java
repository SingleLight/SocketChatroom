package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

/**
 * interface of client
 */
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
  private static final int HOST_NAME_INDEX = 0;
  private static final int PORT_NUMBER_INDEX = 1;
  private static final int CLIENT_NAME_INDEX = 2;
  private final String HOST_NAME;
  private final int PORT_NUMBER;
  private final String CLIENT_NAME;
  private DataOutputStream out;
  private DataInputStream in;
  private ClientChatroomProtocol protocol;
  private Boolean running = true;


  /**
   * constructor
   *
   * @param hostName   host name
   * @param portNumber port number
   * @param clientName client name
   */
  public ClientInterface(String hostName, int portNumber, String clientName) {
    this.HOST_NAME = hostName;
    this.PORT_NUMBER = portNumber;
    this.CLIENT_NAME = clientName;
    connect();
    System.out.println(ESTABLISHED_CONNECTION);
  }

  /**
   * main method
   *
   * @param args host name, port number, client name
   * @throws IOException error in stream read write
   */
  public static void main(String[] args) throws IOException {
    ClientInterface ci = new ClientInterface(args[HOST_NAME_INDEX],
        Integer.parseInt(args[PORT_NUMBER_INDEX]), args[CLIENT_NAME_INDEX]);
    ci.task();
  }

  /**
   * read input from user and handles it
   *
   * @throws IOException error in stream read write
   */
  public void task() throws IOException {
    Scanner scanner = new Scanner(System.in);
    String line;
    while (running) {
      line = scanner.nextLine();
      commandParser(line);
    }
  }

  /**
   * connect to the server
   */
  private void connect() {
    try {
      Socket socket = new Socket(HOST_NAME, PORT_NUMBER);
      out = new DataOutputStream(socket.getOutputStream());
      in = new DataInputStream(socket.getInputStream());
      this.protocol = new ClientChatroomProtocol(in, out);
      this.protocol.connect(CLIENT_NAME);
      Thread clientListener = new Thread(new ClientListeningThread(in, out, protocol));
      clientListener.start();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(CONNECTION_FAILED);
    }
  }

  /**
   * logoff from the server
   *
   * @throws IOException error in stream read write
   */
  private void logOff() throws IOException {
    protocol.logOff(CLIENT_NAME);
    running = false;
  }

  /**
   * query all users connected to server
   *
   * @param username username of the client
   * @throws IOException error in stream read write
   */
  private void listAllUsers(String username) throws IOException {
    protocol.listAllUsers(username);
  }

  /**
   * send direct message to a user
   *
   * @param from    from username
   * @param to      to username
   * @param message message content
   * @throws IOException error in stream read write
   */
  private void directMessage(String from, String to, String message) throws IOException {
    protocol.directMessage(from, to, message);
  }

  /**
   * send broadcast message to everyone
   *
   * @param from    from username
   * @param message message content
   * @throws IOException error in stream read write
   */
  private void broadcastMessage(String from, String message) throws IOException {
    protocol.broadcastMessage(from, message);
  }

  /**
   * send an insult to a user
   *
   * @param from from username
   * @param to   to username
   * @throws IOException error in stream read write
   */
  private void sendInsult(String from, String to) throws IOException {
    protocol.sendInsult(from, to);
  }

  /**
   * get help on the command
   */
  private void help() {
    System.out.println(LOG_OFF_PROMPT);
    System.out.println(QUERY_USER_PROMPT);
    System.out.println(DM_PROMPT);
    System.out.println(INSULT_PROMPT);
    System.out.println(ALL_PROMPT);
  }

  /**
   * parse and make sense of the command
   *
   * @param command command input
   * @throws IOException error in stream read write
   */
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientInterface that = (ClientInterface) o;
    return PORT_NUMBER == that.PORT_NUMBER && HOST_NAME.equals(that.HOST_NAME)
        && CLIENT_NAME.equals(
        that.CLIENT_NAME) && out.equals(that.out) && in.equals(that.in) && protocol.equals(
        that.protocol) && running.equals(that.running);
  }

  @Override
  public int hashCode() {
    return Objects.hash(HOST_NAME, PORT_NUMBER, CLIENT_NAME, out, in, protocol, running);
  }
}
