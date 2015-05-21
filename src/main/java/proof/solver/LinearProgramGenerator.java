package proof.solver;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

/**
 * Class for generating the linear program used to prove the lower bound.
 *
 * @author Tilo Wiedera
 */
public class LinearProgramGenerator {
  private final Graph graph;

  public LinearProgramGenerator(Graph graph) {
    this.graph = graph;
  }

  public String createLinearProgram(Map<CrossingIndex, Boolean> fixedVariables, JSONObject leaf)
      throws InvalidProofException {
    JSONArray constraints = leaf.getJSONArray("constraints");
    JSONObject expansions = leaf.getJSONObject("expansions");

    for (int e = 0; e < graph.getNumberOfEdges(); e++) {
      if (expansions.getInt(String.valueOf(e)) < 0) {
        throw new InvalidProofException("The amount of additional segments must not be negative.");
      }
    }

    StringBuilder output = new StringBuilder();
    StringBuilder boundsOuput = new StringBuilder();
    boolean first = true;
    int numberOfVariables = 0;

    output.append("Minimize\nobj:");

    for (int e1 = 0; e1 < graph.getNumberOfEdges(); e1++) {
      for (int e2 = e1 + 1; e2 < graph.getNumberOfEdges(); e2++) {
        if (!graph.areEdgesAdjacent(e1, e2)) {
          int cost = graph.getEdgeCost(e1) * graph.getEdgeCost(e2);
          for (int i = 0; i <= expansions.getInt(String.valueOf(e1)); i++) {
            for (int j = 0; j <= expansions.getInt(String.valueOf(e2)); j++) {
              String weight = cost == 1 ? "" : (Integer.toString(cost) + " ");
              String varName = createVarName(new CrossingIndex(e1, i, e2, j));
              String prefix = first ? " " : " + ";
              output.append(prefix + weight + varName);
              boundsOuput.append("\n0 <= " + varName + " <= 1");
              numberOfVariables++;
              first = false;
            }
          }
        }
      }
    }

    printNumber("variables", numberOfVariables);
    Set<CrossingIndex> realizedCrossings = new HashSet<>();

    output.append("\nSubject To");
    output.append("\n\\ Fixed Branching Variables");

    for (Map.Entry<CrossingIndex, Boolean> var : fixedVariables.entrySet()) {
      int value = var.getValue() ? 1 : 0;

      output.append("\n" + createVarName(var.getKey()) + " = " + value);

      if (var.getValue()) {
        realizedCrossings.add(var.getKey());
      }
    }

    printNumber("fixed variables", fixedVariables.size());
    int numberOfSimplicityConstraints = 0;

    // note that simplicity is not required on the first segment
    output.append("\n\\ Simplicity Constraints");

    for (int e1 = 0; e1 < graph.getNumberOfEdges(); e1++) {
      for (int i = 1; i <= expansions.getInt(String.valueOf(e1)); i++) {
        first = true;

        for (int e2 = 0; e2 < graph.getNumberOfEdges(); e2++) {
          if (!graph.areEdgesAdjacent(e1, e2)) {
            for (int j = 0; j <= expansions.getInt(String.valueOf(e2)); j++) {
              String prefix = first ? "\n" : " + ";
              output.append(prefix + createVarName(new CrossingIndex(e1, i, e2, j)));
              first = false;
            }
          }
        }

        if (!first) {
          output.append(" <= 1");
          numberOfSimplicityConstraints++;
        }
      }
    }

    printNumber("simplicity constraints", numberOfSimplicityConstraints);
    int nOrder = 0;

    output.append("\n\\ Ordering Constraints");

    for (int e1 = 0; e1 < graph.getNumberOfEdges(); e1++) {
      for (int i = 1; i < expansions.getInt(String.valueOf(e1)); i++) {
        first = true;

        for (int e2 = 0; e2 < graph.getNumberOfEdges(); e2++) {
          if (!graph.areEdgesAdjacent(e1, e2)) {
            for (int j = 0; j <= expansions.getInt(String.valueOf(e2)); j++) {
              String prefix = first ? "\n" : " + ";
              output.append(prefix + createVarName(new CrossingIndex(e1, i, e2, j)) + " - "
                  + createVarName(new CrossingIndex(e1, i + 1, e2, j)));
              first = false;
            }
          }
        }

        if (!first) {
          output.append(" >= 0");
          nOrder++;
        }
      }
    }

    printNumber("ordering constraints", nOrder);
    int numberOfFirstSegmentConstraints = 0;

    output.append("\n\\ First Segment Ordering Constraints");

    for (int e1 = 0; e1 < graph.getNumberOfEdges(); e1++) {
      int exp = expansions.getInt(String.valueOf(e1));

      if (exp == graph.getClaimedLowerBound() - 1) {
        first = true;

        for (int e2 = 0; e2 < graph.getNumberOfEdges(); e2++) {
          if (!graph.areEdgesAdjacent(e1, e2)) {
            for (int j = 0; j <= expansions.getInt(String.valueOf(e2)); j++) {
              String prefix = first ? "\n" : " + ";
              output.append(prefix + createVarName(new CrossingIndex(e1, exp, e2, j)) + " - "
                  + createVarName(new CrossingIndex(e1, 0, e2, j)));
              first = false;
            }
          }
        }

        if (!first) {
          output.append(" >= 0");
          numberOfFirstSegmentConstraints++;
        }
      }
    }

    printNumber("first segment ordering constraints", numberOfFirstSegmentConstraints);
    printNumber("Kuratowski constraints", constraints.length());

    for (int i = 0; i < constraints.length(); i++) {
      output.append("\n\\ Kuratowski Constraint " + i + "\n");

      JSONObject constraint = constraints.getJSONObject(i);
      Set<CrossingIndex> requiredCrossings = new HashSet<>();
      CrossingReader crossReader = new CrossingReader(graph);
      first = true;

      for (int j = 0; j < constraint.getJSONArray("requiredCrossings").length(); j++) {
        CrossingIndex crossing =
            crossReader.read(constraint.getJSONArray("requiredCrossings").getJSONArray(j));

        output.append(" - " + createVarName(crossing));
        requiredCrossings.add(crossing);
        first = false;
      }

      JSONArray paths = constraint.getJSONArray("paths");
      PathReader pathReader = new PathReader(graph, requiredCrossings);
      Set<CrossingIndex> feasibleCrossings = new HashSet<>();

      for (int j = 0; j < paths.length(); j++) {
        JSONArray path1 = paths.getJSONArray(j);

        for (int k = j + 1; k < paths.length(); k++) {
          JSONArray path2 = paths.getJSONArray(k);
          Path p1 = pathReader.read(path1);
          Path p2 = pathReader.read(path2);

          if (!p1.isAdjacentTo(p2)) {
            for (int l = 0; l < path1.length(); l++) {
              for (int h = 0; h < path2.length(); h++) {
                JSONObject seg1 = path1.getJSONObject(l);
                JSONObject seg2 = path2.getJSONObject(h);

                int s1 = seg1.getJSONObject("edge").getInt("source");
                int t1 = seg1.getJSONObject("edge").getInt("target");
                int edge1 = graph.getEdgeId(s1, t1);
                int start1 = seg1.getInt("start");
                int end1 = seg1.getInt("end");

                int s2 = seg2.getJSONObject("edge").getInt("source");
                int t2 = seg2.getJSONObject("edge").getInt("target");
                int edge2 = graph.getEdgeId(s2, t2);
                int start2 = seg2.getInt("start");
                int end2 = seg2.getInt("end");

                // since the first segment might participate in multiple paths this condition could
                // be false
                if (!graph.areEdgesAdjacent(edge1, edge2)) {
                  for (int segIndex1 = Math.max(0, start1); segIndex1 <= Math.min(
                      expansions.getInt(String.valueOf(edge1)), end1); segIndex1++) {
                    for (int segIndex2 = Math.max(0, start2); segIndex2 <= Math.min(
                        expansions.getInt(String.valueOf(edge2)), end2); segIndex2++) {
                      feasibleCrossings.add(new CrossingIndex(edge1, segIndex1, edge2, segIndex2));
                    }
                  }
                }
              }
            }
          }
        }
      }

      for (CrossingIndex cross : feasibleCrossings) {
        output.append((first ? "" : " + ") + createVarName(cross));
        first = false;
      }

      output.append(" >= " + (1 - requiredCrossings.size()));
    }

    output.append("\nBounds");
    output.append(boundsOuput);
    output.append("\nEnd");

    return output.toString();
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

  /**
   * Prints a number to the logger which is only shown in verbose mode.
   *
   * @param title A description of the value
   * @param number The integer value
   */
  private void printNumber(String title, int number) {
    Config.get().logger.print(String.format("    # %-35s%8d", title + ":", number));
  }
}
