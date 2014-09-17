package proof.validator;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.SegmentIndex;
import proof.exception.InvalidPathException;
import proof.validator.base.ArrayValidator;

/**
 * Validates a single path.
 * 
 * Asserts that the given series of segments constitutes a valid path. This means, only set
 * crossings can be used and the start and end node must exists and not be the same.
 * 
 * @author Tilo Wiedera
 *
 */
public class PathValidator implements ArrayValidator {

  private final Set<CrossingIndex> variables;
  private int numberOfSegments;
  private Graph graph;

  /**
   * Creates a new path validator.
   * 
   * @param variables The set of variables that are assumed to be realized (i.e. the crossings that
   *        can be used for establishing paths).
   */
  public PathValidator(Set<CrossingIndex> variables, int numberOfSegments, Graph graph) {
    this.variables = variables;
    this.numberOfSegments = numberOfSegments;
    this.graph = graph;
  }

  @Override
  public void validate(JSONArray path) throws InvalidPathException {
    if (path.length() == 0) {
      throw new InvalidPathException("Path must not be empty");
    }

    // path has to start and end at a normal node
    // since crossings nodes have degree four and thus can never be part of a Kuratowski graph
    if (getSource(path) == getTarget(path)) {
      throw new InvalidPathException("Start and end node of a Path must not be equal");
    }

    for (int i = 0; i < path.length(); i++) {
      JSONObject section = path.getJSONObject(i);
      JSONObject edge = section.getJSONObject("edge");

      int source = edge.getInt("source");
      int target = edge.getInt("target");
      int endId = section.getInt("end");

      if (!graph.edgeExists(source, target)) {
        throw new InvalidPathException("Required edge does not exist");
      }

      int edgeId = graph.getEdgeId(source, target);

      // if there is at least one more section, is it truly reachable?
      if (i < path.length() - 1) {
        JSONObject nextSection = path.getJSONObject(i + 1);
        JSONObject nextEdge = nextSection.getJSONObject("edge");
        int nextSource = nextEdge.getInt("source");

        // either by node
        if (endId == numberOfSegments) {
          if (target != nextSource) {
            throw new InvalidPathException("Disconnected Path is invalid");
          }
        }
        // or by crossing
        else {
          SegmentIndex s1 = new SegmentIndex(edgeId, endId);
          int nextTarget = nextEdge.getInt("target");

          if (!graph.edgeExists(nextSource, nextTarget)) {
            throw new InvalidPathException("Required edge does not exist");
          }

          SegmentIndex s2 =
              new SegmentIndex(graph.getEdgeId(nextSource, nextTarget), nextSection.getInt("start"));
          CrossingIndex cross = new CrossingIndex(s1, s2);
          if (!variables.contains(cross)) {
            throw new InvalidPathException("Required crossing is missing: " + cross);
          }
        }
      }
    }
  }

  /**
   * Return the first node of the {@code path}.
   * 
   * @param path The path to be inspected
   * @return The source node
   * @throws InvalidPathException If there is no source node (i.e. the path starts at a crossing)
   */
  private int getSource(JSONArray path) throws InvalidPathException {
    JSONObject section = path.getJSONObject(0);

    if (section.getInt("start") != -1) {
      throw new InvalidPathException("Path has no source node");
    }

    return section.getJSONObject("edge").getInt("source");
  }

  /**
   * Return the last node of the {@code path}.
   * 
   * @param path The path to be inspected
   * @return The target node
   * @throws InvalidPathException If there is no target node (i.e. the path ends in a crossing)
   */
  private int getTarget(JSONArray path) throws InvalidPathException {
    JSONObject section = path.getJSONObject(path.length() - 1);

    if (section.getInt("end") != numberOfSegments) {
      throw new InvalidPathException("Path has no target node");
    }

    return section.getJSONObject("edge").getInt("target");
  }
}
