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
    private int Node_count;

    public Shortest_Path(int [][] weight_graph, ArrayList<String> Nodes, String source, int Node_count)
    {
        this.Nodes = Nodes;
        this.weight_graph = weight_graph;
        this.source = source;
        this.Node_count = Node_count;
    }

    public void calculatie_distances()
    {
        //Distance and visited arrays initialized
        int [] distance = new int[Node_count];
        boolean [] visited = new boolean[Node_count];

        // Getting the index of source node
        int index = Nodes.indexOf(source);

        // Setting the distance of src node from itself to zero
        // Also marking the source node as visited
        distance[index] = 0;
        visited[index] = true;

        for (int i = 0; i < Node_count - 1; i++)
        {
            if  (i != index)
            {

            }
        }




    }
}
