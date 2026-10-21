package connectivity;

import graph.Edge;
import graph.Graph;
import java.util.*;

public class ConnectivityAlgorithms
{
    public static long lastOperationCount = 0;

    public static List<Integer> topologicalSort(Graph g)
    {
        lastOperationCount= 0;
        int[] inDegree= new int[g.V];

        for (int u= 0;u< g.V;u++)
        {
            for(Edge e : g.adj.get(u))
            {
                inDegree[e.to]++;
                lastOperationCount++;
            }
        }

        Queue<Integer> q= new LinkedList<>();
        for (int i = 0; i < g.V; i++)
        {
            lastOperationCount++;
            if (inDegree[i]== 0)
            {
                q.add(i);
            }
        }

        List<Integer> order= new ArrayList<>();
        while(!q.isEmpty())
        {
            int u= q.poll();
            order.add(u);
            lastOperationCount++;

            for(Edge e : g.adj.get(u))
            {
                lastOperationCount++;
                inDegree[e.to]--;
                if (inDegree[e.to]== 0)
                {
                    q.add(e.to);
                }
            }
        }

        if (order.size() != g.V)
        {
            return null;
        }
        return order;
    }

    public static List<List<Integer>> findSCCs(Graph g)
    {
        lastOperationCount= 0;
        Stack<Integer> stack= new Stack<>();
        boolean[] visited= new boolean[g.V];

        for (int i= 0;i < g.V;i++)
        {
            if (!visited[i])
            {
                fillOrder(g, i, visited, stack);
            }
        }

        Graph reversed= new Graph(g.V, true);
        for (int u = 0;u < g.V;u++) {
            for (Edge e : g.adj.get(u))
            {
                reversed.addEdge(e.to, e.from, e.weight);
                lastOperationCount++;
            }
        }

        Arrays.fill(visited, false);
        List<List<Integer>> sccs = new ArrayList<>();

        while (!stack.isEmpty())
        {
            int v= stack.pop();
            if (!visited[v])
            {
                List<Integer> component= new ArrayList<>();
                dfsCollect(reversed,v,visited,component);
                sccs.add(component);
            }
        }
        return sccs;
    }

    private static void fillOrder(Graph g, int u, boolean[] visited, Stack<Integer> stack)
    {
        visited[u]= true;
        lastOperationCount++;
        for(Edge e : g.adj.get(u))
        {
            lastOperationCount++;
            if(!visited[e.to])
            {
                fillOrder(g, e.to, visited, stack);
            }
        }
        stack.push(u);
    }

    private static void dfsCollect(Graph g,int u,boolean[] visited,List<Integer> component)
    {
        visited[u]= true;
        component.add(u);
        lastOperationCount++;
        for(Edge e : g.adj.get(u))
        {
            lastOperationCount++;
            if(!visited[e.to])
            {
                dfsCollect(g, e.to, visited, component);
            }
        }
    }
}