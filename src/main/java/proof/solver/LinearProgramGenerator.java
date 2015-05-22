package proof.solver;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.Path;
import proof.data.SegmentIndex;
import proof.data.reader.CrossingReader;
import proof.data.reader.PathReader;
import proof.exception.InvalidProofException;
import proof.util.Config;
import proof.util.Statistics;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class for generating the linear program used to prove the lower bound. The program is returned in
 * CPLEX LP file format.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class LinearProgramGenerator {
  private final Graph graph;
  private final int[] expansions;
  private final Statistics stats = new Statistics();
  private final Set<CrossingIndex> variables = new HashSet<CrossingIndex>();

  /**
   * Initializes a new generator.
   *
   * @param graph The graph to work with.
   */
  public LinearProgramGenerator(Graph graph) {
    this.graph = graph;
    expansions = new int[graph.getNumberOfEdges()];
  }

  /**
   * Returns a linear program based on the expanded graph and all given Kuratowski subdivisions. The
   * program is returned in CPLEX LP file format.
   *
   * @param fixedVariables The currently fixed branching variables
   * @param leaf A JSON object containing all relevant information for this leaf
   * @return The generated linear program in CPLEX LP file format
   * @throws InvalidProofException if the number of expansions on any edge is negative
   */
  public String createLinearProgram(Map<CrossingIndex, Boolean> fixedVariables,
      JSONObject leaf) throws InvalidProofException {
    final StringBuilder result = new StringBuilder();

    JSONArray jsonConstraints = leaf.getJSONArray("constraints");
    JSONObject jsonExpansions = leaf.getJSONObject("expansions");

    stats.clear();
    stats.put("fixed variables", fixedVariables.size());
    stats.put("Kuratowski constraints", jsonConstraints.length());

    // parse expansions (i.e. the variables to be generated)
    for (int e = 0; e < graph.getNumberOfEdges(); e++) {
      expansions[e] = jsonExpansions.getInt(String.valueOf(e));

      if (expansions[e] < 0) {
        throw new InvalidProofException(
            "The amount of additional segments must not be negative.");
      }
    }

    result.append("Minimize\nobj:\n");
    result.append(generateObjective());

    stats.put("variables", variables.size());

    result.append("\nSubject To");

    // note that simplicity is not required on the first segment
    result.append("\n\\ Simplicity Constraints");

    for (int e = 0; e < graph.getNumberOfEdges(); e++) {
      for (int s = 1; s <= expansions[e]; s++) {
        result.append("\n" + sumVariables(e, s) + " <= 1");
        stats.increase("simplicity constraints");
      }
    }

    result.append("\n\\ Ordering Constraints");

    for (int e = 0; e < graph.getNumberOfEdges(); e++) {
      for (int s = 1; s < expansions[e]; s++) {
        result.append("\n" + sumVariables(e, s) + sumVariables(e, s + 1, true) + " >= 0");
        stats.increase("ordering constraints");
      }
    }

    for (int i = 0; i < jsonConstraints.length(); i++) {
      result.append("\n\\ Kuratowski Constraint " + i + "\n");
      result.append(generateKuratowski(jsonConstraints.getJSONObject(i)));
    }

    result.append("\nBounds");
    result.append(generateBounds(fixedVariables));
    result.append("\nEnd");

    for (String line : stats.format()) {
      Config.get().logger.print("    " + line);
    }

    return result.toString();
  }

  /**
   * Generates and returns a single Kuratowski constraint.
   *
   * @param constraint A JSON structure containing all paths and required crossings
   * @return A CPLEX LP file format compliant description of the constraint
   */
  private String generateKuratowski(JSONObject constraint) {
    final StringBuilder result = new StringBuilder();
    Set<CrossingIndex> requiredCrossings = new HashSet<>();
    CrossingReader crossReader = new CrossingReader(graph);

    // collect required crossings
    for (int i = 0; i < constraint.getJSONArray("requiredCrossings").length(); i++) {
      CrossingIndex crossing =
          crossReader.read(constraint.getJSONArray("requiredCrossings").getJSONArray(i));
      requiredCrossings.add(crossing);
    }

    boolean first = true;
    JSONArray paths = constraint.getJSONArray("paths");
    PathReader pathReader = new PathReader(graph, requiredCrossings);
    Set<CrossingIndex> feasibleCrossings = new HashSet<>();

    // collect feasible crossings for resolving the Kuratowski subdivision
    for (int i = 0; i < paths.length(); i++) {
      JSONArray path1 = paths.getJSONArray(i);

      for (int k = i + 1; k < paths.length(); k++) {
        JSONArray path2 = paths.getJSONArray(k);
        Path p1 = pathReader.read(path1);
        Path p2 = pathReader.read(path2);

        // crossing adjacent paths do not resolve the subdivision
        if (!p1.isAdjacentTo(p2)) {
          feasibleCrossings.addAll(collectFeasibleCrossings(path1, path2));
        }
      }
    }

    for (CrossingIndex crossing : feasibleCrossings) {
      result.append((first ? "" : " + ") + createVarName(crossing));
      first = false;
    }

    for (CrossingIndex crossing : requiredCrossings) {
      result.append(" - " + createVarName(crossing));
    }

    result.append(" >= " + (1 - requiredCrossings.size()));

    return result.toString();
  }

  /**
   * Collects all feasible {@link #variables} over all segments of two paths.
   *
   * @param path1 The first path
   * @param path2 The second path
   * @return the set of feasible crossings
   */
  private Set<CrossingIndex> collectFeasibleCrossings(JSONArray path1, JSONArray path2) {
    Set<CrossingIndex> result = new HashSet<>();

    for (int i = 0; i < path1.length(); i++) {
      for (int k = 0; k < path2.length(); k++) {
        JSONObject segRange1 = path1.getJSONObject(i);
        JSONObject segRange2 = path2.getJSONObject(k);

        int edge1 =
            graph.getEdgeId(segRange1.getJSONObject("edge").getInt("source"), segRange1
                .getJSONObject("edge").getInt("target"));

        int edge2 =
            graph.getEdgeId(segRange2.getJSONObject("edge").getInt("source"), segRange2
                .getJSONObject("edge").getInt("target"));

        int startSeg1 = Math.max(0, segRange1.getInt("start"));
        int startSeg2 = Math.max(0, segRange2.getInt("start"));

        int endSeg1 = Math.min(expansions[edge1], segRange1.getInt("end"));
        int endSeg2 = Math.min(expansions[edge2], segRange2.getInt("end"));

        // since the first segment might participate in multiple paths this condition could
        // be false
        if (!graph.areEdgesAdjacent(edge1, edge2)) {
          for (int s1 = startSeg1; s1 <= endSeg1; s1++) {
            for (int s2 = startSeg2; s2 <= endSeg2; s2++) {
              result.add(new CrossingIndex(edge1, s1, edge2, s2));
            }
          }
        }
      }
    }

    return result;
  }

  /**
   * Generates the lower and upper bound for all variables. All variables are continuous on the
   * interval {@code [0,1]}. However, some variables might be fixed to either {@code 1} or {@code 0}
   * due to branching.
   *
   * @param fixedVariables The currently fixed branching variables
   * @return A CPLEX LP file format compliant description of all bounds
   */
  private String generateBounds(Map<CrossingIndex, Boolean> fixedVariables) {
    final StringBuilder result = new StringBuilder();

    for (CrossingIndex var : variables) {
      int min = 0;
      int max = 1;

      if (fixedVariables.containsKey(var)) {
        if (fixedVariables.get(var)) {
          min = 1;
        } else {
          max = 0;
        }
      }

      result.append("\n " + min + " <= " + createVarName(var) + " <= " + max);
    }

    return result.toString();
  }

  /**
   * Generates the objective function to be minimized. Clears and collects all feasible
   * {@link #variables}. Since adjacent edges will never cross in any optimal drawing of a graph
   * they are not considered as feasible variables. The cost of any crossing equals the product of
   * the weight of both involved edges. Weighted edges commonly occur in pre-processed graphs.
   *
   * @return the objective function in CPLEX LP file format
   */
  private String generateObjective() {
    final StringBuilder result = new StringBuilder();
    boolean first = true;
    variables.clear();

    for (int e1 = 0; e1 < graph.getNumberOfEdges(); e1++) {
      for (int e2 = e1 + 1; e2 < graph.getNumberOfEdges(); e2++) {
        if (!graph.areEdgesAdjacent(e1, e2)) {
          int cost = graph.getEdgeCost(e1) * graph.getEdgeCost(e2);
          for (int s1 = 0; s1 <= expansions[e1]; s1++) {
            for (int s2 = 0; s2 <= expansions[e2]; s2++) {
              String weight = cost == 1 ? "" : (Integer.toString(cost) + " ");
              CrossingIndex crossing = new CrossingIndex(e1, s1, e2, s2);
              String prefix = first ? " " : " + ";
              result.append(prefix + weight + createVarName(crossing));
              variables.add(crossing);
              stats.increase("variables");
              first = false;
            }
          }
        }
      }
    }

    return result.toString();
  }

  /**
   * Returns the positive sum over all feasible variables (i.e. crossings) including the given
   * segment.
   *
   * @param edge The edge
   * @param segment The segment
   * @return string a CPLEX LP file format compliant representation of the sum over all feasible
   *         variables
   */
  private String sumVariables(int edge, int segment) {
    return sumVariables(edge, segment, false);
  }

  /**
   * Returns the sum over all feasible variables (i.e. crossings) including the given segment.
   *
   * @param edge The edge
   * @param segment The segment
   * @param substract Whether to return the negative sum
   * @return string a CPLEX LP file format compliant representation of the sum over all feasible
   *         variables
   */
  private String sumVariables(int edge, int segment, boolean substract) {
    final StringBuilder result = new StringBuilder();
    boolean first = true;

    for (int e = 0; e < graph.getNumberOfEdges(); e++) {
      if (!graph.areEdgesAdjacent(edge, e)) {
        for (int i = 0; i <= expansions[e]; i++) {
          if (substract) {
            result.append(" - ");
          } else if (!first) {
            result.append(" + ");
          }
          result.append(createVarName(new CrossingIndex(edge, segment, e, i)));
          first = false;
        }
      }
    }

    return result.toString();
  }

  /**
   * Returns the name of the variable associated with the crossing. Note that the crossing ensures
   * the uniqueness of each name.
   *
   * @param crossing The crossing which should be named
   * @return A label for the crossing
   */
  private String createVarName(CrossingIndex crossing) {
    SegmentIndex s1 = crossing.segments[0];
    SegmentIndex s2 = crossing.segments[1];
    return "x_e" + s1.edge + "_s" + s1.segment + "_e" + s2.edge + "_s" + s2.segment;
  }
}
