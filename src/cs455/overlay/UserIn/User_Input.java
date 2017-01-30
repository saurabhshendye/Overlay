/*
 * Created by Saurabh Shendye on 1/24/17.
 */
package cs455.overlay.UserIn;

import java.util.Scanner;

import static cs455.overlay.node.Registry.print_node_info;

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
            input_parser(command);
        }
    }

    private static void input_parser(String command)
    {
        switch (command)
        {
            case "list-messaging nodes": print_node_info();
                break;
        }
    }
}
