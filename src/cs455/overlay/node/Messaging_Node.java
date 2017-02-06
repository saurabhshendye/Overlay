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
import java.util.concurrent.ConcurrentHashMap;


public class Messaging_Node
{
    private static String[] link_info;
    private static String[] Neighbours;
    private static Socket temperory_socket;
    private static ConcurrentHashMap<String, String[]> Sockets_map = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Thread> TCP_Receiver = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, TCPSender> TCP_Sender = new ConcurrentHashMap<>();

    public static void main(String args[]) throws IOException
    {
        // Accept the inputs from the command Line
        String registry_ip = args[0];
        int registry_port = Integer.parseInt(args[1]);

        // Create the Server Socket to continuously Listen
        ServerSocket Msg_server = new ServerSocket();
        Msg_server.bind(null, 5);


        // Create a temporary Socket and send the registration Request to Registry
        Socket bootstrap = new Socket(registry_ip,registry_port);
        String BS_key = registry_ip + ":" + Integer.toString(registry_port);

        // Putting into the HashMap
//        Sockets.put(BS_key, bootstrap);

        // TCP sender object created and put into concurrent HashMap
        TCPSender register = new TCPSender(bootstrap);
        TCP_Sender.put(BS_key, register);

        // TCP Receiver object created to receive from registry
        Thread register_rx = new TCPReceiver(bootstrap);
        register_rx.start();

        // Components for register request
        int port = Msg_server.getLocalPort();
        int local_port = bootstrap.getLocalPort();
        String IP = bootstrap.getLocalAddress().toString();
        IP = IP.replace("/", "");

        // Print the components of register request
        System.out.println("Server Port Number: "+ port);
        System.out.println("Server Socket IP: " + IP);
        System.out.println("Local port of Registry Socket is : " + local_port);

        // Build Register Request and convert it to byte array
        Register_request request = new Register_request(port, local_port, IP);
        byte[] request_inBytes =  request.getBytearray();


        // Sending the request
        register.send_and_maintain(request_inBytes);

        // Create a User input thread and start
        // Pending : We need to differentiate it from Registry
        User_Input In = new User_Input();
        In.start();

        // Sit here and wait for incoming connections
        while(true)
        {
            Socket serving = Msg_server.accept();
            System.out.println("creating thread in Messaging node. ");
            Thread thread_2 = new TCPReceiver(serving);
            make_TCP_ReceiverEntry(serving, thread_2);
            System.out.println("Socket is connected to: " + serving.getRemoteSocketAddress().toString());
            thread_2.start();
        }
    }

    private static void make_TCP_ReceiverEntry(Socket S, Thread T)
    {
        String[] byParts = S.getRemoteSocketAddress().toString().split(":");
        String IP = byParts[0].replace("/","");
//        String[] IP_Port = {IP ,byParts[1]};
        String IP_Port = IP + ":" + byParts[1];
        System.out.println("Making the entry with IP: " + IP);
        System.out.println("Making the entry with port: " + byParts[1]);
        TCP_Receiver.put(IP_Port, T);
//        socket_map.put(IP_Port,S);
//        test_map.put(IP_Port, "It is a socket problem then");
//        System.out.println("Testing HashMap: " + test_map.get(IP_Port));
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

    public static void messaging_node_list_parser(byte [] byte_data)
    {
        String Node_list = new String(byte_data);
        if (Node_list.equals("None"))
        {
            System.out.println("No need to make a connection");
        }
        else
        {
            Neighbours = Node_list.split(";");
            print_messaging_nodes();

            for (String N : Neighbours)
            {

            }
        }

    }

    private static void print_messaging_nodes()
    {
        System.out.println();
        for (String N : Neighbours)
        {
            System.out.println(N);
        }

    }

}
