package cs455.overlay.util;

/*
 * Created by saurabh on 2/10/2017.
 */

public class Counters
{
    private int receive_tracker;
    private int sent_tracker;
    private int relayed_tracker;
    private long sent_summation;
    private long receive_summation;

    public Counters()
    {
        this.receive_tracker = 0;
        this.sent_tracker = 0;
        this.relayed_tracker = 0;
        this.sent_summation = 0;
        this.receive_summation = 0;
    }

    public void set_counters_toZero()
    {
        this.receive_tracker = 0;
        this.sent_tracker = 0;
        this.relayed_tracker = 0;
        this.sent_summation = 0;
        this.receive_summation = 0;
    }

    public synchronized void increment_rx(long num)
    {
        receive_tracker++;
        receive_summation = receive_summation + num;
    }

    public synchronized void increment_tx(long num)
    {
        sent_tracker++;
        sent_summation = sent_summation + num;
    }

}
