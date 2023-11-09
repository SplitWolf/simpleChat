// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    if(message.startsWith("#")) {
      String[] commandMsg = message.split(" ");
      String command = commandMsg[0].substring(1);
      String[] args = new String[commandMsg.length-1];
      System.arraycopy(commandMsg, 1, args, 0, commandMsg.length - 1);
      handleCommand(command,args);
      return;
    }
    if(!isConnected()) {
      clientUI.display("Please login before trying to send a message!");
      return;
    }
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  @Override
  public void connectionClosed() {
    clientUI.display("Logged Off");
  }
  @Override
  public void connectionException(Exception e) {
    clientUI.display("The server has shutdown. Exiting...");
    System.exit(0);
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  public void handleCommand(String cmd, String[] args) {
    switch (cmd) {
      case "quit":
        quit();
        break;
      case "logoff":
        try
        {
          closeConnection();
        }
        catch(IOException e) {}
        break;
      case "sethost":
        if(isConnected()) {
          clientUI.display("You must first logoff to set the host.");
          return;
        }
        setHost(args[0]);
        break;
      case "setport":
        if(isConnected()) {
          clientUI.display("You must first logoff to set the port.");
          return;
        }
        setPort(Integer.parseInt(args[0]));
        break;
      case "login":
        if(!isConnected()) {
          try {
            openConnection();
          } catch (IOException e) {
            clientUI.display("Failed to open connection. Exiting...");
            quit();
          }
        }
        break;
      case "gethost":
        clientUI.display("Host: " + getHost());
        break;
      case "getport":
        clientUI.display("Port: " + getPort());
        break;
      default:
        clientUI.display("Invalid Command!");
        break;
    }
  }

}
//End of ChatClient class
