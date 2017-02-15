/*
 * Created by Saurabh Shendye on 1/24/17.
 */

package cs455.overlay.node;

import cs455.overlay.UserIn.User_Input;
import cs455.overlay.WireFormats.*;
import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.Overlay_Creator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static cs455.overlay.util.StatsCollectorAndDisplay.print_traffic_summary;

public class Registry
{
    private static int Node_Count;
    private static int [][] weights;
    private static int Task_complete_tracker;
    private static int summary_tracker = 0;
    private static ArrayList<String[]> Node_info = new ArrayList<>();
    private static ArrayList<String> Link_info = new ArrayList<>();
    private static ArrayList<String> MN = new ArrayList<>();
    private static ConcurrentHashMap<String, String> IP_Port_Map = new ConcurrentHashMap<>();
//    private static ConcurrentHashMap<String, Thread> TCP_Receiver = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, TCPSender> TCP_Sender = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Socket> socket_map = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> test_map = new ConcurrentHashMap<>();


    public static void main(String args[]) throws IOException
    {
        // Create a Server Socket and bind it to a given port
        ServerSocket Reg_server = new ServerSocket(Integer.parseInt(args[0]), 10);
        System.out.println("Server Socket for Registry is Created");

        // We need to enable the functionality to continuously accept the user input commands
        User_Input In = new User_Input();
        In.start();

        // The while loop is created in order to continuously accept the inputs from the network
        while (true)
        {
            // The execution of a program is blocked here
            // It will not go further till there is a input from the network
            Socket serving = Reg_server.accept();
            System.out.println("creating thread");
            Thread thread_2 = new TCPReceiver(serving);
            System.out.println("Socket is connected to: " + serving.getRemoteSocketAddress().toString());
            make_TCP_ReceiverEntry(serving, thread_2);
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
//        TCP_Receiver.put(IP_Port, T);
        socket_map.put(IP_Port,S);
        test_map.put(IP_Port, "It is a socket problem then");
        System.out.println("Testing HashMap: " + test_map.get(IP_Port));
    }

    public static synchronized void getRegistered(byte[] byte_data) throws IOException
    {

        ByteArrayInputStream bin = new ByteArrayInputStream(byte_data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bin));
        String[] info;

        // Read the ports
        int port = din.readInt();
        int local_port = din.readInt();

        // Print the ports
        System.out.println("Listening Port Number is: " + port);
        System.out.println("Connection port is: " + local_port);

        // Read the IP address
        byte[] IP_byte = new byte[byte_data.length - 8];
        din.readFully(IP_byte);
        String IP  = new String(IP_byte);

        // Print the IP Address
        System.out.println("IP Address is : " + IP);

        info = new String[]{IP, Integer.toString(port)};


        String connection_IP_Port = IP  + ":" + Integer.toString(local_port);
        String server_IP_port = IP + ":" + Integer.toString(port);

        IP_Port_Map.put(server_IP_port, connection_IP_Port);


        // Retrieve the saved socket and create TCPSender object

        Socket temp = socket_map.get(connection_IP_Port);
        System.out.println(test_map.get(connection_IP_Port));
        TCPSender node_connect = new TCPSender(temp);

        // Put the TCP Sender object into HashMap
        TCP_Sender.put(connection_IP_Port,node_connect);


        if (Node_info.contains(info))
        {
            System.out.println("Node already registered");
            Reg_Ack reg_ack = new Reg_Ack("Failed");
            byte[] Ack  = reg_ack.getByteArray();
            node_connect.send_and_maintain(Ack);
        }
        else
        {
            System.out.println("Node added to the list");
            Node_info.add(info);
            Node_Count++;
            Reg_Ack reg_ack = new Reg_Ack("Success");
            byte[] Ack  = reg_ack.getByteArray();
            node_connect.send_and_maintain(Ack);
        }

    }

