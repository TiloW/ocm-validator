package proof.validator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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
import proof.exception.LinearProgramException;
import proof.solver.Solver;
import proof.solver.SolverFactory;

public class LeafValidator implements Validator<JSONObject> {
  private final Graph graph;
  private final Solver solver;

  public LeafValidator(Graph graph) {
    this.graph = graph;

    // TODO specifiy solver py parameter
    this.solver = new SolverFactory().getSolver(null);
  }

  @Override
  public void validate(JSONObject leaf) throws InvalidProofException {
    JSONArray variables = leaf.getJSONArray("fixedVariables");

    CrossingReader crossingReader = new CrossingReader(graph);
    Map<CrossingIndex, Boolean> vars = new HashMap<CrossingIndex, Boolean>();

    for (int j = 0; j < variables.length(); j++) {
      JSONObject variable = variables.getJSONObject(j);

      CrossingIndex cross = crossingReader.read(variable.getJSONArray("crossing"));

      vars.put(cross, variable.getInt("value") == 1);
    }

    ConstraintValidator constraintValidator = new ConstraintValidator(graph, vars);
    JSONArray constraints = leaf.getJSONArray("constraints");

    for (int j = 0; j < constraints.length(); j++) {
      constraintValidator.validate(constraints.getJSONObject(j));
    }

    // TODO: Temporarily logging LPs
    String file = null;
    try {
      int expected = graph.getClaimedLowerBound();
      file = File.createTempFile("leaf", "." + expected + ".lp").getAbsolutePath();
      PrintWriter out = new PrintWriter(file);
      out.print(generateLinearProgram(vars, leaf));
      out.close();
      double lowerBound = solver.solve(file);

      if (Math.ceil(solver.solve(file)) < expected) {
        throw new LinearProgramException(solver, file, "Lower bound is too small: " + lowerBound
            + " instead of " + expected);
      }
    } catch (IOException e) {
      LinearProgramException lpException = new LinearProgramException(solver, file);
      lpException.initCause(e);
      throw lpException;
    }
  }

