package shortestpath;

import graph.Edge;
import graph.Graph;
import java.util.*;

public class Dijkstra
{

    public static class Result
    {
        public int[] dist;
        public int[] parent;
        public long operations;

        public Result(int[] dist,int[] parent,long operations)
        {
            this.dist= dist;
            this.parent= parent;
            this.operations= operations;
        }

        public List<Integer> getPath(int target)
        {
            if(dist[target]== Integer.MAX_VALUE) return Collections.emptyList();
            List<Integer> path= new ArrayList<>();
            for (int at= target;at != -1;at = parent[at])
            {
                path.add(at);
            }
            Collections.reverse(path);
            return path;
        }
    }

    public static Result shortestPath(Graph g, int source)
    {
        int[] dist= new int[g.V];
        int[] parent= new int[g.V];
        Arrays.fill(dist,Integer.MAX_VALUE);
        Arrays.fill(parent,-1);

        dist[source]= 0;
        long ops= 0;

        PriorityQueue<int[]> pq= new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{source, 0});

        while(!pq.isEmpty())
        {
            int[] current= pq.poll();
            int u= current[0];
            int d= current[1];
            ops++;

            if(d > dist[u]) continue;

            for (Edge e : g.adj.get(u))
            {
                ops++;
                if(dist[u]+ e.weight< dist[e.to])
                {
                    dist[e.to]= dist[u] + e.weight;
                    parent[e.to]= u;
                    pq.add(new int[]{e.to,dist[e.to]});
                }
            }
        }

        return new Result(dist, parent, ops);
    }
}