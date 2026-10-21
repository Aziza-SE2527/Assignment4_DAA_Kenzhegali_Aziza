package dsu;

public class DSU
{
    private int[] parent;
    private int[] size;
    public long operationsCount= 0;

    public DSU(int n)
    {
        parent= new int[n];
        size= new int[n];
        for (int i= 0;i<n;i++)
        {
            parent[i]= i;
            size[i]= 1;
        }
    }

    public int find(int i)
    {
        operationsCount++;
        if (parent[i]==i)
        {
            return i;
        }
        parent[i]= find(parent[i]); //Сжатие путей
        return parent[i];
    }

    public boolean union(int i,int j)
    {
        int rootI= find(i);
        int rootJ= find(j);
        operationsCount++;

        if (rootI !=rootJ)
        {
            if (size[rootI]< size[rootJ])
            {
                parent[rootI] = rootJ;
                size[rootJ]+= size[rootI];
            } else {
                parent[rootJ]= rootI;
                size[rootI]+= size[rootJ];
            }
            return true;
        }
        return false;
    }
}