package proof.data;

import java.util.Map;

import proof.exception.InvalidGraphException;

/**
 * Maintains a graph.
 *
 * Represents a graph by an adjacency matrix. Requires nodes to be indexed continuously.
 *
 * @author Tilo Wiedera
 *
 */
public class Graph {

  private final int[][] edgeIndices;
  private final double[] costs;
  private boolean immutable;

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
    immutable = false;

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
    if (immutable) {
      throw new UnsupportedOperationException("Can not modify immutable graph");
    }

    if (edgeExists(source, target)) {
      throw new IllegalArgumentException("Can not override existing edge!");
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
    edgeIndices[source][target] = edgeIndices[target][source] = edgeId;
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
    for (int i = 0; i < edgeIndices.length; i++) {
      for (int ii = 0; ii < edgeIndices.length; ii++) {
        if (edgeIndices[i][ii] != NO_EDGE) {
          counter++;
        }
      }
    }

    if (counter != 2 * costs.length) {
      throw new InvalidGraphException("Can not make incomplete graph immutable");
    }
  }

  /**
   * Validates a variable assignment.
   *
   * Checks there are no crossings that require more than {@numberOfSegments}
   * crossings per edge.
   *
   * @param vars The variable assigment
   * @param numberOfSegments The maximum number of crossings per edge
   */
  public void validateVariables(Map<CrossingIndex, Boolean> vars, int numberOfSegments) {
    // TODO
  }
}
