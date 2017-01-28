/*
 * Created by saurabh on 1/27/17.
 */

package cs455.overlay.WireFormats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class WireFormatWidget {
    private int type;
//    private byte[] data;

    public WireFormatWidget(byte[] marshaledBytes) throws IOException
    {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshaledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.type = din.readInt();

        int Len = din.readInt();
        byte[] data = new byte[Len];

        din.readFully(data);

        din.close();
        baInputStream.close();
    }
}
