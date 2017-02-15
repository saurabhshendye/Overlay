/*
 * Created by saurabh on 1/27/17.
 */

package cs455.overlay.WireFormats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static cs455.overlay.node.Messaging_Node.*;
import static cs455.overlay.node.Registry.DeRegister;
import static cs455.overlay.node.Registry.Task_complete_parser;
import static cs455.overlay.node.Registry.getRegistered;
import static cs455.overlay.util.StatsCollectorAndDisplay.traffic_summary_parser;


public class WireFormatWidget
{
    private int type;
    private byte[] identifier;

    public WireFormatWidget(byte[] marshaledBytes) throws IOException
    {
//        System.out.println("In the constructor of Wire Format");
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshaledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.type = din.readInt();
//        System.out.println("Type in Wire Format Constructor: " +this.type);
        int Len = din.readInt();
//        System.out.println("Length in Wire Format Constructor: " +Len);
        byte[] data = new byte[Len];


        din.readFully(data);
        identifier = data;
//        System.out.println("Identifier: " + identifier);

        din.close();
        baInputStream.close();
    }

    public int getType()
    {
        return this.type;
    }

    public void register() throws IOException
    {
        System.out.println("Getting the node registered..");
        getRegistered(this.identifier);
    }

    public void reg_ack()
    {
        System.out.println("Registration Complete");
        reg_ack_parser(this.identifier);
    }

    public void link_info_widget()
    {
        System.out.println("Link information received");
        link_info_parser(this.identifier);
    }

    public void messaging_info_widget() throws IOException
    {
        System.out.println("Messaging node list received");
        messaging_node_list_parser(this.identifier);
    }

    public void connection_establishment() throws IOException
    {
        System.out.println("Connection establishment message received.");
        connection_request_parser(this.identifier);
    }

    public void Task_initiate_widget() throws IOException
    {
        System.out.println("Task Initiate message received");
        Task_Initiate_Parser(this.identifier);
    }

    public void peer_message() throws IOException
    {
        peer_message_parser(this.identifier);
    }

    public void de_register() throws IOException
    {
        System.out.println("De-register message received");
        DeRegister(this.identifier);
    }

    public void Task_complete_widget() throws IOException, InterruptedException
    {
        Task_complete_parser(this.identifier);
    }

    public void pull_summary_widget() throws IOException
    {
        System.out.println("Pull traffic summary message received");
        traffic_summary_request_parser();
    }

    public void summary_widget() throws IOException
    {
        System.out.println("Summary received");
        traffic_summary_parser(this.identifier);
    }

    public void DE_ACK_Widget()
    {
        System.out.println("Deregister ACK received");
        exit_all();
    }

}
