/*
 * Created by saurabh on 2/8/17.
 */

package cs455.overlay.Dijkstra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class Shortest_Path
{
    private int [][] weight_graph;
    private ArrayList<String> Nodes;
    private String source;
    private int Node_count;
    private int [] distance;
    private ArrayList<String> Adjacent;
    private ConcurrentHashMap<String, String> Successors;

    public Shortest_Path(int [][] weight_graph, ArrayList<String> Nodes, String source)
    {
        this.Nodes = Nodes;
        this.weight_graph = weight_graph;
        this.source = source;
        this.Node_count = Nodes.size();
        this.distance = new int[Node_count];
        this.Adjacent = new ArrayList<>();
        this.Successors = new ConcurrentHashMap<>();
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

        // get the adjacent nodes
        getAdjacent(index);

        // Setting the distance of src node from itself to zero
        // Also marking the source node as visited
        distance[index] = 0;
//        visited[index] = true;

        // Find the nearest node



        for (int i = 0; i < Node_count - 1; i++)
        {
            int nearest_node_index = find_nearest_node(visited, distance);
            visited[nearest_node_index] = true;

            // Make a distance reference array in order to detect the changes
            // and thereby detecting the successor
            int [] dist_reference = new int[Node_count];
            System.arraycopy(distance,0, dist_reference,0, distance.length);
//            dist_reference = distance;

            for (int j = 0; j < Node_count; j++)
            {
                if (!visited[j] && weight_graph[nearest_node_index][j] != 0)
                {
                    if (distance[nearest_node_index] != Integer.MAX_VALUE)
                    {
                        if (distance[j] > distance[nearest_node_index] + weight_graph[nearest_node_index][j])
                        {
                            distance[j] = distance[nearest_node_index] + weight_graph[nearest_node_index][j];
                            System.out.println("Modified the distance array");
                        }
                    }
                }
            }

            find_difference(dist_reference, nearest_node_index);

            if  (i != index)
            {

            }
        }
    }

    private void find_difference(int[] reference, int nearest)
    {
        String current_node = Nodes.get(nearest);
        System.out.println("Checking for the difference..");
        if (!Arrays.equals(reference, distance))
        {
            System.out.println("There is a difference in two arrays");
            for (int i = 0; i < Node_count; i++)
            {
                if (reference[i] != distance[i])
                {
                    if (Adjacent.contains(current_node))
                    {
                        Add_successor(current_node, i);
                    }
                    else
                    {
                        Add_successor(get_successor(Nodes.get(i)), i);
                    }
//                    System.out.println("Added:  " + current_node + " as predecessor for: " + Nodes.get(i));
                    System.out.println();
                }
            }
        }
    }

    private void Add_successor(String current_node, int i)
    {
        // Node_key = destination node
        // node_value = Successor
        System.out.println("Added:  " + current_node + " as predecessor for: " + Nodes.get(i));
        String Node_key = Nodes.get(i);
        Successors.put(Node_key, current_node);

    }

    private String get_successor(String node_key)
    {
//        String Node_key = Nodes.get(i);
         return Successors.get(node_key);

//        return node_value;
    }

    private void getAdjacent(int index)
    {
        for (int i = 0; i < Node_count; i++)
        {
            if (weight_graph[index][i] != 0)
            {
                System.out.println("Node added: " + Nodes.get(i));
                Adjacent.add(Nodes.get(i));
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
        System.out.println("Shortest Distances are: ");
        for (int i : distance)
        {
            System.out.println(i);
        }
    }
}
