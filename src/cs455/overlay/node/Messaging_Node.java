/*
 * Created by Saurabh Shendye on 1/24/17.
 */

package cs455.overlay.node;

import cs455.overlay.UserIn.User_Input;
import cs455.overlay.WireFormats.Register_request;
import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Messaging_Node
{
    private static String[] link_info;


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
        String IP = temp_socket.getLocalAddress().toString();
        IP = IP.replace("/", "");
        System.out.println("Temp Socket address: " + IP);
        TCPSender register = new TCPSender(temp_socket);

        Register_request request = new Register_request(port, IP);

        byte[] request_inBytes =  request.getBytearray();


        // Sending the request
        register.send_data(request_inBytes);

        User_Input In = new User_Input();
        In.start();

        while(true)
        {
            Socket serving = Msg_server.accept();
            System.out.println("creating thread in Messaging node. ");
            Thread thread_2 = new TCPReceiver(serving);
            thread_2.start();
        }
    }

    public static void reg_ack_parser(byte[] byte_data)
    {
        String Message = new String(byte_data);
        System.out.println("Registration Message: " + Message);
    }

    public static void link_info_parser(byte[] byte_data)
    {
        String Full_link_info = new String(byte_data);
        link_info = Full_link_info.split(";");
    }

}
