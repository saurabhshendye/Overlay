/*
 * Created by saurabh on 2/10/2017.
 */

package cs455.overlay.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message_Exchange
{
    private String destination;
    private long num;
    private int type = 6;

    public Message_Exchange(String dest, long num)
    {
        this.destination = dest;
        this.num = num;
    }

    public byte [] getByteArray() throws IOException
    {
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte [] dest_bytes = destination.getBytes();
        int Len = dest_bytes.length;
        Len = Len + 8;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.writeLong(num);
        dout.write(dest_bytes);
        dout.flush();


        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }


}
