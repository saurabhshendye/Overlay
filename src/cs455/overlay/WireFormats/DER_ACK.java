/*
 * Created by saurabh on 2/14/2017.
 */
package cs455.overlay.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DER_ACK
{
    private int type = 17;
    private String code;

    public DER_ACK()
    {
        this.code = "De-Registered";
    }

    public byte [] getByteArray() throws IOException
    {
        // This method creates a byte array which needs to be written onto the socket
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte [] code_bytes = code.getBytes();
        int Len = code_bytes.length;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.write(code_bytes);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
