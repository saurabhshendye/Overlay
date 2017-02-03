/*
 * Created by Saurabh Shendye on 1/24/17.
 */
package cs455.overlay.UserIn;

import java.io.IOException;
import java.util.Scanner;

import static cs455.overlay.node.Registry.print_node_info;
import static cs455.overlay.node.Registry.send_link_weights;
import static cs455.overlay.node.Registry.setup_overlay;

public class User_Input extends Thread
{
    public void run()
    {
        while (true)
        {
            Scanner in = new Scanner(System.in);
            System.out.println("Enter a Command: ");
            String command = in.nextLine();
            System.out.println("Input from User: " + command);
            try
            {
                input_parser(command);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void input_parser(String command) throws IOException
    {
        if (command.equals("list-messaging nodes"))
        {
            print_node_info();
        }
        else if (command.equals("list-weights"))
        {
            System.out.println("List the weights.");
        }
        else if (command.equals("send-overlay-link-weights"))
        {
            System.out.println("Send link weights");
            send_link_weights();
        }
        else if (command.startsWith("setup-overlay"))
        {
            System.out.println("Setup Overlay");
            setup_overlay(command);
        }
        else if (command.startsWith("start"))
        {
            System.out.println("Start Exchanging the messages");
        }
        else if (command.equals("print-shortest-path"))
        {
            System.out.println("Print the shortest path");
        }
        else if (command.equals("exit-overlay"))
        {
            System.out.println("Exit the overlay");
        }
        else
        {
            System.out.println("Invalid Command");
        }

    }
}
