package cs455.overlay.util;

/*
 * Created by saurabh on 2/10/2017.
 */

public class Counters
{
    private int receive_tracker;
    private int sent_tracker;
    private int relayed_tracker;
    private long summation_tracker;

    public Counters()
    {
        this.receive_tracker = 0;
        this.sent_tracker = 0;
        this.relayed_tracker = 0;
        this.summation_tracker = 0;
    }
}
