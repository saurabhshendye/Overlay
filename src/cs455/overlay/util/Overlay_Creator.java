/*
 * Created by saurabh on 1/31/17.
 */
package cs455.overlay.util;


import java.util.Random;

public class Overlay_Creator
{
    private int Node_degree;
    private int Node_count;
    private int connection_indicator [][];



    public Overlay_Creator(int Node_degree, int Node_count)
    {
        this.Node_degree = Node_degree;
        this.Node_count = Node_count;
        this.connection_indicator = new int[Node_count][Node_count];
    }

    public void create_overlay()
    {
        if ((Node_degree & 1) == 0)
        {
            for(int i = 0; i< Node_count; i++)
            {
                for (int j = 0; j< Node_count; j++)
                {
                    if (i == j)
                    {
                        connection_indicator[i][j] = -1;
                        if (j + 2 >= Node_count)
                        {
                            connection_indicator[i][(j+1) % Node_count] = 1;
                            connection_indicator[i][(j+2) % Node_count] = 1;

                            connection_indicator[(j+1) % Node_count][i] = 1;
                            connection_indicator[(j+2) % Node_count][i] = 1;
                        }
                        else
                        {
                            connection_indicator[i][j+1] = 1;
                            connection_indicator[i][j+2] = 1;

                            connection_indicator[j+1][i] = 1;
                            connection_indicator[j+2][i] = 1;


                        }
                    }
                }
            }
        }
    }

    public int[][] link_weights_assignment()
    {
        System.out.println("Assigning link weights..");

        int [][] weights = new int[Node_count][Node_count];
        Random Generate = new Random();

        for (int i = 0; i < Node_count; i++ )
        {
            for (int j = 0; j < Node_count; j++)
            {
                if(connection_indicator[i][j] == 1)
                {

                    weights[i][j] = Generate.nextInt(10) + 1;
                }
            }
        }

        return weights;
    }


    public int [][] getConnection_indicator()
    {
        return connection_indicator;
    }

}
