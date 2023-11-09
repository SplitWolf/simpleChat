package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import edu.seg2105.edu.server.ui.ServerConsole;
import ocsf.server.*;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " from " + client.getInfo("id"));
    if (msg instanceof String message) {
      if(message.startsWith("#")) {
        String[] commandMsg = message.split(" ");
        String command = commandMsg[0].substring(1);
        String[] args = new String[commandMsg.length-1];
        System.arraycopy(commandMsg, 1, args, 0, commandMsg.length - 1);
        handleCommand(command,args, client);
        return;
      }
    }
    this.sendToAllClients(client.getInfo("id") + ">" + msg);
  }

  public void handleCommand(String cmd, String[] args, ConnectionToClient client) {
    switch (cmd) {
      case "login":
        if(client.getInfo("id") == null) {
          client.setInfo("id",args[0]);
          String loginMessage = args[0] + " has logged on.";
          System.out.println(loginMessage);
          this.sendToAllClients(loginMessage);
          return;
        }
        try {
          client.sendToClient("Error, Client already logged in. Terminating Connection.");
          client.close();
        } catch (IOException e) {
        }
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  @Override
  public void clientConnected(ConnectionToClient client) {
    System.out.printf("%s has connected. %n", client.getInfo("id"));
  }

  public void clientDisconnected(ConnectionToClient client) {
    System.out.printf("%s has disconnected. %n", client.getInfo("id"));
  }

  @Override
  public void clientException(ConnectionToClient client, Throwable ignored) {
    System.out.printf("%s has disconnected.", client.getInfo("id"));
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args)
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    ServerConsole adminConsole = new ServerConsole(sv);
    adminConsole.accept();
  }
}
//End of EchoServer class
