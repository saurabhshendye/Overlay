/*
 * Created by Saurabh Shendye on 1/23/17.
 */

package cs455.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;


public class Node
{
    Node(int port) throws IOException
    {
        ServerSocket server = new ServerSocket(port, 5);
    }

    Node() throws IOException
    {
        // This will create a socket and will not bind to any of the port
        ServerSocket server = new ServerSocket();
        // This will bind the socket to any port with capability of handling 5 connections at a time
        server.bind(null, 5);
    }
    
}

