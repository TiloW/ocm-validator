package proof.solver;

import proof.exception.LinearProgramException;

/**
 * Wrapper class for performing calls to the Gurobi linear program solver.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
class Gurobi extends Solver {
  @Override
  protected String getCommand(String filename) {
    return "gurobi_cl " + filename;
  }

  @Override
  protected void handleLine(String line) throws LinearProgramException {
    if (line.contains("Infeasible model") || line.contains("(null): 0 rows, 0 columns, 0 nonzeros")) {
      returnInfeasiblity();
    } else if (line.contains("Optimal objective")) {
      setResult(parseDouble(line));
    }
  }
}
