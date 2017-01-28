/*
 * Created by saurabh on 1/27/17.
 */

package cs455.overlay.WireFormats;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

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

    public byte[] getBytes() throws IOException
    {
        byte[] marshaled = null;
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baopstream);
        dout.writeInt(type);
        byte[] b = this.getBytes();
        int Len = b.length;
        dout.writeInt(Len);
        dout.write(b);
        dout.flush();

        marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
