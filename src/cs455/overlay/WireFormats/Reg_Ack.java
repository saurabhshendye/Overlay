package cs455.overlay.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
 * Created by saurabh on 1/29/17.
 */
public class Reg_Ack {
    private String code;
    private int type = 10;

    public Reg_Ack(String code)
    {
        this.code = code;
    }

    public byte[] getByteArray() throws IOException
    {
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte[] code_array = this.code.getBytes();
        int Len = code_array.length + 4;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.write(code_array);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
