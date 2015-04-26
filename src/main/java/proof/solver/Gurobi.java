package proof.solver;

import proof.exception.InfeasibleLinearProgramException;

/**
 * Wrapper class for performing calls to the Gurobi linear program solver.
 *
 * @author Tilo Wiedera
 */
public class Gurobi extends Solver {
  @Override
  protected String getCommand(String filename) {
    return "gurobi_cl " + filename;
  }

  @Override
  protected void handleLine(String filename, String line) throws InfeasibleLinearProgramException {
    if (line.contains("Infeasible model")) {
      throw new InfeasibleLinearProgramException(filename);
    } else if (line.contains("Optimal objective")) {
      setResult(parseDouble(line));
    }
  }
}
