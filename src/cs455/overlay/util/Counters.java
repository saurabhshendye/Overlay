package cs455.overlay.util;

/*
 * Created by saurabh on 2/10/2017.
 */

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Counters
{
    private int type = 9;
    private int receive_tracker;
    private int sent_tracker;
    private int relayed_tracker;
    private long sent_summation;
    private long receive_summation;
    private String IP_Port;

    public Counters(String IP_Port)
    {
        this.receive_tracker = 0;
        this.sent_tracker = 0;
        this.relayed_tracker = 0;
        this.sent_summation = 0;
        this.receive_summation = 0;
        this.IP_Port = IP_Port;
    }

    public void set_counters_toZero()
    {
        this.receive_tracker = 0;
        this.sent_tracker = 0;
        this.relayed_tracker = 0;
        this.sent_summation = 0;
        this.receive_summation = 0;
    }

    public void increment_rx(long num)
    {
        receive_tracker++;
        receive_summation = receive_summation + num;
    }

    public void increment_tx(long num)
    {
        sent_tracker++;
        sent_summation = sent_summation + num;
    }

    public void increment_relayed()
    {
        relayed_tracker++;
    }

    public int getReceive_tracker()
    {
        return receive_tracker;
    }

    public int getSent_tracker()
    {
        return sent_tracker;
    }

    public int getRelayed_tracker()
    {
        return relayed_tracker;
    }

    public long getSent_summation()
    {
        return sent_summation;
    }

    public long getReceive_summation()
    {
        return receive_summation;
    }

    public byte [] getByteArray() throws IOException
    {
        // This method creates a byte array which needs to be written onto the socket
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte [] IP_P_bytes = IP_Port.getBytes();
//        int Len = 28;
        int Len = IP_P_bytes.length;
        Len = Len + 28;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.writeInt(receive_tracker);
        dout.writeInt(sent_tracker);
        dout.writeInt(relayed_tracker);
        dout.writeLong(receive_summation);
        dout.writeLong(sent_summation);
        dout.write(IP_P_bytes);
        dout.flush();


        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }

}
