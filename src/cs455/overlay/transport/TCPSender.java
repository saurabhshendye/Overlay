/*
 * Created by Saurabh Shendye on 1/24/17.
 */

package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender
{
    private Socket socket;
    private DataOutputStream dout;

    public TCPSender(Socket S) throws IOException {
        this.socket = S;
        dout = new DataOutputStream(socket.getOutputStream());
    }

    public synchronized void send_data(byte[] data_to_send) throws IOException
    {
        int D_len = data_to_send.length;
        dout.writeInt(D_len);
        dout.write(data_to_send,0,D_len);
        dout.flush();
        dout.close();
    }

    public synchronized void send_and_maintain(byte[] data_to_send) throws IOException
    {
        int D_len = data_to_send.length;
        dout.writeInt(D_len);
        dout.write(data_to_send, 0, D_len);
        dout.flush();
    }


}
