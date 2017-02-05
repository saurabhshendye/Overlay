/*
 * Created by saurabh on 2/3/17.
 */

package cs455.overlay.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Messaging_nodes_list
{
    private int type = 3;
    private String MN_info;

    public Messaging_nodes_list(String MN)
    {
        this.MN_info = MN;
    }

    public byte[] getByteArray() throws IOException
    {
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte[] MN_info_bytes = MN_info.getBytes();
        int Len = MN_info_bytes.length;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.write(MN_info_bytes);
        dout.flush();


        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;

    }
}
