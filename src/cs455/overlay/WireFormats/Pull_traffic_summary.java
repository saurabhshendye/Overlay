/*
 * Created by saurabh on 2/12/2017.
 */
package cs455.overlay.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Pull_traffic_summary
{
    private int type = 8;
    private String message;

    public Pull_traffic_summary()
    {
        this.message = "Pull Traffic Summary";
    }

    public byte [] getByteArray() throws IOException
    {
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte [] message_bytes = message.getBytes();
        int Len = message_bytes.length;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.write(message_bytes);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
