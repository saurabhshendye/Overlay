package cs455.overlay.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
 * Created by saurabh on 2/11/2017.
 */
public class Task_complete
{
    private int type = 16;
    private String task_cmplt;

    public Task_complete()
    {
        this.task_cmplt = "Task-Complete";
    }

    public byte [] getByteArray() throws IOException
    {
        // This method creates a byte array which needs to be written onto the socket
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte [] bytes = task_cmplt.getBytes();
        int Len = bytes.length;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.write(bytes);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }

}
