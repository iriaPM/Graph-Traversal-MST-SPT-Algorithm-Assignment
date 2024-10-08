//IRIA PARADA 
//C223058634
// Kruskal's Minimum Spanning Tree Algorithm
// Union-find implemented using disjoint set trees without compression

import java.io.*;

class Edge {
    public int u, v, wgt;

    public Edge() {
        u = 0;
        v = 0;
        wgt = 0;
    }

    public Edge(int x, int y, int w) {
        u = x;
        v = y;
        wgt = w;
    }

    public void show() {
        System.out.print("Edge " + toChar(u) + "--" + wgt + "--" + toChar(v) + "\n");
    }

    // convert vertex into char for pretty printing
    private char toChar(int u) {
        return (char) (u + 64);
    }
}

class Heap {
    private int[] h;
    int N, Nmax;
    Edge[] edge;

    // Bottom up heap construc
    public Heap(int _N, Edge[] _edge) {
        int i;
        Nmax = N = _N;
        h = new int[N + 1];
        edge = _edge;

        // initially just fill heap array with
        // indices of edge[] array.
        for (i = 0; i <= N; ++i)
            h[i] = i;

        // Then convert h[] into a heap
        // from the bottom up.
        for (i = N / 2; i > 0; --i) {
            siftDown(i);//sfit down for each element in heap
        }

    }

    private void siftDown(int k) {
        int e, j;

        e = h[k];
        while (k <= N / 2) {

            j = 2 * k;// left child
            if (j < N && edge[h[j]].wgt > edge[h[j + 1]].wgt) { // check if right child is smaller
                ++j;// make j point to right child
            }
            if (edge[e].wgt <= edge[h[j]].wgt) { // check if parent is smaller than child
                break;// if yes, leave loop
            }
            // if not swap parent with child
            h[k] = h[j];// if not swap parent with child
            k = j;
        }
        h[k] = e;
    }

    public int remove() {
        h[0] = h[1];
        h[1] = h[N--];
        siftDown(1);
        return h[0];
    }
}

/****************************************************
 *
 * UnionFind partition to support union-find operations
 * Implemented simply using Discrete Set Trees
 *
 *****************************************************/

class UnionFindSets {
    private int[] treeParent;
    private int N;

    public UnionFindSets(int V) {
        N = V;
        treeParent = new int[V + 1];
        for (int i = 1; i <= V; i++) {
            treeParent[i] = i;
        }
    }

    public int findSet(int vertex) {
        if (vertex != treeParent[vertex]) {
            treeParent[vertex] = findSet(treeParent[vertex]);
        }
        return treeParent[vertex];
    }

    public void union(int set1, int set2) {
        // missing
        treeParent[set2] = set1;// set parent of set2 to set1
    }

    public void showTrees() {
        int i;
        for (i = 1; i <= N; ++i)
            System.out.print(toChar(i) + "->" + toChar(treeParent[i]) + "  ");
        System.out.print("\n");
    }

    public void showSets() {
        int u, root;
        int[] shown = new int[N + 1];
        for (u = 1; u <= N; ++u) {
            root = findSet(u);
            if (shown[root] != 1) {
                showSet(root);
                shown[root] = 1;
            }
        }
        System.out.print("\n");
    }

    private void showSet(int root) {
        int v;
        System.out.print("Set{");
        for (v = 1; v <= N; ++v)
            if (findSet(v) == root)
                System.out.print(toChar(v) + " ");
        System.out.print("}  ");

    }

    private char toChar(int u) {
        return (char) (u + 64);
    }
}

class Graph {
    private int V, E;
    private Edge[] edge;
    private Edge[] mst;

    public Graph(String graphFile) throws IOException {
        int u, v;
        int w, e;

        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = " +"; // multiple whitespace as delimiter
        String line = reader.readLine();
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);

        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);

        // create edge array
        edge = new Edge[E + 1];

        // read the edges
        System.out.println("Reading edges from text file");
        for (e = 1; e <= E; ++e) {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]);
            w = Integer.parseInt(parts[2]);

            System.out.println("Edge " + toChar(u) + "--(" + w + ")--" + toChar(v));

            // create Edge object
            edge[e] = new Edge(u, v, w); // add edge ro array

            System.out.println("Added edge: " + edge[e].u + " " + edge[e].v + " " + edge[e].wgt);//display edge
        }

    }

    /**********************************************************
     *
     * Kruskal's minimum spanning tree algorithm
     *
     **********************************************************/
    public Edge[] MST_Kruskal() {
        int ei = 0;
        int i = 0;
        mst = new Edge[V - 1];

        // create edge array to store MST
        // Initially it has no edges.
        mst = new Edge[V - 1];

        Heap h = new Heap(E, edge);
        UnionFindSets partition = new UnionFindSets(V);

        // create partition of singleton sets for the vertices
        while (i < V - 1 && ei < E) { // while mst is not full, there are still edges to check
            int edgeIndex = h.remove(); // remove edge with smallest weight 
            Edge currentEdge = edge[edgeIndex]; //get current edge
            int uSet = partition.findSet(currentEdge.u); //find set of unvisited vertex 
            int vSet = partition.findSet(currentEdge.v); // find set of visited vertex
            if (uSet != vSet) { //if they are not in the same set 
                partition.union(uSet, vSet); // join the sts 
                mst[ei++] = currentEdge; // add edge to mst 
                i++;
            }
        }

        return mst;
    }

    // convert vertex into char for pretty printing
    private char toChar(int u) {
        return (char) (u + 64);
    }

    // dispay the mst
    public void showMST(Edge[] mst) {
        System.out.print("\nMinimum spanning tree build from following edges:\n");
        for (int e = 0; e < mst.length; ++e) {
            mst[e].show();
        }
        System.out.println();
    }

} // end of Graph class

// test code// test code
class KruskalTrees {
    public static void main(String[] args) throws IOException {
        String fname;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("\nInput name of file with graph definition: ");
        fname = reader.readLine(); // Reading user input for the file name

        Graph g = new Graph(fname);

        Edge[] minimumSpanningTree = g.MST_Kruskal(); // Store the result in a variable
        g.showMST(minimumSpanningTree); // Pass the result to showMST() method
    }
}
