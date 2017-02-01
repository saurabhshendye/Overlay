/*
 * Created by Saurabh Shendye on 1/24/17.
 */

package cs455.overlay.node;

import cs455.overlay.UserIn.User_Input;
import cs455.overlay.WireFormats.Reg_Ack;
import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.Overlay_Creator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.System.in;

public class Registry extends Node
{
    private static int Node_Count;
    private static ArrayList<String[]> Node_info = new ArrayList<String[]>();
//    private static ArrayList<String[]> Node_info;

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

    public static void setup_overlay(String command)
    {
        int command_len = command.length();
        int Node_degree = Character.getNumericValue(command.charAt(command_len - 1));

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
    }

}
