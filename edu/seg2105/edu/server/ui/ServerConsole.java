package edu.seg2105.edu.server.ui;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.ServerClient;
import ocsf.server.AbstractServer;

import java.io.IOException;
import java.util.Scanner;

public class ServerConsole implements ChatIF {


    ServerClient client;
    Scanner fromConsole;

    public ServerConsole(AbstractServer server)
    {
        try
        {
            client = new ServerClient(this, server);
        }
        catch(IOException exception)
        {
            System.out.println("Error: Can't setup connection!"
                    + " Terminating client.");
            System.exit(1);
        }

        // Create scanner object to read from console
        fromConsole = new Scanner(System.in);
    }
    public void accept()
    {
        try
        {

            String message;

            while (true)
            {
                message = fromConsole.nextLine();
                client.handleMessageFromClientUI(message);
            }
        }
        catch (Exception ex)
        {
            System.out.println
                    ("Unexpected error while reading from console!");
        }
    }

    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    public void display(String message)
    {
        String[] msgComponents = message.split(">");
        if(msgComponents.length > 1) {
            System.out.println("[Server UI] " + msgComponents[0] + "> " + message.substring(msgComponents[0].length()+1));
        } else {
            System.out.println("> " + message);
        }
    }
}
