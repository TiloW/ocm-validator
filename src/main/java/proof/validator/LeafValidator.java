package proof.validator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.reader.CrossingReader;
import proof.exception.ExceptionHelper;
import proof.exception.InvalidProofException;
import proof.exception.LinearProgramException;
import proof.solver.LinearProgramGenerator;
import proof.solver.Solver;
import proof.util.Config;

/**
 * Validates a single leaf of the branch and bound tree. All Kuratowski subdivions in the leaf are
 * validated by the {@link ConstraintValidator}. A linear program is generated by the
 * {@link LinearProgramGenerator} to validate the claimed lower bound.
 *
 * @author Tilo Wiedera
 */
public class LeafValidator implements Validator<JSONObject> {
  private final Graph graph;
  private final Solver solver;
  private final LinearProgramGenerator generator;

  /**
   * Initializes a new leaf validator.
   *
   * @param graph The graph to work with.
   */
  public LeafValidator(Graph graph) {
    this.graph = graph;
    generator = new LinearProgramGenerator(graph);
    solver = Config.get().solver;
  }

  @Override
  public void validate(JSONObject leaf) throws InvalidProofException {
    // extract branching variables
    JSONArray variables = leaf.getJSONArray("fixedVariables");

    CrossingReader crossingReader = new CrossingReader(graph);
    Map<CrossingIndex, Boolean> vars = new HashMap<CrossingIndex, Boolean>();

    for (int j = 0; j < variables.length(); j++) {
      JSONObject variable = variables.getJSONObject(j);
      CrossingIndex cross = crossingReader.read(variable.getJSONArray("crossing"));
      vars.put(cross, variable.getInt("value") == 1);
    }

    // validate all Kuratowski constraints
    ConstraintValidator constraintValidator = new ConstraintValidator(graph);
    JSONArray constraints = leaf.getJSONArray("constraints");

    for (int j = 0; j < constraints.length(); j++) {
      try {
        Config.get().logger.progress("  Kuratowski constraint " + j);
        constraintValidator.validate(constraints.getJSONObject(j));
      } catch (InvalidProofException e) {
        throw ExceptionHelper.wrap(e, new InvalidProofException("Could not validate constraint "
            + j));
      }
    }

    // validate the claimed lower bound
    String file = null;
    try {
      int expected = graph.getClaimedLowerBound();

      file = File.createTempFile("leaf", "." + expected + ".lp").getAbsolutePath();
      PrintWriter out = new PrintWriter(file);
      Config.get().logger.print("  generate linear program");
      out.print(generator.createLinearProgram(vars, leaf));
      out.close();
      Config.get().logger.print("  linear program written to " + file);
      Config.get().logger.print("  solve linear program");

      double lowerBound = solver.solve(file);

      if (Math.ceil(lowerBound) < expected) {
        throw new LinearProgramException(solver, file, "Lower bound is too small: " + lowerBound
            + " instead of " + expected);
      }
    } catch (IOException e) {
      throw ExceptionHelper.wrap(e, new LinearProgramException(solver, file));
    }
  }
}
