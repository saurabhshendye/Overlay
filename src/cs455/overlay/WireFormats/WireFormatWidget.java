/*
 * Created by saurabh on 1/27/17.
 */

package cs455.overlay.WireFormats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static cs455.overlay.node.Messaging_Node.*;
import static cs455.overlay.node.Registry.getRegistered;

public class WireFormatWidget
{
    private int type;
    private byte[] identifier;

    public WireFormatWidget(byte[] marshaledBytes) throws IOException
    {
        System.out.println("In the constructor of Wire Format");
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshaledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.type = din.readInt();
        System.out.println("Type in Wire Format Constructor: " +this.type);
        int Len = din.readInt();
        System.out.println("Length in Wire Format Constructor: " +Len);
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

    public void Task_initiate_widget()
    {
        System.out.println("Task Initiate message received");
        Task_Initiate_Parser(this.identifier);
    }
}
