/*
 * Created by saurabh on 2/4/17.
 */


package cs455.overlay.WireFormats;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class establish_connection_msg
{
    private int type = 4;
    private int Port;
    private String IP;
    private int local_port;

    public establish_connection_msg(String IP, int local_port, int Port)
    {
        this.IP = IP;
        this.local_port = local_port;
        this.Port = Port;

    }

    public byte[] getBytearray() throws IOException
    {
//        This method creates a byte array which needs to be written onto the socket
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte[] IP_array = this.IP.getBytes();
        int IP_Len = IP_array.length;
        int Len = IP_Len + 8;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.writeInt(this.Port);
        dout.writeInt(this.local_port);
        dout.write(IP_array);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
