//IRIA PARADA 
//C223058634
// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Heap {
    private int[] a; // heap array
    private int[] hPos; // hPos[h[k]] == k
    private int[] dist; // dist[v] = priority of v

    private int N; // heap size

    // The heap constructor gets passed from the Graph:
    // 1. maximum heap size
    // 2. reference to the dist[] array
    // 3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos) {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public void siftUp(int k) {
        int v = a[k];
        a[0] = 0;
        dist[0] = 0;

        while (dist[v] < dist[a[k / 2]]) {
            a[k] = a[k / 2];
            hPos[a[k]] = k;
            k = k / 2;
        }

        a[k] = v;
        hPos[v] = k;
    }

    public void siftDown(int k) {
        int v, j;

        v = a[k];
        j = 2 * k;

        while (j <= N) {
            if (j < N && dist[a[j]] > dist[a[j + 1]])
                j++;

            if (dist[v] <= dist[a[j]])
                break;

            a[k] = a[j];
            hPos[a[k]] = k;
            k = j;
            j = 2 * k;
        }

        a[k] = v;
        hPos[v] = k;
    }

    public void insert(int x) {
        a[++N] = x;
        siftUp(N);
    }

    public int remove() {
        int v = a[1];
        hPos[v] = 0; // v is no longer in heap
        a[N + 1] = 0; // put null node into empty spot

        a[1] = a[N--];
        siftDown(1);

        return v;
    }

}

class Graph {
    class Node {
        public int vert;
        public int wgt;
        public Node next;
    }

    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    private int V, E;
    private Node[] adj;
    private Node z;
    private int[] mst;

    // used for traversing graph
    private int[] visited;
    private int id;

