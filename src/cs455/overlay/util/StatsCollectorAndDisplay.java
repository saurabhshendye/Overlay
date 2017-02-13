/*
 * Created by saurabh on 2/12/2017.
 */

package cs455.overlay.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class StatsCollectorAndDisplay
{
    private static ConcurrentHashMap<String, String> Receive_track = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> send_track = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> relayed_track = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> receive_summation = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> send_summation = new ConcurrentHashMap<>();


    public synchronized static void traffic_summary_parser(byte [] byte_data) throws IOException
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(byte_data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

        int receive = din.readInt();
        int sent = din.readInt();
        int relayed = din.readInt();
        long rec_sum = din.readLong();
        long sent_sum = din.readLong();

        byte [] ID_byte = new byte[byte_data.length - 28];
        din.readFully(ID_byte);

        String ID = new String(ID_byte);

        Receive_track.put(ID, Integer.toString(receive));
        send_track.put(ID, Integer.toString(sent));
        relayed_track.put(ID, Integer.toString(relayed));
        receive_summation.put(ID, Long.toString(rec_sum));
        send_summation.put(ID, Long.toString(sent_sum));

    }

//    public static void main(String [] args)
//    {
//
//    }
}
