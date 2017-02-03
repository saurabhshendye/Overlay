/*
 * Created by Saurabh Shendye on 1/24/17.
 */

package cs455.overlay.node;

import cs455.overlay.UserIn.User_Input;
import cs455.overlay.WireFormats.Link_Weights;
import cs455.overlay.WireFormats.Reg_Ack;
import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.Overlay_Creator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Registry extends Node
{
    private static int Node_Count;
    private static ArrayList<String[]> Node_info = new ArrayList<>();
    private static int [][] weights;
    private static ArrayList<String> Link_info = new ArrayList<>();


    Registry(int port) throws IOException {
        super(port);
    }


    public static void main(String args[]) throws IOException
    {
        // Create a Server Socket and bind it to a given port
        ServerSocket Reg_server = new ServerSocket(Integer.parseInt(args[0]), 5);
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
            thread_2.start();
        }
    }

    public static synchronized void getRegistered(byte[] byte_data) throws IOException
    {

        ByteArrayInputStream bin = new ByteArrayInputStream(byte_data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bin));
        String[] info;
        int port = din.readInt();

        System.out.println("Port Number is: " + port);

        byte[] IP_byte = new byte[byte_data.length - 4];
        din.readFully(IP_byte);
        String IP  = new String(IP_byte);
        System.out.println("IP Address is : " + IP);

        info = new String[]{IP, Integer.toString(port)};

        Socket temp_ack_send = new Socket(IP,port);
        TCPSender send_ack = new TCPSender(temp_ack_send);
        if (Node_info.contains(info))
        {
            System.out.println("Node already registered");
            Reg_Ack reg_ack = new Reg_Ack("Failed");
            byte[] Ack  = reg_ack.getByteArray();
            send_ack.send_data(Ack);
        }
        else
        {
            System.out.println("Node added to the list");
            Node_info.add(info);
            Node_Count++;
            Reg_Ack reg_ack = new Reg_Ack("Success");
            byte[] Ack  = reg_ack.getByteArray();
            send_ack.send_data(Ack);
        }

    }

    public static void setup_overlay(String command)
    {
//        int command_len = command.length();
//        int Node_degree = Character.getNumericValue(command.charAt(command_len - 1));
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
            Socket LW_sock = new Socket(Node[0], Integer.parseInt(Node[1]));
            TCPSender Wt_send = new TCPSender(LW_sock);
            Wt_send.send_data(B);
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
