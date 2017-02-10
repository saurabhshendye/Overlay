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
    private int [] distance;

    public Shortest_Path(int [][] weight_graph, ArrayList<String> Nodes, String source, int Node_count)
    {
        this.Nodes = Nodes;
        this.weight_graph = weight_graph;
        this.source = source;
        this.Node_count = Node_count;
        this.distance = new int[Node_count];
    }

    public void calculate_distances()
    {
        //Distance and visited arrays initialized

        boolean [] visited = new boolean[Node_count];

        // Getting the index of source node
        int index = Nodes.indexOf(source);

        // Making all distances equal to infinity and visited flags to false
        distance = distance_initialization(distance);
        visited = visited_initialization(visited);

        // Setting the distance of src node from itself to zero
        // Also marking the source node as visited
        distance[index] = 0;
//        visited[index] = true;

        // Find the nearest node


        for (int i = 0; i < Node_count - 1; i++)
        {
            int nearest_node_index = find_nearest_node(visited, distance);
            visited[nearest_node_index] = true;

            for (int j = 0; j < Node_count; j++)
            {
                if (!visited[j] && weight_graph[nearest_node_index][j] != 0)
                {
                    if (distance[nearest_node_index] != Integer.MAX_VALUE)
                    {
                        if (distance[j] > distance[nearest_node_index] + weight_graph[nearest_node_index][j])
                        {
                            distance[j] = distance[nearest_node_index] + weight_graph[nearest_node_index][j];
                        }
                    }
                }
            }

            if  (i != index)
            {

            }
        }
    }

    private int find_nearest_node(boolean [] visited, int [] distance)
    {
        int shortest_distance = Integer.MAX_VALUE;
        int nearest_node_index = -1;

        for (int i = 0; i < Node_count; i++)
        {
            if (!visited[i] && distance[i] <= shortest_distance)
            {
                shortest_distance = distance[i];
                nearest_node_index = i;
            }
        }
        return nearest_node_index;
    }

    private int [] distance_initialization(int [] distance)
    {
        for (int i = 0; i < Node_count; i++)
        {
            distance[i] = Integer.MAX_VALUE;
        }
        return distance;
    }

    private boolean [] visited_initialization(boolean [] visited)
    {
        for (int i = 0; i < Node_count; i++)
        {
            visited[i] = false;
        }
        return visited;
    }

    public void print_distances()
    {
        System.out.println();
        for (int i : distance)
        {
            System.out.print(i + "/t");
        }
    }
}
