/*
 * Created by Saurabh Shendye on 1/24/17.
 */

package cs455.overlay.node;

import cs455.overlay.Dijkstra.Shortest_Path;
import cs455.overlay.UserIn.User_Input;
import cs455.overlay.WireFormats.Register_request;
import cs455.overlay.WireFormats.establish_connection_msg;
import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.Counters;
import cs455.overlay.util.links_to_array;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class Messaging_Node
{
    private static int Node_Count = 0;
    private static int my_port;
    private static int [][] weights;
    private static String my_IP;
    private static String[] Neighbours;
    private static ArrayList<String []> link_info = new ArrayList<>();
    private static ArrayList<String[]> Node_info = new ArrayList<>();
    private static ArrayList<String> Nodes = new ArrayList<>();
    private static ConcurrentHashMap<String, String> IP_Port_Map = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Thread> TCP_Receiver = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, TCPSender> TCP_Sender = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Socket> socket_map = new ConcurrentHashMap<>();
    private static Counters C = new Counters();
    private static Shortest_Path P;



    public static void main(String args[]) throws IOException
    {
        // Accept the inputs from the command Line
        String registry_ip = args[0];
        int registry_port = Integer.parseInt(args[1]);

        // Create the Server Socket to continuously Listen
        ServerSocket Msg_server = new ServerSocket();
        Msg_server.bind(null, 10);


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
        my_port = Msg_server.getLocalPort();
        int local_port = bootstrap.getLocalPort();
        String IP = bootstrap.getLocalAddress().toString();
        my_IP = IP.replace("/", "");

        // Print the components of register request
        System.out.println("Server Port Number: "+ my_port);
        System.out.println("Server Socket IP: " + my_IP);
        System.out.println("Local port of Registry Socket is : " + local_port);

        // Build Register Request and convert it to byte array
        Register_request request = new Register_request(my_port, local_port, my_IP);
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
        String IP_Port = IP + ":" + byParts[1];
        System.out.println("Making the entry with IP: " + IP);
        System.out.println("Making the entry with port: " + byParts[1]);
        TCP_Receiver.put(IP_Port, T);
        socket_map.put(IP_Port,S);
    }

    public static void reg_ack_parser(byte[] byte_data)
    {
        String Message = new String(byte_data);
        System.out.println("Registration Message: " + Message);
    }

    public static void link_info_parser(byte[] byte_data)
    {
        // Storing the link information in the link_info ArrayList
        String Full_link_info = new String(byte_data);
        String [] temp_link = Full_link_info.split(";");
        link_info_print(temp_link);
        link_info_arraylist(temp_link);
        create_node_list();
        print_node_list();

        Node_Count = Nodes.size();
        System.out.println("Node Count is: " + Node_Count);
        links_to_array L_A = new links_to_array(Node_Count, link_info, Nodes);
        weights = L_A.getAdjecencyMatrix();

        print_weights();

        String source = my_IP + ":" +Integer.toString(my_port);
        Shortest_Path path = new Shortest_Path(weights, Nodes, source);
        P = path;
        path.calculate_distances();
        path.print_distances();

        print_successors(path);
    }

    private static void print_successors(Shortest_Path path)
    {
        for (String N : Nodes)
        {
            System.out.println("Successor for Node: " + N + " is: " + path.get_successor(N));
        }
    }

    private static void link_info_print(String[] link_info)
    {
        for (String link : link_info)
        {
            System.out.println("Link details : " + link);
        }
    }

    private static void link_info_arraylist(String [] links )
    {
        for (String link : links)
        {
            link_info.add(link.split(" "));
        }
    }

    private static void create_node_list()
    {
        for (String [] temp : link_info )
        {
            if (!Nodes.contains(temp[0]))
            {
                Nodes.add(temp[0]);
//                Node_Count++;
            }
            if (!Nodes.contains(temp[1]))
            {
                Nodes.add(temp[1]);
//                Node_Count++;
            }
        }
    }

    private static void print_weights()
    {
        for (int [] i : weights)
        {
            for (int b : i)
            {
                System.out.print(b + "\t" );
            }
            System.out.println();
        }
    }

    private static void print_node_list()
    {
        for (String S : Nodes)
        {
            System.out.println("Node: " + S);
        }
    }

    public static void messaging_node_list_parser(byte [] byte_data) throws IOException
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
                String [] byParts = N.split(":");

                // Creating a socket , TCPSender and TCPReceiver objects
                Socket MN_socket = new Socket(byParts[0], Integer.parseInt(byParts[1]));
                TCPSender MN_sender = new TCPSender(MN_socket);
                Thread MN_receiver = new TCPReceiver(MN_socket);

                // Getting the connection IP port
                String [] parts = MN_socket.getRemoteSocketAddress().toString().split(":");
                String connection_IP_port = byParts[0] + ":" + parts[1];
                System.out.println("Connection IP:Port :  "  + connection_IP_port);

                // Putting the above created objects in Concurrent HashMaps
                IP_Port_Map.put(N, connection_IP_port);
                TCP_Receiver.put(connection_IP_port, MN_receiver);
                TCP_Sender.put(connection_IP_port,MN_sender);


                int local_port = MN_socket.getLocalPort();
                establish_connection_msg conn_msg = new establish_connection_msg(my_IP,local_port, my_port);
                byte [] conn_msg_bytes = conn_msg.getBytearray();
                MN_sender.send_and_maintain(conn_msg_bytes);

            }
        }
    }

    public static void connection_request_parser(byte [] byte_data) throws IOException
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(byte_data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bin));
        String[] info;

        // Read the ports
        int port = din.readInt();
        int local_port = din.readInt();

        System.out.println("Listening Port Number is: " + port);
        System.out.println("Connection port is: " + local_port);

        // Read the IP address
        byte[] IP_byte = new byte[byte_data.length - 8];
        din.readFully(IP_byte);
        String IP  = new String(IP_byte);

        // Print the IP Address
        System.out.println("IP Address is : " + IP);

        // Adding the node in the Node Information variable
        info = new String[]{IP, Integer.toString(port)};
        System.out.println("Node added to the list");
        Node_info.add(info);
        Node_Count++;

        // Creating the IP:port mapping
        String connection_IP_Port = IP  + ":" + Integer.toString(local_port);
        String server_IP_port = IP + ":" + Integer.toString(port);
        IP_Port_Map.put(server_IP_port, connection_IP_Port);

        // Retrieve the saved socket and create TCPSender object
        Socket temp = socket_map.get(connection_IP_Port);

        TCPSender node_connect = new TCPSender(temp);

        // Put the TCP Sender object into HashMap
        TCP_Sender.put(connection_IP_Port,node_connect);
    }

    public static void Task_Initiate_Parser(byte [] byte_data)
    {
        String temp = new String(byte_data);
        int rounds = Integer.parseInt(temp);

        System.out.println("Number of rounds to perform: " +rounds);

        String self_id = my_IP + ":" + my_port;

        // Select the random node from the Nodes list
        int rand = new Random().nextInt(Nodes.size());
        String sink = Nodes.get(rand);

        while (sink.equals(self_id))
        {
            rand = new Random().nextInt(Nodes.size());
            sink = Nodes.get(rand);
        }

        System.out.println("Source Node (myself): " +self_id);
        System.out.println("Selected sink node is: " +sink);

        // Start sending messages
        Start_Transmitting(rounds, sink);

    }

    private static void Start_Transmitting(int rounds, String sink)
    {
        ArrayList<String> Adj = P.getAdjacent();
        String predecessor = P.get_successor(sink);
        String self_id = my_IP + ":" + my_port;

        if (self_id.equals(P.get_successor(predecessor)))
        {
            System.out.println("They are equal");
        }

        if (!self_id.equals(P.get_successor(predecessor)))
        {
            System.out.println("They are not equal");
        }

        while (!self_id.equals(P.get_successor(predecessor)))
        {

            System.out.println("Predecessor: " +predecessor);
            predecessor = P.get_successor(predecessor);
        }
        //        while (!Adj.contains(P.get_successor(sink)))

//        System.out.println(P.get_successor(sink));


    }

    public static void peer_message_parser(byte [] byte_data)
    {

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
