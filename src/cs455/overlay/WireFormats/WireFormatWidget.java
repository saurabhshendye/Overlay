/*
 * Created by saurabh on 1/27/17.
 */

package cs455.overlay.WireFormats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static cs455.overlay.node.Registry.getRegistered;

public class WireFormatWidget {
    private int type;
    private byte[] identifier;

    public WireFormatWidget(byte[] marshaledBytes) throws IOException
    {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshaledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.type = din.readInt();

        int Len = din.readInt();
        byte[] data = new byte[Len];

        din.readFully(data);
        identifier = data;

        din.close();
        baInputStream.close();
    }

    public int getType()
    {
        return this.type;
    }

    public void register() throws IOException
    {
        System.out.println("Getting the node registered..");
        getRegistered(this.identifier);

    }

    public void reg_ack()
    {
        System.out.println("Registration Complete");
    }

}
