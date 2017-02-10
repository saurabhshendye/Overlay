/*
 * Created by saurabh on 2/10/2017.
 */

package cs455.overlay.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Task_Initiate
{
    private int type = 5;
    private int rounds;

    public Task_Initiate(int Num_rounds)
    {
        this.rounds = Num_rounds;
    }

    public byte [] getByteArray() throws IOException
    {
        // This method creates a byte array which needs to be written onto the socket
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));



        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
