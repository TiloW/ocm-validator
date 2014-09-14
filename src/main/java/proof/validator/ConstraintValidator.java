package proof.validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.reader.CrossingReader;
import proof.exception.InvalidConstraintException;
import proof.exception.InvalidPathException;
import proof.validator.base.ObjectValidator;

/**
 * Validates a single Kuratwoski Constraint.
 *
 * The only type of constraints included in the log are Kuratwoski constraints. These constraints
 * will be validated by proving the respective paths do form a Kuratowski-subdivision. Valid
 * Kuratowski subdivisions in this context are the K3,3 and the K5.
 *
 * @author Tilo Wiedera
 *
 */
public class ConstraintValidator implements ObjectValidator {

  private final static CrossingReader CROSSING_READER = new CrossingReader();

  private final Map<CrossingIndex, Boolean> fixedVariables;
  private final int numberOfSegments;
  private final Graph graph;

  /**
   * Creates a new constraint validator.
   *
   * @param fixedVariables The branching variables
   * @param numberOfSegments The maximum number of segments per edge (i.e. the optimal solution)
   * @param graph The original graph we are working on (without any crossings)
   */
  public ConstraintValidator(Map<CrossingIndex, Boolean> fixedVariables, int numberOfSegments,
      Graph graph) {
    this.fixedVariables = fixedVariables;
    this.numberOfSegments = numberOfSegments;
    this.graph = graph;
  }

  @Override
  public void validate(JSONObject object) throws InvalidConstraintException {

    final Set<CrossingIndex> vars = new HashSet<CrossingIndex>();
    for (CrossingIndex cross : fixedVariables.keySet()) {
      if (fixedVariables.get(cross)) {
        vars.add(cross);
      }
    }

    // realize required variables
    // override branching variables if required (should never happen but still)
    JSONArray crossings = object.getJSONArray("requiredCrossings");

    for (int i = 0; i < crossings.length(); i++) {
      vars.add(CROSSING_READER.read(crossings.getJSONArray(i)));
    }

    JSONArray paths = object.getJSONArray("paths");

    PathValidator validator = createPathValidator(vars, numberOfSegments, graph);
    for (int i = 0; i < paths.length(); i++) {
      try {
        validator.validate(paths.getJSONArray(i));
      } catch (InvalidPathException e) {
        throw (InvalidConstraintException) new InvalidConstraintException(
            "Invalid path encountered").initCause(e);
      }
    }

    String constraintType = object.getString("type");

    if (constraintType.equals("K33")) {
      validateK33(paths);
    } else if (constraintType.equals("K5")) {
      validateK5(paths);
    } else {
      throw new InvalidConstraintException("Invalid type of Kuratowski constraint: "
          + constraintType);
    }
  }

  protected PathValidator createPathValidator(Set<CrossingIndex> vars, int numberOfSegments,
      Graph graph) {
    return new PathValidator(vars, numberOfSegments, graph);
  }

  /**
   * Asserts that all five nodes are connected to one another.
   *
   * @param paths The ten paths
   * @throws InvalidConstraintException If one of the paths has no defined source or target
   */
  private void validateK5(JSONArray paths) throws InvalidConstraintException {
    if (paths.length() != 10) {
      throw new InvalidConstraintException("Supposed K5 has an invalid number of paths: "
          + paths.length());
    }

    Map<Integer, Integer> nodes = collectNodes(paths);

    if (nodes.size() != 5) {
      throw new InvalidConstraintException("Supposed K5 has an invalid number of nodes: "
          + nodes.size());
    }

    // find all paths
    boolean[][] foundPaths = new boolean[5][5];
    for (int i = 0; i < 5; i++) {
      for (int ii = 0; ii < 5; ii++)
        foundPaths[i][ii] = false;
    }

    for (int i = 0; i < paths.length(); i++) {
      JSONArray path = paths.getJSONArray(i);
      int u = nodes.get(getSource(path));
      int v = nodes.get(getTarget(path));

      foundPaths[u][v] = foundPaths[v][u] = true;
    }

    // assert each required path is present
    for (int i = 0; i < 5; i++) {
      for (int ii = 0; ii < 5; ii++) {
        if (i == ii) {
          if (foundPaths[i][ii]) {
            throw new InvalidConstraintException("Self loop in supposed Kuratowski path");
          }
        } else {
          if (!foundPaths[i][ii]) {
            throw new InvalidConstraintException("Missing Kuratowski path in supposed K5");
          }
        }
      }
    }
  }

