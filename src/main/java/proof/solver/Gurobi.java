package proof.solver;

import proof.exception.LinearProgramException;

/**
 * Wrapper class for performing calls to the Gurobi linear program solver.
 *
 * @author Tilo Wiedera
 */
class Gurobi extends Solver {
  @Override
  protected String getCommand(String filename) {
    return "gurobi_cl " + filename;
  }

  @Override
  protected void handleLine(String line) throws LinearProgramException {
    if (line.contains("Infeasible model")) {
      returnInfeasiblity();
    } else if (line.contains("Optimal objective")) {
      setResult(parseDouble(line));
    }
  }
}
