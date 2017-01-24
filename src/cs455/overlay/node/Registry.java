/*
 * Created by Saurabh Shendye on 1/24/17.
 */

package cs455.overlay.node;

import cs455.overlay.transport.TCPReceiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Registry extends Node
{
    Registry(int port) throws IOException {
        super(port);
    }


    public static void main(String args[]) throws IOException
    {
        ServerSocket Reg_server = new ServerSocket(Integer.parseInt(args[0]), 5);
        System.out.println("Server Socket for Registry is Created");

        while (true)
        {
            Socket serving = Reg_server.accept();
            System.out.println("creating thread");
            Thread thread_2 = new TCPReceiver(serving);
            thread_2.start();
        }
    }

}
