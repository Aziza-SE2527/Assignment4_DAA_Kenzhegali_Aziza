import graph.Graph;
import mst.MSTAlgorithms;
import connectivity.ConnectivityAlgorithms;
import shortestpath.Dijkstra;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("PART 1:CORRECTNESS & EDGE CASE TESTS");
        runTests();

        System.out.println("\nPART 2: EXPERIMENTAL BENCHMARKS (5 RUNS AVERAGE)");
        runBenchmarks();
    }

    private static void runTests()
    {
        Graph single= new Graph(1, false);
        System.out.println("1.Single Vertex MST Weight: "+MSTAlgorithms.kruskal(single).totalWeight);

        Graph disconnected= new Graph(4, false);
        disconnected.addEdge(0,1,10);
        disconnected.addEdge(2,3,20);
        System.out.println("2. Disconnected Graph Connected? "+MSTAlgorithms.kruskal(disconnected).isConnected);

        Graph triangle = new Graph(3, false);
        triangle.addEdge(0,1,5);
        triangle.addEdge(1,2,5);
        triangle.addEdge(0,2,5);
        int kWeight= MSTAlgorithms.kruskal(triangle).totalWeight;
        int pWeight=MSTAlgorithms.prim(triangle).totalWeight;
        System.out.println("3.Duplicate Weights MST->Kruskal: "+kWeight +", Prim: " + pWeight);

        Graph dag =new Graph(4,true);
        dag.addEdge(0,1, 1); dag.addEdge(1,2,1); dag.addEdge(0,3,1);
        System.out.println("4.DAG Topological Order: "+ConnectivityAlgorithms.topologicalSort(dag));

        Graph cyclic= new Graph(3,true);
        cyclic.addEdge(0,1,1); cyclic.addEdge(1,2,1); cyclic.addEdge(2,0,1);
        System.out.println("5.Cyclic Graph Topological Order (Should be null): " + ConnectivityAlgorithms.topologicalSort(cyclic));
        System.out.println("6.Cyclic Graph SCCs: " + ConnectivityAlgorithms.findSCCs(cyclic));

        Graph roadMap= new Graph(4, true);
        roadMap.addEdge(0,1,10);
        roadMap.addEdge(1,2,15);
        Dijkstra.Result dij= Dijkstra.shortestPath(roadMap, 0);
        System.out.println("7. Dijkstra Path 0-> 2: " + dij.getPath(2) + " (Cost: " + dij.dist[2] + ")");
        System.out.println("8. Dijkstra Unreachable Vertex 3 Distance: "+(dij.dist[3] == Integer.MAX_VALUE ? "UNREACHABLE" : dij.dist[3]));
    }

    private static void runBenchmarks()
    {
        int[][] configs =
                {
                {10, 30},     // small
                {100, 500},   // medium
                {1000, 5000}  // large
        };
        String[] names ={"Small","Medium","Large"};

        System.out.printf("%-8s | %-12s | %-15s | %-12s%n", "Size", "Algorithm", "Avg Time (ns)", "Avg Ops");
        System.out.println("------------------------------------------------------------");

        for (int i = 0;i<configs.length; i++)
        {
            int V= configs[i][0];
            int E =configs[i][1];
            String label= names[i];

            Graph undirected= Graph.generateConnectedUndirectedGraph(V, E, 42L);
            Graph dag= Graph.generateDirectedGraph(V, E, false, 42L);
            Graph directedCyclic= Graph.generateDirectedGraph(V, E, true, 42L);

            long kTime= 0, kOps = 0;
            for (int r =0;r<5;r++)
            {
                long start=System.nanoTime();
                MSTAlgorithms.MSTResult res= MSTAlgorithms.kruskal(undirected);
                kTime +=(System.nanoTime() - start);
                kOps +=res.operations;
            }
            System.out.printf("%-8s | %-12s | %-15d | %-12d%n", label, "Kruskal", kTime / 5, kOps / 5);

            long pTime= 0,pOps= 0;
            for (int r =0;r< 5;r++)
            {
                long start=System.nanoTime();
                MSTAlgorithms.MSTResult res=MSTAlgorithms.prim(undirected);
                pTime+=(System.nanoTime()-start);
                pOps+= res.operations;
            }
            System.out.printf("%-8s | %-12s | %-15d | %-12d%n", label, "Prim", pTime / 5, pOps / 5);

            long tTime= 0,tOps = 0;
            for (int r=0;r< 5;r++)
            {
                long start= System.nanoTime();
                ConnectivityAlgorithms.topologicalSort(dag);
                tTime+=(System.nanoTime() - start);
                tOps+=ConnectivityAlgorithms.lastOperationCount;
            }
            System.out.printf("%-8s | %-12s | %-15d | %-12d%n", label, "TopoSort", tTime / 5, tOps / 5);

            long sTime=0,sOps= 0;
            for (int r=0;r < 5; r++)
            {
                long start= System.nanoTime();
                ConnectivityAlgorithms.findSCCs(directedCyclic);
                sTime+= (System.nanoTime()-start);
                sOps+= ConnectivityAlgorithms.lastOperationCount;
            }
            System.out.printf("%-8s | %-12s | %-15d | %-12d%n", label, "SCC", sTime / 5, sOps / 5);

            long dTime=0,dOps=0;
            for (int r=0;r <5; r++)
            {
                long start= System.nanoTime();
                Dijkstra.Result res= Dijkstra.shortestPath(undirected,0);
                dTime+=(System.nanoTime() - start);
                dOps+=res.operations;
            }
            System.out.printf("%-8s | %-12s | %-15d | %-12d%n", label, "Dijkstra", dTime / 5, dOps / 5);
            System.out.println("------------------------------------------------------------");
        }
    }
}