  /**
   * Asserts a valid K33, i.e. a bipartite Graph with three nodes in each of the two subsets.
   *
   * @param paths The 9 paths
   * @throws InvalidConstraintException If one of the paths has no defined source or target
   */
  private void validateK33(JSONArray paths) throws InvalidConstraintException {
    if (paths.length() != 9) {
      throw new InvalidConstraintException("Supposed K33 has an invalid number of paths: "
          + paths.length());
    }

    Map<Integer, Integer> nodes = collectNodes(paths);

    if (nodes.size() != 6) {
      throw new InvalidConstraintException("Supposed K33 has an invalid number of nodes: "
          + nodes.size());
    }

    // classify nodes (2-coloring)
    boolean[] color = new boolean[6];
    for (int i = 0; i < 6; i++) {
      color[i] = false;
    }

    for (int i = 0; i < paths.length(); i++) {
      JSONArray path = paths.getJSONArray(i);
      int u = nodes.get(getSource(path));
      int v = nodes.get(getTarget(path));

      if (u == 0) {
        color[v] = true;
      } else if (v == 0) {
        color[u] = true;
      }
    }

    // validate coloring
    int counter = 0;
    for (int i = 0; i < 6; i++) {
      if (color[i]) {
        counter++;
      }
    }

    if (counter != 3) {
      throw new InvalidConstraintException("Supposed K33 has invalid 2-coloring");
    }

    // validate each node is connected to exactly three nodes (of different color)
    for (int node = 0; node < 6; node++) {
      int edgeCounter = 0;

      for (int i = 0; i < paths.length(); i++) {
        JSONArray path = paths.getJSONArray(i);
        int v = nodes.get(getSource(path));
        int w = nodes.get(getTarget(path));

        if (color[v] == color[w]) {
          throw new InvalidConstraintException(
              "Connected nodes in supposed K33 have the same color");
        }

        if (node == v || node == w) {
          edgeCounter++;
        }
      }

      if (edgeCounter != 3) {
        throw new InvalidConstraintException(
            "Node of supposed K33 is not connected to the right number of nodes");
      }
    }
  }

  /**
   * Collects all the nodes present as source or target nodes in the paths. Will enumerate those
   * nodes starting at zero. Returns a mapping of original node indices to their normalized indices.
   *
   * @param paths The paths to be searched for source and target nodes
   * @return The resulting node mapping
   */
  private Map<Integer, Integer> collectNodes(JSONArray paths) {
    Map<Integer, Integer> result = new HashMap<Integer, Integer>();
    int counter = 0;

    for (int i = 0; i < paths.length(); i++) {
      int nodeIndex = getSource(paths.getJSONArray(i));
      if (!result.containsKey(nodeIndex)) {
        result.put(nodeIndex, counter++);
      }
      nodeIndex = getTarget(paths.getJSONArray(i));
      if (!result.containsKey(nodeIndex)) {
        result.put(nodeIndex, counter++);
      }
    }

    return result;
  }

  /**
   * Returns the first node of a valid path.
   */
  private int getSource(JSONArray path) {
    return path.getJSONObject(0).getInt("from");
  }

  /**
   * Returns the last node of a valid path.
   */
  private int getTarget(JSONArray path) {
    return path.getJSONObject(path.length() - 1).getInt("to");
  }
}
