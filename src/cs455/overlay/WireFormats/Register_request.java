/*
 * Created by saurabh on 1/27/17.
 */

package cs455.overlay.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Register_request
{
//    This class creates a register message object

    private int port;
    private String IP;
    private int type = 0;
    private int local_port;

    public Register_request(int port, int local_port, String IP)
    {
        this.IP = IP;
        this.port = port;
        this.local_port = local_port;
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
        dout.writeInt(this.port);
        dout.writeInt(this.local_port);
        dout.write(IP_array);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }

}
