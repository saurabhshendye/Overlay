/*
 * Created by Saurabh Shendye on 1/24/17.
 */

package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Messaging_Node extends Node
{

    public Messaging_Node() throws IOException
    {
        super();
    }

    public static void main(String args[]) throws IOException
    {
        // Accept the inputs from the command Line
        String registry_ip = args[0];
        int registry_port = Integer.parseInt(args[1]);

        // Create the Server Socket to continuously Listen
        ServerSocket Msg_server = new ServerSocket();
        Msg_server.bind(null, 5);
        int port = Msg_server.getLocalPort();
        System.out.println("Port Number: "+ port);
        // Create a temporary Socket and send the registration Request to Registry
        Socket temp_socket = new Socket(registry_ip,registry_port);
        System.out.println("Temp Socket address: " + temp_socket.getLocalAddress());
        TCPSender register = new TCPSender(temp_socket);
        String request = "Test";
        byte[] request_inBytes = request.getBytes();
        register.send_data(request_inBytes);
    }
}
