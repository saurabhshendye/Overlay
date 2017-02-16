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
    private static int receive_O = 0;
    private static int send_O = 0;
    private static int relayed_O = 0;
    private static long rec_sum_O = 0;
    private static long sent_sum_O = 0;
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
        receive_O = receive_O + receive;

        int sent = din.readInt();
        send_O = send_O + sent;

        int relayed = din.readInt();
        relayed_O = relayed_O + relayed;

        long rec_sum = din.readLong();
        rec_sum_O = rec_sum_O + rec_sum;

        long sent_sum = din.readLong();
        sent_sum_O = sent_sum_O + sent_sum;

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
        System.out.println();
        System.out.println("Printing Traffic Summary");
        System.out.println();
        final Object[][] table = new String[Node_Ids.size() + 2][];
//        System.out.println("Node ID" + "\t\t" + "Received" + "\t" + "Sent");
        table[0] = new String[] {"Node ID", "Received Msg Count", "Sent Msg Count",
                                    "Relayed Msg Count", "Sum of Rcvd values",
                                "Sum of sent values"};

        for (int i = 1; i <= Node_Ids.size(); i++)
        {
//            System.out.println(N + "\t" + Receive_track.get(N) + "\t" + send_track.get(N));
//            System.out.format()
            String N = Node_Ids.get(i-1);
            table[i] = new String []{N, Receive_track.get(N), send_track.get(N), relayed_track.get(N),
                                        receive_summation.get(N), send_summation.get(N)};
        }

        table[Node_Ids.size() + 1] = new String[] {"Total", Integer.toString(receive_O), Integer.toString(send_O),
                                        Integer.toString(relayed_O), Long.toString(rec_sum_O),
                                        Long.toString(sent_sum_O)};

        for (final Object[] row: table)
        {
            System.out.format("%22s%22s%22s%22s%22s%22s\n", row);
        }
    }

    public static void make_all_zero()
    {
        receive_O = 0;
        send_O = 0;
        relayed_O = 0;
        rec_sum_O = 0;
        sent_sum_O = 0;
        Node_Ids.clear();
    }

//    public static void main(String [] args)
//    {
//
//    }
}
