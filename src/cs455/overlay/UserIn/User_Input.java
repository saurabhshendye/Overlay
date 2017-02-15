/*
 * Created by Saurabh Shendye on 1/24/17.
 */
package cs455.overlay.UserIn;

import java.io.IOException;
import java.util.Scanner;

import static cs455.overlay.node.Messaging_Node.exit_overlay_parser;
import static cs455.overlay.node.Messaging_Node.print_counters;
import static cs455.overlay.node.Messaging_Node.print_shortest_path;
import static cs455.overlay.node.Registry.*;

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
            print_links();
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
            start_parser(command);
        }
        else if (command.equals("print-shortest-path"))
        {
            System.out.println("Print the shortest path");
            print_shortest_path();
        }
        else if (command.equals("exit-overlay"))
        {
            System.out.println("Exit the overlay");
            exit_overlay_parser();
        }
        else if (command.equals("print-counters"))
        {
            System.out.println("Printing the counter values");
            print_counters();
        }
        else
        {
            System.out.println("Invalid Command");
        }

    }
}
