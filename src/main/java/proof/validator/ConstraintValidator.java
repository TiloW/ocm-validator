package proof.validator;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.Path;
import proof.data.reader.CrossingReader;
import proof.data.reader.PathReader;
import proof.exception.ExceptionHelper;
import proof.exception.InvalidConstraintException;
import proof.exception.InvalidPathException;
import proof.exception.ReaderException;
import proof.util.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validates a single Kuratwoski Constraint. The only type of constraints included in the log are
 * Kuratwoski constraints. These constraints will be validated by proving the respective paths do
 * form a Kuratowski-subdivision. Valid Kuratowski subdivisions in this context are the K3,3 and the
 * K5.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class ConstraintValidator implements Validator<JSONObject> {
  private final CrossingReader crossingReader;
  private final Graph graph;

  /**
   * Creates a new constraint validator.
   *
   * @param graph The original graph we are working on (without any crossings)
   */
  public ConstraintValidator(Graph graph) {
    this.graph = graph;
    crossingReader = new CrossingReader(graph);
  }

  /**
   * Validates a single Kuratwoski constraint. Investigates all paths and asserts the constraint
   * represents a valid K3,3 or K5.
   */
  @Override
  public void validate(JSONObject object) throws InvalidConstraintException {
    // A constraint is considered to be relevant as long as it does not require any crossings
    // that are currently not realizable (because of the branching)
    // only relevant constraints are validated
    boolean relevant = true;
    final Set<CrossingIndex> requiredCrossings = new HashSet<CrossingIndex>();

    // realize required variables
    // override branching variables if required (should never happen but still)
    JSONArray crossings = object.getJSONArray("requiredCrossings");

    for (int i = 0; relevant && i < crossings.length(); i++) {
      try {
        requiredCrossings.add(crossingReader.read(crossings.getJSONArray(i)));
      } catch (ReaderException e) {
        throw ExceptionHelper.wrap(e, new InvalidConstraintException(
            "Encountered infeasible crossing."));
      }
    }

    PathReader reader = new PathReader(graph, requiredCrossings);
    JSONArray jsonPaths = object.getJSONArray("paths");
    Path[] paths = new Path[jsonPaths.length()];

    for (int i = 0; i < paths.length; i++) {
      try {
        Config.get().logger.print("    path " + i);
        paths[i] = reader.read(jsonPaths.getJSONArray(i));
      } catch (InvalidPathException e) {
        throw ExceptionHelper.wrap(e, new InvalidConstraintException("Path " + i + " is invalid."));
      }
    }

    // paths must be disjoint
    for (int i = 0; i < paths.length; i++) {
      for (int j = i + 1; j < paths.length; j++) {
        if (!paths[i].isDisjointTo(paths[j])) {
          throw new InvalidConstraintException("Paths are not disjoint (" + i + "," + j + ").");
        }
      }
    }

    String constraintType = object.getString("type");

    if ("K33".equals(constraintType)) {
      validateK33(paths);
    } else if ("K5".equals(constraintType)) {
      validateK5(paths);
    } else {
      throw new InvalidConstraintException("Invalid type of Kuratowski constraint: "
          + constraintType);
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

    Map<Object, Integer> nodes = collectNodes(paths);

    if (nodes.size() != 5) {
      throw new InvalidConstraintException("Supposed K5 has an invalid number of nodes: "
          + nodes.size());
    }

    // find all paths
    boolean[][] foundPaths = new boolean[5][5];
    for (int i = 0; i < 5; i++) {
      for (int ii = 0; ii < 5; ii++) {
        foundPaths[i][ii] = false;
      }
    }

    for (Path path : paths) {
      int u = nodes.get(path.getSource());
      int v = nodes.get(path.getTarget());

      foundPaths[u][v] = foundPaths[v][u] = true;
    }

    // assert each required path is present
    for (int i = 0; i < 5; i++) {
      for (int ii = 0; ii < 5; ii++) {
        if (i == ii) {
          if (foundPaths[i][ii]) {
            throw new InvalidConstraintException("Self loop in supposed Kuratowski path.");
          }
        } else {
          if (!foundPaths[i][ii]) {
            throw new InvalidConstraintException("Missing Kuratowski path in supposed K5.");
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

    Map<Object, Integer> nodes = collectNodes(paths);

    if (nodes.size() != 6) {
      throw new InvalidConstraintException("Supposed K33 has an invalid number of nodes ("
          + nodes.size() + "): " + nodes);
    }

    // classify nodes (2-coloring)
    boolean[] color = new boolean[6];
    for (int i = 0; i < 6; i++) {
      color[i] = false;
    }

    for (Path path : paths) {
      int u = nodes.get(path.getSource());
      int v = nodes.get(path.getTarget());

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
      throw new InvalidConstraintException("Supposed K33 has invalid 2-coloring.");
    }

    // validate each node is connected to exactly three nodes (of different color)
    for (int node = 0; node < 6; node++) {
      int edgeCounter = 0;

      for (Path path : paths) {
        int v = nodes.get(path.getSource());
        int w = nodes.get(path.getTarget());

        if (color[v] == color[w]) {
          throw new InvalidConstraintException(
              "Connected nodes in supposed K33 have the same color.");
        }

        if (node == v || node == w) {
          edgeCounter++;
        }
      }

      if (edgeCounter != 3) {
        throw new InvalidConstraintException(
            "Node of supposed K33 is not connected to the right number of nodes.");
      }
    }
  }

  /**
   * Collects all nodes and crossings which represent a start or end of any of the given paths.
   *
   * @param paths The paths to be investigated
   * @return a mapping of of encountered nodes to a continuous index
   */
  private Map<Object, Integer> collectNodes(Path[] paths) {
    Map<Object, Integer> result = new HashMap<Object, Integer>();
    int counter = 0;

    for (Path path : paths) {
      Object source = path.getSource();
      Object target = path.getTarget();

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
