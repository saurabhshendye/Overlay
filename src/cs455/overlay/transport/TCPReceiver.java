/*
 * Created by saurabh on 1/24/17.
 */
package cs455.overlay.transport;
import cs455.overlay.WireFormats.WireFormatWidget;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;


public class TCPReceiver extends Thread
{

    private Socket Serving;
    private DataInputStream din;

    public TCPReceiver(Socket S) throws IOException
    {
        this.Serving = S;
        din = new DataInputStream(Serving.getInputStream());
    }

    public void run()
    {

        try
        {
            int D_len = din.readInt();
            byte[] data = new byte[D_len];
            din.readFully(data, 0, D_len);
            WireFormatWidget WireFormat = new WireFormatWidget(data);

            int type = WireFormat.getType();
            switch (type)
            {
                case 0: WireFormat.register();              // 0 for Registration Request
                        break;
            }

        }
        catch (IOException e1)
        {
        System.out.println(e1.getMessage());
        }

    }
}