    public synchronized static void DeRegister(byte [] byte_data) throws IOException
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(byte_data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bin));
        String[] info;

        // Read the port
        int port = din.readInt();
        byte[] IP_byte = new byte[byte_data.length - 4];
        din.readFully(IP_byte);
        String IP  = new String(IP_byte);

        info = new String[]{IP, Integer.toString(port)};

        String server_IP_port = IP + ":" + Integer.toString(port);

        String test = IP_Port_Map.get(server_IP_port);

        if (test != null)
        {
            System.out.println("Node : " +server_IP_port +"is removed from the list");
            System.out.println("Send the de-register ack to the node");

            DER_ACK DACK = new DER_ACK();
            byte[] DR_bytes = DACK.getByteArray();

            TCPSender DR_send = TCP_Sender.get(server_IP_port);
            DR_send.send_and_maintain(DR_bytes);

            IP_Port_Map.remove(server_IP_port);

        }
        else
        {
            System.out.println("This node is not in the list");
        }

    }

    public static void setup_overlay(String command) throws IOException
    {
        String [] byParts = command.split(" ");
        int Node_degree = Integer.parseInt(byParts[1]);

        Overlay_Creator overlay = new Overlay_Creator(Node_degree, Node_Count);
        overlay.create_overlay();
        int [][] connections = overlay.getConnection_indicator();
        for (int [] i : connections)
        {
            for (int j : i )
            {
                System.out.print(j + "\t");
            }
            System.out.println();
        }

        weights = overlay.link_weights_assignment();

        print_weights();
        send_messaging_node_info();
    }


    private static void send_messaging_node_info() throws IOException
    {
        build_messaging_node_list();
        for (int i = 0; i < Node_Count; i++)
        {
            Messaging_nodes_list MN_list = new Messaging_nodes_list(MN.get(i));
            byte[] B = MN_list.getByteArray();

            String key = Node_info.get(i)[0] + ":" + Node_info.get(i)[1];
            String right_IP_port = IP_Port_Map.get(key);
//            System.out.println("Right IP port: " +right_IP_port);

            TCPSender MN_sending = TCP_Sender.get(right_IP_port);
            MN_sending.send_and_maintain(B);

        }

    }

    private static void build_messaging_node_list()
    {
        for (int i = 0; i < Node_Count; i++)
        {
            String temp = "";
            for (int j = 0; j < Node_Count; j++)
            {
                if (weights[i][j] != 0 && i > j)
                {
                    temp = temp + Node_info.get(j)[0] + ":" + Node_info.get(j)[1] + ";";
                }
            }
            if (temp.isEmpty() || temp.equals(""))
            {
                temp = "None";
            }
            MN.add(i,temp);
        }
    }

    public static void start_parser(String command) throws IOException
    {
        String [] byParts = command.split(" ");
        String Number_of_rounds = byParts[1];
        Task_Initiate start = new Task_Initiate(Number_of_rounds);
        byte [] TI_msg = start.getByteArray();
        create_and_send(TI_msg);
    }

    public static void send_link_weights() throws IOException
    {
        convert2String();
        Link_Weights LW = new Link_Weights(Link_info);
        byte [] LW_bytes = LW.getByteArray();
        create_and_send(LW_bytes);
    }

    private static void create_and_send(byte[] B) throws IOException
    {
        for(String [] Node : Node_info)
        {
            String key = Node[0] + ":" + Node[1];
            String IP_port_value = IP_Port_Map.get(key);
            System.out.println(IP_port_value);

            TCPSender Wt_send = TCP_Sender.get(IP_port_value);
            Wt_send.send_and_maintain(B);
//            Socket LW_sock = new Socket(Node[0], Integer.parseInt(Node[1]));
//            TCPSender Wt_send = new TCPSender(LW_sock);
//            Wt_send.send_data(B);
        }
    }

    private static void convert2String()
    {
        for(int i = 0; i < Node_Count; i++)
        {
            for (int j = 0; j < Node_Count; j++)
            {
                if (i>j)
                {
                    if (weights[i][j] != 0)
                    {
                        String temp = Node_info.get(i)[0] + ":" + Node_info.get(i)[1] + " " +
                                Node_info.get(j)[0] + ":" + Node_info.get(j)[1] + " " +
                                Integer.toString(weights[i][j]) + ";";

                        Link_info.add(temp);
                    }
                }

            }
        }

    }

    public synchronized static void Task_complete_parser(byte[] byte_data) throws InterruptedException, IOException
    {
        System.out.println("Task Complete message received");
        Task_complete_tracker++;
        if (Task_complete_tracker == Node_Count)
        {
            System.out.println("Wait for 40 seconds");
            TimeUnit.SECONDS.sleep(40);
            System.out.println("Send pull traffic summary message");
            send_pull_traffic_summary();
        }

    }

    private static void send_pull_traffic_summary() throws IOException
    {
        Pull_traffic_summary pull = new Pull_traffic_summary();
        byte [] byte_message = pull.getByteArray();
        create_and_send(byte_message);
    }


    public synchronized static void increment_track_counter()
    {
        System.out.println("Node count is: " +Node_Count);
        summary_tracker++;
        System.out.println("Summary Tracker: " +summary_tracker);
        if (summary_tracker == Node_Count)
        {
            print_traffic_summary();
            summary_tracker = 0;
        }
    }


//-------------------------------------------- Printing--------------------------------------------

    public static void print_node_info()
    {
        if(Node_info.isEmpty())
        {
            System.out.println("No Nodes Registered yet!!");
        }
        else
        {
            System.out.println("\n");
            System.out.println("Node IP" + "\t" + "Port");
            for(String[] node: Node_info)
            {
                System.out.println("\n");
                System.out.println(node[0] +"\t" + node[1]);
            }
        }
    }


    private static void print_weights()
    {
        System.out.println();

        for (int [] i : weights)
        {
            for (int j : i )
            {
                System.out.print(j + "\t");
            }
            System.out.println();
        }
    }

}
