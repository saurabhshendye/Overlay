/*
 * Created by Saurabh Shendye on 1/24/17.
 */

package cs455.overlay.node;

import cs455.overlay.Dijkstra.Shortest_Path;
import cs455.overlay.UserIn.User_Input;
import cs455.overlay.WireFormats.*;
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
    private static String BS_key;
    private static String[] Neighbours;
    private static ArrayList<String []> link_info = new ArrayList<>();
    private static ArrayList<String[]> Node_info = new ArrayList<>();
    private static ArrayList<String> Nodes = new ArrayList<>();
    private static ConcurrentHashMap<String, String> IP_Port_Map = new ConcurrentHashMap<>();
//    private static ConcurrentHashMap<String, Thread> TCP_Receiver = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, TCPSender> TCP_Sender = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Socket> socket_map = new ConcurrentHashMap<>();
//    private static Counters C = new Counters();
    private static Shortest_Path P;
    private static Counters C;



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
        BS_key = registry_ip + ":" + Integer.toString(registry_port);

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

        // Initiating Counter Variable
        String self_id = my_IP + ":" + my_port;
        C = new Counters(self_id);

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
            make_TCP_ReceiverEntry(serving);
            System.out.println("Socket is connected to: " + serving.getRemoteSocketAddress().toString());
            thread_2.start();
        }
    }

    private static void make_TCP_ReceiverEntry(Socket S)
    {
        String[] byParts = S.getRemoteSocketAddress().toString().split(":");
        String IP = byParts[0].replace("/","");
        String IP_Port = IP + ":" + byParts[1];
//        System.out.println("Making the entry with IP: " + IP);
//        System.out.println("Making the entry with port: " + byParts[1]);
//        TCP_Receiver.put(IP_Port, T);
        socket_map.put(IP_Port,S);
    }

    public static void reg_ack_parser(byte[] byte_data)
    {
        String Message = new String(byte_data);
        System.out.println("Registration Message: " + Message);
    }
    
    public static void exit_overlay_parser() throws IOException
    {
        De_register exit_request = new De_register(my_IP, my_port);
        byte [] exit_bytes = exit_request.getByteArray();

        TCPSender exit = TCP_Sender.get(BS_key);
        exit.send_and_maintain(exit_bytes);

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

//        print_weights();

        String source = my_IP + ":" +Integer.toString(my_port);
        Shortest_Path path = new Shortest_Path(weights, Nodes, source);
        P = path;
        path.calculate_distances();
//        path.print_distances();

//        print_successors(path);
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

    private static void link_info_arraylist(String [] links)
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
                MN_receiver.start();

                // Getting the connection IP port
                String [] parts = MN_socket.getRemoteSocketAddress().toString().split(":");
                String connection_IP_port = byParts[0] + ":" + parts[1];
                System.out.println("Connection IP:Port :  "  + connection_IP_port);

                // Putting the above created objects in Concurrent HashMaps
                IP_Port_Map.put(N, connection_IP_port);
//                TCP_Receiver.put(connection_IP_port, MN_receiver);
                TCP_Sender.put(connection_IP_port,MN_sender);


                int local_port = MN_socket.getLocalPort();
                establish_connection_msg conn_msg = new establish_connection_msg(my_IP,local_port, my_port);
                byte [] conn_msg_bytes = conn_msg.getBytearray();
                MN_sender.send_and_maintain(conn_msg_bytes);

            }
        }
    }

    public synchronized static void connection_request_parser(byte [] byte_data) throws IOException
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

    public static void Task_Initiate_Parser(byte [] byte_data) throws IOException {
        String temp = new String(byte_data);
        int rounds = Integer.parseInt(temp);

        System.out.println("Number of rounds to perform: " +rounds);

//        String self_id = my_IP + ":" + my_port;
//
//        // Select the random node from the Nodes list
//        int rand = new Random().nextInt(Nodes.size());
//        String sink = Nodes.get(rand);
//
//        while (sink.equals(self_id))
//        {
//            rand = new Random().nextInt(Nodes.size());
//            sink = Nodes.get(rand);
//        }

//        System.out.println("Source Node (myself): " +self_id);
//        System.out.println("Selected sink node is: " +sink);

        // Start sending messages
        Start_Transmitting(rounds);

        // Print Task Summary
        System.out.println("Enter command 'print-counters' to view the summary");

        // Send task summary to registry
        send_task_complete();

    }

    private static void Start_Transmitting(int rounds) throws IOException {
//        ArrayList<String> Adj = P.getAdjacent();

//        System.out.println("next hop: " +next_hop);
//        System.out.println("Predecessor: " +predecessor);

//        String right_addr = find_next_hop(sink);
//
//        System.out.println("Right IP Port: " +right_addr);
//        TCPSender Msg_send = TCP_Sender.get(right_addr);

//        String [] next_node = predecessor.split(":");

        Random generate = new Random();

        for (int i = 0; i < rounds; i++)
        {
            String self_id = my_IP + ":" + my_port;

            // Select the random node from the Nodes list
            int rand = new Random().nextInt(Nodes.size());
            String sink = Nodes.get(rand);

            while (sink.equals(self_id))
            {
                rand = new Random().nextInt(Nodes.size());
                sink = Nodes.get(rand);
            }

            String right_addr = find_next_hop(sink);

//            System.out.println("Right IP Port: " +right_addr);
            TCPSender Msg_send = TCP_Sender.get(right_addr);

            for (int j = 0; j < 5; j++)
            {
                long val = generate.nextLong();
                C.increment_tx(val);
                Message_Exchange p_msg = new Message_Exchange(sink,val);
                byte [] byte_msg = p_msg.getByteArray();
                Msg_send.send_and_maintain(byte_msg);
            }
        }
        System.out.println("Done With Sending");

    }

    private static void send_task_complete() throws IOException
    {
        // Creating task complete message
        Task_complete TC = new Task_complete();
        byte [] Bytes = TC.getByteArray();

        TCPSender send_TC = TCP_Sender.get(BS_key);
        send_TC.send_and_maintain(Bytes);
    }

    public static void traffic_summary_request_parser() throws IOException
    {
        byte [] summary_bytes = C.getByteArray();

        TCPSender send_summary = TCP_Sender.get(BS_key);
        send_summary.send_and_maintain(summary_bytes);

        C.set_counters_toZero();
    }

    private synchronized static String find_next_hop(String sink)
    {
        String predecessor = P.get_successor(sink);
        String self_id = my_IP + ":" + my_port;

        String right_addr;

        if (self_id.equals(predecessor))
        {
//            System.out.println("They are equal");
            right_addr = IP_Port_Map.get(sink);
        }
        else
        {
//            System.out.println("They are not equal");
            String next_hop = predecessor;
            while (!self_id.equals(predecessor))
            {
//            System.out.println("Predecessor: " +predecessor);
                next_hop = predecessor;
                predecessor = P.get_successor(predecessor);
            }
            right_addr = IP_Port_Map.get(next_hop);
        }

        return right_addr;
    }

    public static void print_shortest_path()
    {
        String self_id = my_IP + ":" + Integer.toString(my_port);

        System.out.println("Self ID: " +self_id);
        for (String node: Nodes)
        {
            String next_hop;
            System.out.println("-------------------------------------------------");
            if (!node.equals(self_id))
            {

                ArrayList<String> paths = new ArrayList<>();
                String Dest = node;
                System.out.println("Destination: "+Dest);
                while (!node.equals(self_id))
                {
//                    next_hop = node;

//                    int next_hop_ind = Nodes.indexOf(next_hop);
                    node = P.get_successor(node);
                    paths.add(node);
//                    System.out.println("Next hop: " +node);
//                    int node_index = node.indexOf(node);
//                    System.out.println("Link Weight: " +weights[next_hop_ind][node_index]);

                }

//                for (String hop: paths)
//                {
//                    System.out.println(hop);
//                }
                System.out.println("Has the destination variable changed ? " +Dest);
                for (int i = 0; i < paths.size(); i++)
                {
                    if (i == 0)
                    {
                        int Dest_ind = Nodes.indexOf(Dest);
                        String current = paths.get(i);
                        int current_ind = Nodes.indexOf(current);
                        System.out.println("Link Weight: " +weights[Dest_ind][current_ind]);
                        System.out.println(current);
                    }
                    else
                    {
                        String current = paths.get(i);
                        int current_ind = Nodes.indexOf(current);
                        String predecessor = paths.get(i-1);
                        int prev_ind = Nodes.indexOf(predecessor);
                        System.out.println("Link Weight: " +weights[prev_ind][current_ind]);
                        System.out.println(current);
                    }
                }
//                P.print_path(node);

            }
        }
    }

