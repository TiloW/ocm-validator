package proof.data;

/**
 * Maintains a graph.
 * 
 * Represents a graph by an adjacency matrix. Requires nodes to be indexed continuously.
 * 
 * @author Tilo Wiedera
 *
 */
public class Graph {

  private int[][] edgeIndices;
  private double[] costs;

  public final static int NO_EDGE = -1;
  public final static double NO_EDGE_COST = Double.MAX_VALUE;

  /**
   * Creates a new graph with the exact number of nodes and edges.
   * 
   * @param numberOfNodes The number of nodes
   * 
   * @param numberOfEdges The number of edges
   */
  public Graph(int numberOfNodes, int numberOfEdges) {
    costs = new double[numberOfEdges];
    edgeIndices = new int[numberOfNodes][numberOfNodes];

    for (int i = 0; i < numberOfEdges; i++) {
      costs[i] = NO_EDGE_COST;
    }

    for (int i = 0; i < numberOfNodes; i++) {
      for (int ii = 0; ii < numberOfNodes; ii++) {
        edgeIndices[i][ii] = NO_EDGE;
      }
    }
  }

  /**
   * Checks whether a single edge exists (or its directed counterpart).
   * 
   * @param source The index of the first node
   * @param target The index of the second node
   * 
   * @return {@code true} iff the edge exist
   */
  public boolean edgeExists(int source, int target) {
    if (source < 0 || source >= edgeIndices.length) {
      throw new IllegalArgumentException("source node out of range: " + source);
    }
    if (target < 0 || target >= edgeIndices.length) {
      throw new IllegalArgumentException("target node out of range: " + source);
    }

    return edgeIndices[source][target] != NO_EDGE;
  }

  /**
   * Returns the id of any single edge.
   * 
   * @param source The index of the first node
   * @param target The index of the second node
   * 
   * @return The index of the edge
   */
  int getEdgeId(int source, int target) {
    if (!edgeExists(source, target)) {
      throw new IllegalArgumentException("Edge does not exist!");
    }

    return edgeIndices[source][target];
  }

  /**
   * Returns the cost of a single edge
   * 
   * @param source The index of the first node
   * @param target The index of the second node
   * 
   * @return The cost
   */
  public double getEdgeCost(int source, int target) {
    return costs[getEdgeId(source, target)];
  }

  /**
   * Creates a new edge.
   * 
   * Will fail if the index is already in use or the edge already exists.
   * 
   * @param edgeId The edge index to be used
   * @param source The index of the first node
   * @param target The index of the second node
   * @param cost The cost of the edge
   */
  public void addEdge(int edgeId, int source, int target, double cost) {
    if (edgeExists(source, target)) {
      throw new IllegalArgumentException("Can not override existing edge!");
    }

    if (edgeId < 0 || edgeId >= costs.length) {
      throw new IllegalArgumentException("Edge index out of bounds: " + edgeId);
    }

    if (costs[edgeId] != NO_EDGE_COST) {
      throw new IllegalArgumentException("Edge ID already exists!");
    }

    costs[edgeId] = cost;
    edgeIndices[source][target] = edgeIndices[target][source] = edgeId;
  }
}
