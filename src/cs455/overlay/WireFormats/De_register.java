/*
 * Created by saurabh on 1/27/17.
 */
package cs455.overlay.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class De_register
{
    private int port;
    private String IP;
    private int type = 7;

    public De_register(String IP, int port)
    {
        this.IP = IP;
        this.port = port;
    }

    public byte [] getByteArray() throws IOException
    {
        // This method creates a byte array which needs to be written onto the socket
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte[] IP_array = this.IP.getBytes();
        int IP_Len = IP_array.length;
        int Len = IP_Len + 4;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.writeInt(this.port);
        dout.write(IP_array);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
