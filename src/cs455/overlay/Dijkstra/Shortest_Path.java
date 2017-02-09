/*
 * Created by saurabh on 2/8/17.
 */

package cs455.overlay.Dijkstra;

import java.util.ArrayList;

public class Shortest_Path
{
    private int [][] weight_graph;
    private ArrayList<String> Nodes;
    private String source;

    public Shortest_Path(int [][] weight_graph, ArrayList<String> Nodes, String source)
    {
        this.Nodes = Nodes;
        this.weight_graph = weight_graph;
        this.source = source;
    }

    public void calculatie_distances()
    {
        int index = Nodes.indexOf(source);

    }
}
