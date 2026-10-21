# Assignment 3: Smart City Infrastructure and Route Planning

## 1. Overview
In this project, I modeled a city's infrastructure network as a graph:
- **Vertices (V):** City districts or locations.
- **Edges (E):** Roads or connections between districts.
- **Weights:** Road construction costs (for MST) or travel distances (for Dijkstra).

To represent the graph in Java, I used an **Adjacency List** (`List<List<Edge>>`) along with an **Edge List** (`List<Edge>`). The adjacency list is fast for algorithms that explore neighbors (like Prim's, Dijkstra, SCC, and Topological Sort), while the edge list makes it easy to sort all edges by weight for Kruskal's algorithm.

---

## 2. Implementation & Design Decisions (Quick Defense Notes)

### Task 1: Disjoint Set Union (DSU) & Minimum Spanning Tree (MST)
- **DSU (`src/dsu/DSU.java`):** Implemented with two optimizations:
  1. **Path Compression (in `find`):** Makes nodes point directly to the root when searching, which flattens the tree.
  2. **Union by Size (in `union`):** Attaches the smaller tree under the larger tree so the tree height stays very small.
- **Kruskal's Algorithm (`src/mst/MSTAlgorithms.java`):** Sorts all edges from cheapest to most expensive and adds them one by one using DSU to avoid cycles.
- **Prim's Algorithm (`src/mst/MSTAlgorithms.java`):** Starts at vertex `0` and uses a `PriorityQueue` to always pick the cheapest edge connecting a visited district to an unvisited one.
- **Connectivity check:** Both MST algorithms count how many edges/vertices were connected. If we connected `V` vertices (`V - 1` edges), the graph is connected; otherwise, it is disconnected.

### Task 2: Topological Sort & Strongly Connected Components (SCC)
- **Topological Sort (`src/connectivity/ConnectivityAlgorithms.java`):** Implemented using **Kahn's Algorithm** (in-degrees + Queue).
  - *How it detects cycles:* It counts the incoming edges (`inDegree`) for each vertex and processes vertices with `inDegree == 0`. If the final sorted list has fewer than `V` vertices, it means some vertices were stuck in a cycle, so the method returns `null`.
- **SCC (`src/connectivity/ConnectivityAlgorithms.java`):** Implemented using **Kosaraju's Algorithm**:
  1. Run DFS on the original graph and push vertices onto a stack by finish time.
  2. Reverse all edges in the graph.
  3. Pop vertices from the stack and run DFS on the reversed graph—each DFS run collects one Strongly Connected Component.

### Task 3: Shortest Paths (Dijkstra's Algorithm)
- **Dijkstra (`src/shortestpath/Dijkstra.java`):** Uses a `PriorityQueue` and a `dist[]` array initialized to `Integer.MAX_VALUE` (which also handles unreachable vertices). A `parent[]` array is used to reconstruct the exact path from the source to any district.
- **Why Dijkstra fails with negative edge weights:** Dijkstra is a greedy algorithm. Once it picks the closest vertex from the PriorityQueue, it assumes the shortest path to that vertex is already found because adding positive edges can only make a path longer. If there is a negative edge later, it could make a previously explored path shorter, which breaks Dijkstra's logic.

---

## 3. Complexity Analysis
All complexities below assume an **Adjacency List** representation and a **Binary Heap (`PriorityQueue`)**:

| Algorithm | Time Complexity | Space Complexity | Why? (Defense Explanation) |
| :--- | :--- | :--- | :--- |
| **DSU (`find` & `union`)** | $O(\alpha(V)) \approx O(1)$ | $O(V)$ | Path compression + union by size keep the tree almost completely flat. `parent` and `size` arrays take $O(V)$ memory. |
| **Kruskal's MST** | $O(E \log E)$ | $O(V + E)$ | Sorting all $E$ edges takes $O(E \log E)$, which dominates the fast DSU checks. |
| **Prim's MST** | $O(E \log V)$ | $O(V + E)$ | We push at most $E$ edges into the PriorityQueue, and each heap operation takes $O(\log V)$. |
| **Topological Sort** | $O(V + E)$ | $O(V)$ | We check every vertex and edge once to calculate in-degrees and process the queue. |
| **Kosaraju's SCC** | $O(V + E)$ | $O(V + E)$ | Does two standard DFS traversals ($O(V+E)$) and creates one reversed graph ($O(V+E)$ space). |
| **Dijkstra's Algorithm** | $O((V + E) \log V)$ | $O(V)$ | Every vertex and edge is checked, and updating distances in the PriorityQueue takes $O(\log V)$. |

---

## 4. Experimental Setup
I tested the algorithms on three graph sizes using a fixed random seed (`42L`) so the results are reproducible:
- **Small:** $V = 10, E = 30$
- **Medium:** $V = 100, E = 500$
- **Large:** $V = 1000, E = 5000$

**How graphs were generated (`Graph.java`):**
1. **Undirected Connected Graphs (for MST & Dijkstra):** First, I connected every vertex `i` to a random previous vertex `0..i-1` to guarantee the graph is connected ($V-1$ edges). Then I added random unique edges until reaching $E$ edges.
2. **Directed Graphs (for TopoSort & SCC):**
   - **DAG (no cycles):** Only added edges from smaller vertex IDs to larger vertex IDs ($u \to v$ where $u < v$), making cycles impossible.
   - **Cyclic Directed Graph:** Added random directed edges $u \to v$ in any direction, allowing natural cycles to form.

Each algorithm was run **5 times** per graph size, and I recorded the average time using `System.nanoTime()` and average operation counts.

---

## 5. Results

### Benchmark Table (Average of 5 Runs)

| Graph Size | V | E | Algorithm | Avg Time (ns) | Avg Time (µs) | Avg Operations |
| :--- | :---: | :---: | :--- | :---: | :---: | :---: |
| **Small** | 10 | 30 | Kruskal | 18,400 | 18.4 | 76 |
| **Small** | 10 | 30 | Prim | 12,100 | 12.1 | 69 |
| **Small** | 10 | 30 | TopoSort (DAG) | 6,200 | 6.2 | 80 |
| **Small** | 10 | 30 | Kosaraju SCC | 14,500 | 14.5 | 110 |
| **Small** | 10 | 30 | Dijkstra | 11,800 | 11.8 | 70 |
| **Medium** | 100 | 500 | Kruskal | 245,000 | 245.0 | 1,185 |
| **Medium** | 100 | 500 | Prim | 165,000 | 165.0 | 1,092 |
| **Medium** | 100 | 500 | TopoSort (DAG) | 58,000 | 58.0 | 1,200 |
| **Medium** | 100 | 500 | Kosaraju SCC | 140,000 | 140.0 | 1,700 |
| **Medium** | 100 | 500 | Dijkstra | 152,000 | 152.0 | 1,100 |
| **Large** | 1,000 | 5,000 | Kruskal | 2,890,000 | 2,890.0 | 11,940 |
| **Large** | 1,000 | 5,000 | Prim | 1,920,000 | 1,920.0 | 10,850 |
| **Large** | 1,000 | 5,000 | TopoSort (DAG) | 610,000 | 610.0 | 12,000 |
| **Large** | 1,000 | 5,000 | Kosaraju SCC | 1,480,000 | 1,480.0 | 17,000 |
| **Large** | 1,000 | 5,000 | Dijkstra | 1,750,000 | 1,750.0 | 11,000 |

### Plots
![Execution Time vs Vertices](results/plots/execution_time_vs_vertices.png)
![Execution Time vs Edges](results/plots/execution_time_vs_edges.png)

---

## 6. Comparison and Discussion
- **Kruskal vs. Prim:** Both algorithms always found the exact same total MST weight. Prim was slightly faster on Medium and Large graphs because Kruskal has to sort the entire list of $E$ edges at the start, while Prim only processes edges of visited nodes using the PriorityQueue.
- **DSU Performance:** Because of path compression and union by size, DSU operations stayed very small (roughly proportional to $E$) even when $V=1000$.
- **SCC & Topological Sort:** Both grew linearly ($O(V + E)$) as the graph increased $10\times$. SCC took a bit more than twice as long as Topological Sort because Kosaraju runs DFS twice and builds a reversed graph in memory.
- **Dijkstra:** Scaled almost identically to Prim's algorithm since both rely on a binary heap (`PriorityQueue`) over adjacency lists.

---

## 7. Testing
In `Main.java` (`runTests()`), I tested all required edge cases:
1. **Single vertex graph ($V=1$):** MST weight is `0` and `isConnected` is `true`.
2. **Disconnected graph:** Tested on 4 vertices with 2 separate edges; both MST algorithms correctly return `isConnected = false` (weight `-1`).
3. **Duplicate edge weights & Multiple valid MSTs:** Tested on a triangle graph where all 3 edges have weight `5`. Both Kruskal and Prim return the same valid MST weight of `10`.
4. **DAG vs. Cyclic Graph:** Topological sort returns a valid order on the DAG and returns `null` when given a 3-node cycle (`0 -> 1 -> 2 -> 0`).
5. **SCC:** Verified that the cyclic graph groups `0, 1, 2` into one SCC and that every vertex belongs to exactly one SCC.
6. **Unreachable vertices in Dijkstra:** Tested with an isolated vertex (`3`), confirming its distance stays `Integer.MAX_VALUE` and prints `UNREACHABLE`.

---

## 8. Conclusion
- **Kruskal's vs. Prim's:** Both solve MST for infrastructure cost minimization; Kruskal is simpler to implement when edges are already in a list, while Prim is slightly faster when using adjacency lists on denser graphs.
- **Topological Sort & SCC:** Best for analyzing one-way roads and service dependencies—Topological Sort works when there are no cycles (DAGs), while SCC groups mutually reachable districts when cycles exist.
- **Dijkstra:** Ideal for finding the fastest route between city districts as long as all road distances/weights are non-negative.

---

## How to Run
```bash
javac -d out src/graph/*.java src/dsu/*.java src/mst/*.java src/connectivity/*.java src/shortestpath/*.java src/Main.java
java -cp out Main
```
