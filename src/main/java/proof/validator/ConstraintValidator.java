package proof.validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.Path;
import proof.data.reader.CrossingReader;
import proof.data.reader.PathReader;
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

  private final CrossingReader crossingReader;

  private final Map<CrossingIndex, Boolean> fixedVariables;
  private final Graph graph;

  /**
   * Creates a new constraint validator.
   *
   * @param graph The original graph we are working on (without any crossings)
   * @param fixedVariables The branching variables
   */
  public ConstraintValidator(Graph graph, Map<CrossingIndex, Boolean> fixedVariables) {
    this.fixedVariables = fixedVariables;
    this.graph = graph;
    crossingReader = new CrossingReader(graph);
  }

  /**
   * Validates a single Kuratwoski constraint.
   *
   * Investigates all paths and asserts the constraint represents a valid K3,3 or K5.
   */
  @Override
  public void validate(JSONObject object) throws InvalidConstraintException {

    // A constraint is considered to be relevant as long as it does not require any crossings
    // that are currently not realizable (because of the branching)
    // only relevant constraints are validated
    boolean relevant = true;

    final Set<CrossingIndex> vars = new HashSet<CrossingIndex>();
    for (CrossingIndex cross : fixedVariables.keySet()) {
      if (fixedVariables.get(cross)) {
        vars.add(cross);
      }
    }

    // realize required variables
    // override branching variables if required (should never happen but still)
    JSONArray crossings = object.getJSONArray("requiredCrossings");

    for (int i = 0; relevant && i < crossings.length(); i++) {
      CrossingIndex newCrossing = crossingReader.read(crossings.getJSONArray(i));

      for (CrossingIndex cross : vars) {
        relevant &= !newCrossing.conflicting(cross);
      }

      vars.add(newCrossing);
    }

    if (relevant) {
      PathReader reader = new PathReader(graph, vars);

      JSONArray jsonPaths = object.getJSONArray("paths");
      Path paths[] = new Path[jsonPaths.length()];

      for (int i = 0; i < paths.length; i++) {
        try {
          // System.out.println("    Path " + i);
          paths[i] = reader.read(jsonPaths.getJSONArray(i));
        } catch (InvalidPathException e) {
          throw (InvalidConstraintException) new InvalidConstraintException(
              "Invalid path encountered").initCause(e);
        }
      }

      // paths must be disjoint
      for (int i = 0; i < paths.length; i++) {
        for (int j = i + 1; j < paths.length; j++) {
          if (!paths[i].isDisjoint(paths[j])) {
            throw new InvalidConstraintException("Paths are not disjoint (" + i + "," + j + ").");
          }
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
  }

  /**
   * Asserts that all five nodes are connected to one another.
   *
   * @param paths The ten paths
   * @throws InvalidConstraintException If one of the paths has no defined source or target
   */
  private void validateK5(Path[] paths) throws InvalidConstraintException {
    if (paths.length != 10) {
      throw new InvalidConstraintException("Supposed K5 has an invalid number of paths: "
          + paths.length);
    }

    Map<Object, Integer> endpoints = collectEndpoints(paths);

    if (endpoints.size() != 5) {
      throw new InvalidConstraintException("Supposed K5 has an invalid number of nodes: "
          + endpoints.size());
    }

    // find all paths
    boolean[][] foundPaths = new boolean[5][5];
    for (int i = 0; i < 5; i++) {
      for (int ii = 0; ii < 5; ii++)
        foundPaths[i][ii] = false;
    }

    for (int i = 0; i < paths.length; i++) {
      Path path = paths[i];
      int u = endpoints.get(path.getSource());
      int v = endpoints.get(path.getTarget());

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
  private void validateK33(Path[] paths) throws InvalidConstraintException {
    if (paths.length != 9) {
      throw new InvalidConstraintException("Supposed K33 has an invalid number of paths: "
          + paths.length);
    }

    Map<Object, Integer> endpoints = collectEndpoints(paths);

    if (endpoints.size() != 6) {
      throw new InvalidConstraintException("Supposed K33 has an invalid number of nodes: "
          + endpoints.size());
    }

    // classify nodes (2-coloring)
    boolean[] color = new boolean[6];
    for (int i = 0; i < 6; i++) {
      color[i] = false;
    }

    for (int i = 0; i < paths.length; i++) {
      Path path = paths[i];
      int u = endpoints.get(path.getSource());
      int v = endpoints.get(path.getTarget());

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

      for (int i = 0; i < paths.length; i++) {
        Path path = paths[i];
        int v = endpoints.get(path.getSource());
        int w = endpoints.get(path.getTarget());

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

  private Map<Object, Integer> collectEndpoints(Path paths[]) {
    Map<Object, Integer> result = new HashMap<Object, Integer>();
    int counter = 0;

    for (int i = 0; i < paths.length; i++) {
      Object source = paths[i].getSource();
      Object target = paths[i].getTarget();

      if (!result.containsKey(source)) {
        result.put(source, counter++);
      }

      if (!result.containsKey(target)) {
        result.put(target, counter++);
      }
    }

    return result;
  }
}
