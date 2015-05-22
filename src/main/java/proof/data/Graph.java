package proof.data;

import proof.exception.InvalidGraphException;

/**
 * Maintains a graph.
 *
 * Represents a graph by an adjacency matrix. Requires nodes to be indexed continuously.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class Graph {
  private final int[][] edgeIndices;
  private final int[] sources;
  private final int[] targets;
  private final int[] costs;
  private boolean immutable;
  private final int claimedLowerBound;

  public final static int NO_EDGE = -1;
  public final static int NO_EDGE_COST = Integer.MAX_VALUE;

  /**
   * Creates a new graph with the exact number of nodes and edges.
   *
   * @param numberOfNodes The number of nodes
   * @param numberOfEdges The number of edges
   * @param claimedLowerBound The claimed minimum of realized crossings
   */
  public Graph(int numberOfNodes, int numberOfEdges, int claimedLowerBound) {
    immutable = false;
    this.claimedLowerBound = claimedLowerBound;

    sources = new int[numberOfEdges];
    targets = new int[numberOfEdges];
    costs = new int[numberOfEdges];
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

  public int getClaimedLowerBound() {
    return claimedLowerBound;
  }

  public int getNumberOfNodes() {
    return edgeIndices.length;
  }

  public int getNumberOfEdges() {
    return costs.length;
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
    return source >= 0 && source < edgeIndices.length && target >= 0 && target < edgeIndices.length
        && edgeIndices[source][target] != NO_EDGE;
  }

  /**
   * Returns the id of any single edge.
   *
   * @param source The index of the first node
   * @param target The index of the second node
   *
   * @return The index of the edge
   */
  public int getEdgeId(int source, int target) {
    if (!edgeExists(source, target)) {
      throw new IllegalArgumentException("Edge does not exist: (" + source + "," + target + ")");
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
  public int getEdgeCost(int source, int target) {
    return getEdgeCost(getEdgeId(source, target));
  }

  public int getEdgeCost(int id) {
    return costs[id];
  }

  public int getEdgeSource(int id) {
    return sources[id];
  }

  public int getEdgeTarget(int id) {
    return targets[id];
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
  public void addEdge(int edgeId, int source, int target, int cost) {
    assertIsMutable();

    if (edgeExists(source, target)) {
      throw new IllegalArgumentException("Can not override existing edge!");
    }

    if (edgeExists(target, source)) {
      throw new IllegalArgumentException("Inverted edge already exists!");
    }

    if (edgeId < 0 || edgeId >= costs.length) {
      throw new IllegalArgumentException("Edge index out of bounds: " + edgeId);
    }

    if (source < 0 || source >= edgeIndices.length) {
      throw new IllegalArgumentException("Source index out of bounds: " + source);
    }

    if (target < 0 || target >= edgeIndices.length) {
      throw new IllegalArgumentException("Target index out of bounds: " + target);
    }

    if (costs[edgeId] != NO_EDGE_COST) {
      throw new IllegalArgumentException("Edge ID already exists!");
    }

    costs[edgeId] = cost;
    sources[edgeId] = source;
    targets[edgeId] = target;
    edgeIndices[source][target] = edgeId;
  }

  /**
   * Makes this graph immutable.
   *
   * Future calls of {@code addEdge} will throw an {@link UnsupportedOperationException}.
   *
   * @throws InvalidGraphException Iff not all edges have been set
   */
  public void makeImmutable() throws InvalidGraphException {
    immutable = true;

    // validate all edges have been inserted
    int counter = 0;
    for (int[] edgeIndice : edgeIndices) {
      for (int ii = 0; ii < edgeIndices.length; ii++) {
        if (edgeIndice[ii] != NO_EDGE) {
          counter++;
        }
      }
    }

    if (counter != costs.length) {
      throw new InvalidGraphException("Can not make incomplete graph immutable");
    }
  }

  /**
   * Returns true iff both edges have a common incident node.
   *
   * @param e1 The index of the first edge
   * @param e2 The index of the second edge
   * @return true iff the edges are adjacent
   */
  public boolean areEdgesAdjacent(int e1, int e2) {
    int s1 = getEdgeSource(e1);
    int t1 = getEdgeTarget(e1);
    int s2 = getEdgeSource(e2);
    int t2 = getEdgeTarget(e2);

    return s1 == s2 || s1 == t2 || t1 == s2 || t1 == t2;
  }

  /**
   * Called before modifying the graph.
   */
  private void assertIsMutable() {
    if (immutable) {
      throw new UnsupportedOperationException("Can not modify immutable graph");
    }
  }
}