  public String generateLinearProgram(Map<CrossingIndex, Boolean> fixedVariables, JSONObject leaf) {
    JSONArray constraints = leaf.getJSONArray("constraints");
    JSONObject expansions = leaf.getJSONObject("expansions");

    StringBuilder output = new StringBuilder();
    StringBuilder boundsOuput = new StringBuilder();

    output.append("Minimize\n obj:");
    boolean first = true;
    for (int e1 = 0; e1 < graph.getNumberOfEdges(); e1++) {
      for (int e2 = e1 + 1; e2 < graph.getNumberOfEdges(); e2++) {
        if (!graph.edgesAreAdjacent(e1, e2)) {
          int cost = graph.getEdgeCost(e1) * graph.getEdgeCost(e2);
          for (int i = 0; i <= expansions.getInt(String.valueOf(e1)); i++) {
            for (int j = 0; j <= expansions.getInt(String.valueOf(e2)); j++) {
              String weight = cost == 1 ? "" : Integer.toString(cost);
              String varName = createVarName(new CrossingIndex(e1, i, e2, j));
              output.append((first ? " " : " + ") + weight + varName);
              boundsOuput.append("\n 0 <= " + varName + " <= 1");
              first = false;
            }
          }
        }
      }
    }

    output.append("\nSubject To");
    output.append("\n\\ Fixed Branching Variables");

    Set<CrossingIndex> realizedCrossings = new HashSet<>();
    for (Map.Entry<CrossingIndex, Boolean> var : fixedVariables.entrySet()) {
      int value = var.getValue() ? 1 : 0;
      output.append("\n" + createVarName(var.getKey()) + " = " + value);

      if (var.getValue()) {
        realizedCrossings.add(var.getKey());
      }
    }

    // note that simplicity is not required on the first segment
    output.append("\n\\ Simplicity Constraints");
    for (int e1 = 0; e1 < graph.getNumberOfEdges(); e1++) {
      for (int i = 1; i <= expansions.getInt(String.valueOf(e1)); i++) {
        first = true;
        for (int e2 = 0; e2 < graph.getNumberOfEdges(); e2++) {
          if (!graph.edgesAreAdjacent(e1, e2)) {
            for (int j = 0; j <= expansions.getInt(String.valueOf(e2)); j++) {
              output.append((first ? "\n " : " + ")
                  + createVarName(new CrossingIndex(e1, i, e2, j)));
              first = false;
            }
          }
        }

        if (!first) {
          output.append(" <= 1");
        }
      }
    }

    // TODO investigate ordering constraints
    output.append("\n\\ Ordering Constraints");
    for (int e1 = 0; e1 < graph.getNumberOfEdges(); e1++) {
      for (int i = 1; i <= expansions.getInt(String.valueOf(e1)); i++) {
        first = true;
        for (int e2 = e1 + 1; e2 < graph.getNumberOfEdges(); e2++) {
          for (int j = 0; j <= expansions.getInt(String.valueOf(e2)); j++) {
            if (!graph.edgesAreAdjacent(e1, e2)) {
              output.append((first ? "\n " : " + ")
                  + createVarName(new CrossingIndex(e1, i - 1, e2, j)) + " - "
                  + createVarName(new CrossingIndex(e1, i, e2, j)));
              first = false;
            }
          }
        }
        if (!first) {
          output.append(" >= 0");
        }
      }
    }

    output.append("\n\\ Kuratowski Constraints");
    first = true;

    // vars
    for (int i = 0; i < constraints.length(); i++) {
      Set<CrossingIndex> constraintCrossings = new HashSet<CrossingIndex>(realizedCrossings);
      JSONObject constraint = constraints.getJSONObject(i);
      JSONArray crossings = constraint.getJSONArray("requiredCrossings");

      output.append("\n \\ Constraint #" + i + "\n ");

      first = true;
      CrossingReader crossReader = new CrossingReader(graph);
      for (int j = 0; j < crossings.length(); j++) {
        CrossingIndex crossing = crossReader.read(crossings.getJSONArray(j));
        output.append((first ? "" : " + ") + "1 - " + createVarName(crossing));
        first = false;
        constraintCrossings.add(crossing);
      }

      JSONArray paths = constraint.getJSONArray("paths");

      PathReader pathReader = new PathReader(graph, constraintCrossings);

      for (int j = 0; j < paths.length(); j++) {
        JSONArray path1 = paths.getJSONArray(j);

        for (int k = j + 1; k < paths.length(); k++) {
          JSONArray path2 = paths.getJSONArray(k);

          Path p1 = pathReader.read(path1);
          Path p2 = pathReader.read(path2);

          if (!p1.getSource().equals(p2.getSource()) && !p1.getSource().equals(p2.getTarget())
              && !p1.getTarget().equals(p2.getSource()) && !p1.getTarget().equals(p2.getTarget())) {
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

                if (!graph.edgesAreAdjacent(edge1, edge2)) {
                  for (int segIndex1 = Math.max(0, start1); segIndex1 <= Math.min(
                      expansions.getInt(String.valueOf(edge1)), end1); segIndex1++) {
                    for (int segIndex2 = Math.max(0, start2); segIndex2 <= Math.min(
                        expansions.getInt(String.valueOf(edge2)), end2); segIndex2++) {
                      output.append((first ? "" : " + ")
                          + createVarName(new CrossingIndex(edge1, segIndex1, edge2, segIndex2)));
                      first = false;
                    }
                  }
                }
              }
            }
          }
        }
      }

      output.append(" >= 1");
    }

    output.append("\nBounds");
    output.append(boundsOuput);
    output.append("\nEnd");

    return output.toString();
  }

  private String createVarName(CrossingIndex crossing) {
    SegmentIndex s1 = crossing.segments[0];
    SegmentIndex s2 = crossing.segments[1];
    return "x_e" + s1.edge + "_s" + s1.segment + "_e" + s2.edge + "_s" + s2.segment;
  }
}
