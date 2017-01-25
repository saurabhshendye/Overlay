/*
 * Created by Saurabh Shendye on 1/24/17.
 */
package cs455.overlay.UserIn;

import java.util.Scanner;

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
        }
    }
}
