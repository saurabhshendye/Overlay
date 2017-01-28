/*
 * Created by saurabh on 1/27/17.
 */

package cs455.overlay.WireFormats;

import java.net.InetAddress;

public class Register_request
{
    private int port;
    private InetAddress IP;
    private int type = 0;

    public Register_request(int port, InetAddress IP)
    {
        this.IP = IP;
        this.port = port;
    }

}
