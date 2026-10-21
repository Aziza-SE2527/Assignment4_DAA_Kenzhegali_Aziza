package graph;

import java.util.*;

public class Graph
{
    public int V;
    public boolean isDirected;
    public List<Edge> allEdges;
    public List<List<Edge>> adj;

    public Graph(int V,boolean isDirected)
    {
        this.V= V;
        this.isDirected= isDirected;
        this.allEdges= new ArrayList<>();
        this.adj= new ArrayList<>();
        for (int i=0;i<V;i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int weight)
    {
        Edge e1= new Edge(u,v,weight);
        allEdges.add(e1);
        adj.get(u).add(e1);

        if (!isDirected)
        {
            Edge e2= new Edge(v,u,weight);
            adj.get(v).add(e2);
        }
    }

    public static Graph generateConnectedUndirectedGraph(int V, int E, long seed)
    {
        Random rand= new Random(seed);
        Graph g =new Graph(V, false);
        Set<String> existingEdges= new HashSet<>();

        for (int i=1; i<V;i++)
        {
            int u= rand.nextInt(i);
            int w =rand.nextInt(100)+1;
            g.addEdge(u,i, w);
            existingEdges.add(Math.min(u,i) + "-" + Math.max(u, i));
        }

        while (g.allEdges.size()< E)
        {
            int u= rand.nextInt(V);
            int v= rand.nextInt(V);
            if (u != v)
            {
                String key = Math.min(u, v) + "-" + Math.max(u, v);
                if (!existingEdges.contains(key))
                {
                    existingEdges.add(key);
                    g.addEdge(u,v,rand.nextInt(100)+ 1);
                }
            }
        }
        return g;
    }

    public static Graph generateDirectedGraph(int V,int E,boolean allowCycles,long seed)
    {
        Random rand= new Random(seed);
        Graph g= new Graph(V, true);
        Set<String> existingEdges=new HashSet<>();

        while (g.allEdges.size() < E)
        {
            int u= rand.nextInt(V);
            int v= rand.nextInt(V);
            if (u== v) continue;

            if (!allowCycles && u > v)
            {
                int temp= u;u= v;v= temp;
            }

            String key= u + "->"+ v;
            if (!existingEdges.contains(key))
            {
                existingEdges.add(key);
                g.addEdge(u,v,rand.nextInt(100)+1);
            }
        }
        return g;
    }
}