//    public synchronized static void peer_message_parser(byte [] byte_data) throws IOException
    public static void peer_message_parser(byte [] byte_data) throws IOException
    {
        String self_id = my_IP + ":" + my_port;

        ByteArrayInputStream bin = new ByteArrayInputStream(byte_data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

        long num = din.readLong();

        byte[] dest_byte = new byte[byte_data.length - 8];
        din.readFully(dest_byte);
        String dest  = new String(dest_byte);
//        System.out.println("Message is for: " +dest);
        if (dest.equals(self_id))
        {
//            System.out.println("This is for me");
            C.increment_rx(num);
        }
        else
        {
            // Increment the counter
//            System.out.println("This is not for me");
            C.increment_relayed();

            // Re-Create the message to be forwarded
            Message_Exchange p_msg = new Message_Exchange(dest,num);
            byte [] byte_msg = p_msg.getByteArray();

            // Get the right socket
            String right_addr = find_next_hop(dest);
//            System.out.println("Forwarding the message to: "+right_addr);
            TCPSender Msg_send = TCP_Sender.get(right_addr);
            Msg_send.send_and_maintain(byte_msg);
        }
    }

    public static void exit_all()
    {
        System.out.println("Terminating...");
        System.exit(0);
    }

    private static void print_messaging_nodes()
    {
        System.out.println();
        for (String N : Neighbours)
        {
            System.out.println(N);
        }
    }




    public static void print_counters()
    {
        System.out.println("Number of messages Received: "+ C.getReceive_tracker());
        System.out.println("Number of messages sent: "+ C.getSent_tracker());
        System.out.println("Number of messages relayed: " +C.getRelayed_tracker());
        System.out.println("Sent Summation: " +C.getSent_summation());
        System.out.println("Received Summation: " +C.getReceive_summation());
    }
}
