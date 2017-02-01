/*
 * Created by saurabh on 1/31/17.
 */
package cs455.overlay.util;




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
                    if (i != j)
                    {
                        for (int k = 0; k< (Node_count/2); k++)
                        {
                            if (j+k< Node_count)
                            {
                                connection_indicator[i][j+k] = 1;
                            }
                        }
                    }
                    else
                    {
                        connection_indicator[i][j] = -1;
                    }
                }
            }
        }
    }

    public int [][] getConnection_indicator()
    {
        return connection_indicator;
    }

}
