package proof.solver;

import proof.exception.LinearProgramException;

/**
 * Wrapper class for performing calls to the Gurobi linear program solver.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
class Gurobi extends Solver {
  @Override
  protected String getCommand(String filename) {
    return "gurobi_cl " + filename;
  }

  @Override
  protected void handleLine(String line) throws LinearProgramException {
    boolean empty = line.contains("(null): 0 rows, 0 columns, 0 nonzeros");

    if (empty || line.contains("Infeasible model")) {
      returnInfeasiblity();
    } else if (line.contains("Optimal objective")) {
      setResult(parseDouble(line));
    }
  }
}
