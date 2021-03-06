package proof.data;

import proof.exception.InvalidGraphException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a graph by an adjacency matrix. Requires nodes to be indexed continuously.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class Graph {
  private final int[][] edgeIndices;
  private final int[] sources;
  private final int[] targets;
  private final int[] costs;
  private boolean immutable;
  private final int claimedLowerBound;

  public static final int NO_EDGE = -1;
  public static final int NO_EDGE_COST = Integer.MAX_VALUE;

  /**
   * Creates a new graph with the exact number of nodes and edges.
   *
   * @param numberOfNodes number of nodes
   * @param numberOfEdges number of edges
   * @param claimedLowerBound claimed minimum of realized crossings (to be proven)
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
   * @param source index of the first node
   * @param target index of the second node
   *
   * @return {@code true} iff the edge exist
   */
  public boolean edgeExists(int source, int target) {
    return nodeExists(source) && nodeExists(target) && edgeIndices[source][target] != NO_EDGE;
  }

  /**
   * Returns the id of any single edge.
   *
   * @param source index of the first node
   * @param target index of the second node
   *
   * @return the index of the edge
   * @throws InvalidGraphException if the edge does not exist
   */
  public int getEdgeId(int source, int target) throws InvalidGraphException {
    if (!edgeExists(source, target)) {
      throw new InvalidGraphException("Edge does not exist: (" + source + "," + target + ").");
    }

    return edgeIndices[source][target];
  }

  /**
   * Returns the cost of a single edge.
   *
   * @param source index of the first node
   * @param target index of the second node
   *
   * @return the cost
   * @throws InvalidGraphException if the edge does not exist
   */
  public int getEdgeCost(int source, int target) throws InvalidGraphException {
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
   * Creates a new edge. Will fail if the index is already in use or the edge already exists.
   *
   * @param edgeId edge index to be used
   * @param source index of the first node
   * @param target index of the second node
   * @param cost cost of the edge
   * @throws InvalidGraphException if the edge already exists
   */
  public void addEdge(int edgeId, int source, int target, int cost) throws InvalidGraphException {
    assertIsMutable();

    if (edgeExists(source, target)) {
      throw new IllegalArgumentException("Can not override existing edge.");
    }

    if (edgeExists(target, source)) {
      throw new IllegalArgumentException("Inverted edge already exists.");
    }

    if (edgeId < 0 || edgeId >= costs.length) {
      throw new IllegalArgumentException("Edge index out of bounds: " + edgeId);
    }

    if (!nodeExists(source)) {
      throw new IllegalArgumentException("Source index out of bounds: " + source);
    }

    if (!nodeExists(target)) {
      throw new IllegalArgumentException("Target index out of bounds: " + target);
    }

    if (costs[edgeId] != NO_EDGE_COST) {
      throw new IllegalArgumentException("Edge index already exists.");
    }

    if (cost < 1) {
      throw new IllegalArgumentException("Minimum weight of edges is 1.");
    }

    costs[edgeId] = cost;
    sources[edgeId] = source;
    targets[edgeId] = target;
    edgeIndices[source][target] = edgeId;
  }

  /**
   * Makes this graph immutable. Future calls of {@code addEdge} will throw an
   * {@link UnsupportedOperationException}.
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
      throw new InvalidGraphException("Can not make partially read graph immutable.");
    }
  }

  /**
   * Tests whether the graph is connected.
   *
   * @return true iff the graph is connected
   */
  public boolean isConnected() {
    boolean[] visited = new boolean[getNumberOfNodes()];

    for (int i = 0; i < visited.length; i++) {
      visited[i] = false;
    }

    List<Integer> nodes = new ArrayList<>(visited.length);
    nodes.add(0);
    visited[0] = true;

    while (!nodes.isEmpty()) {
      int v = nodes.remove(0);

      for (int i = 0; i < getNumberOfEdges(); i++) {
        int s = getEdgeSource(i);
        int t = getEdgeTarget(i);

        int w = s == v ? t : t == v ? s : -1;

        if (w != -1 && !visited[w]) {
          visited[w] = true;
          nodes.add(w);
        }
      }
    }

    boolean result = true;

    for (boolean element : visited) {
      result &= element;
    }

    return result;
  }

  /**
   * Returns true iff both edges have a common incident node.
   *
   * @param e1 index of the first edge
   * @param e2 index of the second edge
   * @return {@code true} iff the edges are adjacent
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
   *
   * @throws InvalidGraphException if the graph is immutable
   */
  private void assertIsMutable() throws InvalidGraphException {
    if (immutable) {
      throw new InvalidGraphException("Can not modify immutable graph.");
    }
  }

  /**
   * Checks whether a single node exists.
   *
   * @param node index of the supposed node
   * @return {@code true} iff the node exist
   */
  private boolean nodeExists(int node) {
    return node >= 0 && node < edgeIndices.length;
  }
}
