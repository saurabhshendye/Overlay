/*
 * Created by saurabh on 2/12/2017.
 */

package cs455.overlay.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static cs455.overlay.node.Registry.increment_track_counter;

public class StatsCollectorAndDisplay
{
    private static ConcurrentHashMap<String, String> Receive_track = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> send_track = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> relayed_track = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> receive_summation = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> send_summation = new ConcurrentHashMap<>();
    private static ArrayList<String> Node_Ids = new ArrayList<>();


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
        Node_Ids.add(ID);

        Receive_track.put(ID, Integer.toString(receive));
        send_track.put(ID, Integer.toString(sent));
        relayed_track.put(ID, Integer.toString(relayed));
        receive_summation.put(ID, Long.toString(rec_sum));
        send_summation.put(ID, Long.toString(sent_sum));

        increment_track_counter();

    }

    public static void print_traffic_summary()
    {
        System.out.println("Printing Traffic Summary");
        System.out.println("Node ID" + "\t" + "Received" + "\t" + "Sent");

        for (String N: Node_Ids)
        {
            System.out.println(N + "\t" + Receive_track.get(N) + "\t" + send_track.get(N));
        }
    }

//    public static void main(String [] args)
//    {
//
//    }
}