    // default constructor
    public Graph(String graphFile) throws IOException {
        int u, v;
        int e, wgt;
        Node t;

        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = " +"; // multiple whitespace as delimiter
        String line = reader.readLine();
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);

        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);

        // create sentinel node
        z = new Node();
        z.next = z;

        // create adjacency lists, initialised to sentinel node z
        adj = new Node[V + 1];
        for (v = 1; v <= V; ++v)
            adj[v] = z;

        // read the edges
        System.out.println("Reading edges from text file");
        for (e = 1; e <= E; ++e) {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]);
            wgt = Integer.parseInt(parts[2]);

            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));

            // write code to put edge into adjacency list
            Node newNode = new Node(); // create a new node
            newNode.vert = v; // set vertex to visited
            newNode.wgt = wgt; // set weight into wgt
            newNode.next = adj[u]; // set next to adj[u]
            adj[u] = newNode; // set adj[u](next) to new node

            newNode = new Node(); // create new node
            newNode.vert = u; // set vertex ti visited
            newNode.wgt = wgt; // set weight into wgt
            newNode.next = adj[v];// set next to adj[u]
            adj[v] = newNode;// set adj[u](next) to new node
        }
    }

    // convert vertex into char for pretty printing
    private char toChar(int u) {
        return (char) (u + 64);
    }

    // method to display the graph representation
    public void display() {
        int v;
        Node n;

        for (v = 1; v <= V; ++v) {
            System.out.print("\nadj[" + toChar(v) + "] ->");
            for (n = adj[v]; n != z; n = n.next)
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");
        }
        System.out.println("");
    }

    // Depth-first traversal using recursion
    public void DF(int s) {
        visited = new int[V + 1];
        id = 0;
        DFVisit(s);
    }

    private void DFVisit(int u) {
        visited[u] = ++id; // set visited to id
        System.out.print(toChar(u) + " "); // print vertex

        for (Node v = adj[u]; v != z; v = v.next) { // visit all adj verices not visited
            if (visited[v.vert] == 0) {
                DFVisit(v.vert);
            }
        }
    }

    // Breadth-first traversal using a queue
    public void breadthFirst(int s) {
        visited = new int[V + 1];
        id = 0;
        Queue<Integer> queue = new LinkedList<>(); // create a queue
        queue.add(s);
        visited[s] = ++id;

        while (!queue.isEmpty()) { // while queue is not empty
            int u = queue.poll(); // remove first element from queue
            System.out.print(toChar(u) + " "); // print vertex

            for (Node v = adj[u]; v != z; v = v.next) { // visit all adj verices (not visited)
                if (visited[v.vert] == 0) {// if not visited
                    visited[v.vert] = ++id; // set visited to id
                    queue.add(v.vert);// add to the queue
                }
            }
        }
    }

    public void MST_Prim(int s) {
        int v, u;
        int wgt, wgt_sum = 0;
        int[] dist, parent, hPos;
        Node t;

        dist = new int[V + 1]; // make the array (distance)
        parent = new int[V + 1]; // array for parent
        hPos = new int[V + 1];// array for heap position

        for (v = 1; v <= V; ++v) { // initialize the arrays
            dist[v] = Integer.MAX_VALUE;
            parent[v] = 0;
            hPos[v] = 0;
        }

        dist[s] = 0;

        Heap h = new Heap(V, dist, hPos);
        h.insert(s);

        while (!h.isEmpty()) { // while heap is not empty
            v = h.remove();// remove vertex with minimun distance from source
            System.out.println("Adding vertex " + toChar(v) + " to MST"); // print verte

            wgt_sum += dist[v]; // add distance to weight sum
            dist[v] = -dist[v]; // mark v as in the tree

            for (Node n = adj[v]; n != z; n = n.next) { // for all adj verices not visited
                u = n.vert;// set u to visited
                wgt = n.wgt; // set weigth to wgt
                if (wgt < dist[u]) { // if weight is less than distance
                    dist[u] = wgt; // set distance to weight
                    parent[u] = v; // set parent to visited
                    if (hPos[u] == 0) // if its not in hep insstet it
                        h.insert(u);
                    else
                        h.siftUp(hPos[u]);
                }
            }
        }

        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");
        showMST(parent, dist, hPos);
    }

    // display the mst
    public void showMST(int[] parent, int[] dist, int[] heap) {
        System.out.println("\n\nMinimum Spanning tree parent array is:");
        for (int v = 1; v <= V; ++v) {
            if (parent[v] != 0) { // Exclude the root vertex
                System.out.print(toChar(v) + " -> " + toChar(parent[v]) + ", Distance: ");
                if (dist[v] < 0) { // If the distance is negative, it means the vertex is in the tree
                    System.out.print(-dist[v]);
                } else {
                    System.out.print(dist[v]);
                }
                System.out.println(", Heap position: " + heap[v]);
            }
        }
        System.out.println("");
    }

    public void SPT_Dijkstra(int s) {
        int[] dist = new int[V + 1]; // distance array
        int[] parent = new int[V + 1]; // parent array
        int[] hPos = new int[V + 1]; // heap position array
        boolean[] inTree = new boolean[V + 1]; // indicates if a vertex is in the tree
        int id = 0; // id for marking visited vertices

        for (int v = 1; v <= V; v++) {
            dist[v] = Integer.MAX_VALUE; // initialize distances to infinity
            parent[v] = -1; // initialize parent array
            hPos[v] = 0; // initialize heap position array
            inTree[v] = false; // initialize inTree array
        }

        dist[s] = 0; // set distance of source vertex to 0

        Heap pq = new Heap(V, dist, hPos); //  queue (heap) initially empty
        pq.insert(s); // insert source vertex into the queue heap

        while (!pq.isEmpty()) {
            int v = pq.remove(); // remove vertex with minimum distance from  source
            inTree[v] = true; // mark vertex as part of the tree

            System.out.println("Adding vertex " + toChar(v) + " to Shortest Path Tree");

            // update diatance  and parent for adj vertices
            for (Node uNode = adj[v]; uNode != z; uNode = uNode.next) {
                int u = uNode.vert;
                int wgt = uNode.wgt;

                if (!inTree[u] && dist[v] != Integer.MAX_VALUE && dist[v] + wgt < dist[u]) {//if veretx is not in tree/ dist is not in infity /distance is less than current dist 
                    dist[u] = dist[v] + wgt; // update distance
                    parent[u] = v; // update parent
                    // display parent and dist and heap
                    System.out.println("Parent of " + toChar(u) + " is " + toChar(parent[u]) + ", Distance: " + dist[u]
                            + ", Heap position: " + hPos[u]);
                    if (hPos[u] == 0)
                        pq.insert(u); // insert vertex into the priority queue if not already in it
                    else
                        pq.siftUp(hPos[u]); 
                }
            }
        }

        // print SPT
        System.out.println("\nShortest Path Tree:");
        for (int i = 1; i <= V; i++) {
            if (parent[i] != -1) {
                System.out.println("Parent of " + toChar(i) + " is " + toChar(parent[i]) + ", Distance: " + dist[i]);
            }
        }
    }

}

public class GraphLists {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // prompt the user for the name of the text file containing the graph
        System.out.print("Enter the name of the text file containing the graph: ");
        String fname = scanner.nextLine();

        //prompt the user for the starting vertex
        System.out.print("Enter the starting vertex: ");
        int s = scanner.nextInt();

        Graph g = new Graph(fname);

        g.display();

        // Depth-first traversal
        System.out.println("\nDepth-First Traversal:");
        g.DF(s);

        // Breadth-first traversal
        System.out.println("\n\nBreadth-First Traversal:");
        g.breadthFirst(s);

        System.out.println("\n\nPrim's Algorithm for MST:");
        g.MST_Prim(s);

        System.out.println("\n\nDijkstra:");
        g.SPT_Dijkstra(s);
    }
}
