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

//        for(int i = 0; i < Node_count; i++)
//        {
//            for (int j = 0; j < Node_count; j++)
//            {
//                if (i != j && weights[i][j] == 0)
//                {
//                    weights[i][j] = Integer.MAX_VALUE;
//                }
//            }
//
//        }


        return weights;
    }
}
