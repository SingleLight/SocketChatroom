# Assignment 6 - ChatroomServer

###### Mingyuan Ruihe

## Description

This is a chat room program that implements both the server and client side. The server and client
communicate with a socket and defined protocols.

## How to run the program

Run ChatroomServer in the Server package to run the server.  
Run ClientInterface in the Client package to run the client.  
The server needs a commandline argument of the form `portNumber`. For example: `3000`  
The client needs a commandline argument of the form `hostName portNumber userName`. For example:
`localhost 3000 Bob`.  
The port number needs to match for the communication to start.  
Clients with duplicate names would be rejected.  
The server does not need user input after initialization.  
The client would interact with the user in console.

## Class organization

The program is split in two packages Server and Client, each handling the respective side of
functionality.

On the server side, the chatroom server would establish a server socket that listens for incoming
connections. When it hears one, it would create a separate connection socket and create a thread to
handle this connection. A maximum of 10 threads can be running at the same time. The thread keeps
listening for stream from the client and process these requests using functions in
ServerChatroomProtocol.

On the client side, the chatroom client would establish a socket connection with the server and
communicate using such socket. The rules for the communication are also located in the client
chatroom protocol.

## How to use the client

After initializing the client, it would tell you if the connection is successful, and the number of
other connected clients.  
To get help for what command you can use, type `?` in the console.    
To see who else are connected, type `who` in the console.   
To send a direct message to a user, type `@user [username] [message]` in the console.  
To send an insult to a user, type `!user [username]` in the console  
To send a broadcast message to all users, type `[message]` in the console

## Example interaction output between client and server:

1. Initialize the server with port number `3000`:

```
The server is listening on port 3000
```

2. Initialize the client with `localhost 3000 Bob`:

```
Socket connection established
Connection successful Bob, there are 0 other connected clients.
```

3. Initialize the another client with `localhost 3000 Ann`:

```
Socket connection established
Connection successful Ann, there are 1 other connected clients.
```

4. Send a DM from Ann to Bob `@user Bob Hello!`:

```
Message from Ann to Bob : Hello!
```

5. Query the users from Bob `who`:

```
Users: Ann
```

6. Get help from Ann `?`:

```
Use logoff to logoff
Use who to see the connected users
Use @user [username] [message] to send a direct message to a user
Use !user [username] to send an insult to a user
Type anything else in the console to send to all users
```

7. Logoff from Ann `logoff`:
```
Success! You are no longer connected
```

---

Console of server:

```
The server is listening on port 3000
The server is listening on port 3000
User Bob is connected
The server is listening on port 3000
User Ann is connected
Socket closed
```

Console of Bob:

```
Socket connection established
Connection successful Bob, there are 0 other connected clients.
Message from Ann to Bob : Hello!
who
Users: Ann 
```

Console of Ann:

```
Socket connection established
Connection successful Ann, there are 1 other connected clients.
@user Bob Hello!
?
Use logoff to logoff
Use who to see the connected users
Use @user [username] [message] to send a direct message to a user
Use !user [username] to send an insult to a user
Type anything else in the console to send to all users
logoff
Success! You are no longer connected
```