/*
 * Created by saurabh on 2/7/17.
 */

package cs455.overlay.util;

import java.util.ArrayList;

public class links_to_array
{
    private int Node_count;
    private ArrayList<String []> link_info;
    private ArrayList<String> Nodes;

    public links_to_array(int Node_count, ArrayList<String []> link_info, ArrayList<String> Nodes)
    {
        this.Node_count = Node_count;
        this.link_info = link_info;
        this.Nodes = Nodes;

    }

    public int [][] getAdjecencyMatrix()
    {
        int [][] weights = new int[Node_count][Node_count];

        for (String [] link : link_info)
        {
            int i = Nodes.indexOf(link[0]);
            int j = Nodes.indexOf(link[1]);

            weights[i][j] = Integer.parseInt(link[2]);
            weights[j][i] = Integer.parseInt(link[2]);
        }


        return weights;
    }
}
