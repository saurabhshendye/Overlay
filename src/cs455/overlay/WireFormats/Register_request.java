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
    private int port;
    private String IP;
    private int type = 0;

    public Register_request(int port, String IP)
    {
        this.IP = IP;
        this.port = port;
    }

    public byte[] getBytearray() throws IOException
    {

        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));
        dout.writeInt(type);

        byte[] IP_array = this.IP.getBytes();
        int IP_Len = IP_array.length;
        int Len = IP_Len + 4;
        dout.writeInt(Len);
        dout.writeInt(port);
        dout.write(IP_array);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
