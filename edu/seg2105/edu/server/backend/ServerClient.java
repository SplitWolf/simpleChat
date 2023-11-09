package edu.seg2105.edu.server.backend;

import edu.seg2105.client.common.ChatIF;
import ocsf.client.AbstractClient;
import ocsf.server.AbstractServer;

import java.io.IOException;

public class ServerClient {
    /**
     * The interface type variable.  It allows the implementation of
     * the display method in the client.
     */
    ChatIF clientUI;
    AbstractServer server;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     * @param clientUI The interface type variable.
     * @param server The server for this client
     */

    public ServerClient(ChatIF clientUI, AbstractServer server)
            throws IOException
    {
        super(); //Call the superclass constructor
        this.clientUI = clientUI;
        this.server = server;
    }


    //Instance methods ************************************************

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
        server.sendToAllClients("SEVER MSG>" + message);
        clientUI.display("SERVER MSG>" + message);
    }

    /**
     * This method terminates the client.
     */
    public void quit()
    {
        try
        {
            server.close();
        }
        catch(IOException e) {}
        System.exit(0);
    }

    public void handleCommand(String cmd, String[] args) {
        switch (cmd) {
            case "quit":
                quit();
                break;
            case "stop":
                server.stopListening();
                clientUI.display("Server stopped listening.");
                break;
            case "close":
                try {
                    server.close();
                } catch (IOException e) {
                    clientUI.display("Server could not close. Exiting.. ");
                    quit();
                }
                break;
            case "start":
                if(!server.isListening()) {
                    try {
                        server.listen();
                    } catch (IOException e) {
                        clientUI.display("Server failed to listen.. Exiting.");
                        quit();
                    }
                    return;
                }
                clientUI.display("Server already Running!");
                break;
            case "setport":
                if(server.isListening()) {
                    clientUI.display("You must first stop the server to use to setPort");
                    return;
                }
                server.setPort(Integer.parseInt(args[0]));
                break;
            case "getport":
                clientUI.display("Port: " + server.getPort());
                break;
            default:
                clientUI.display("Invalid Command!");
                break;
        }
    }
}
