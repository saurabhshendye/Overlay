/*
 * Created by saurabh on 1/24/17.
 */
package cs455.overlay.transport;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;


public class TCPReceiver extends Thread {

    private Socket Serving;
    private DataInputStream din;

    public TCPReceiver(Socket S) throws IOException
    {
        this.Serving = S;
        din = new DataInputStream(Serving.getInputStream());
    }

    public void run()
    {
        int D_len;
        while(Serving != null)
        {
            try
            {
                D_len = din.readInt();
                byte[] data = new byte[D_len];
//                InputStream din = Serving.getInputStream();
                din.readFully(data, 0, D_len);

                String msg = new String(data);
                System.out.print("Received Message" + msg);
            }
            catch (IOException e1)
            {
                System.out.println(e1.getMessage());
            }
        }

    }
}
