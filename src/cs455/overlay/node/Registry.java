/*
 * Created by Saurabh Shendye on 1/24/17.
 */

package cs455.overlay.node;

import cs455.overlay.UserIn.User_Input;
import cs455.overlay.transport.TCPReceiver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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

    public static void getRegistered(byte[] byte_data) throws IOException
    {
//        System.out.println("Got Registered : " +data);
//        byte[] byte_data = data.getBytes();
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
        Node_info.add(info);
    }

}
