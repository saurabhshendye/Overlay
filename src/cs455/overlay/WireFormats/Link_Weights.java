/*
 * Created by saurabh on 2/2/17.
 */

package cs455.overlay.WireFormats;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Link_Weights {

    private ArrayList<String> links;
    private int type = 2;

    public Link_Weights(ArrayList<String> link_info)
    {
        this.links = link_info;
    }

    public byte[] getByteArray() throws IOException
    {
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));
        String Full_link = null;

        for(String Link: links)
        {
            Full_link = Full_link + Link;
        }

        assert Full_link != null;
        byte [] link_bytes = Full_link.getBytes();

        int Len = link_bytes.length;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.write(link_bytes);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;


    }
}
