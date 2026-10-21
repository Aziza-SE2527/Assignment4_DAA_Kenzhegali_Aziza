package mst;

import graph.Edge;
import graph.Graph;
import dsu.DSU;
import java.util.*;

public class MSTAlgorithms
{

    public static class MSTResult
    {
        public int totalWeight;
        public boolean isConnected;
        public long operations;

        public MSTResult(int totalWeight,boolean isConnected,long operations)
        {
            this.totalWeight= totalWeight;
            this.isConnected= isConnected;
            this.operations= operations;
        }
    }

    public static MSTResult kruskal(Graph g)
    {
        if (g.V <= 1) return new MSTResult(0, true, 1);

        List<Edge> edges= new ArrayList<>(g.allEdges);
        Collections.sort(edges);

        DSU dsu= new DSU(g.V);
        int totalWeight= 0;
        int edgesUsed= 0;
        long ops= edges.size();

        for (Edge e : edges)
        {
            ops++;
            if (dsu.union(e.from, e.to))
            {
                totalWeight+= e.weight;
                edgesUsed++;
                if (edgesUsed== g.V - 1) break;
            }
        }

        ops+= dsu.operationsCount;
        boolean connected= (edgesUsed== g.V - 1);
        return new MSTResult(connected ? totalWeight : -1,connected, ops);
    }

    public static MSTResult prim(Graph g)
    {
        if(g.V == 0) return new MSTResult(0, true, 0);
        if(g.V == 1) return new MSTResult(0, true, 1);

        boolean[] visited= new boolean[g.V];
        PriorityQueue<Edge> pq=new PriorityQueue<>();
        int totalWeight= 0;
        int visitedCount= 0;
        long ops= 0;

        visited[0]= true;
        visitedCount++;
        for (Edge e : g.adj.get(0))
        {
            pq.add(e);
            ops++;
        }

        while (!pq.isEmpty() && visitedCount < g.V)
        {
            Edge cheapest= pq.poll();
            ops++;

            if (visited[cheapest.to]) continue;

            visited[cheapest.to]= true;
            totalWeight += cheapest.weight;
            visitedCount++;

            for (Edge nextEdge : g.adj.get(cheapest.to))
            {
                ops++;
                if(!visited[nextEdge.to])
                {
                    pq.add(nextEdge);
                }
            }
        }

        boolean connected=(visitedCount==g.V);
        return new MSTResult(connected ? totalWeight : -1, connected,ops);
    }
}