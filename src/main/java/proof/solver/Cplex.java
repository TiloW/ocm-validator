package proof.solver;

import proof.exception.LinearProgramException;

/**
 * Wrapper class for calling the CPLEX optimization suite linear program solver.
 *
 * @author Tilo Wiedera
 */
class Cplex extends Solver {
  @Override
  protected String getCommand(String filename) {
    return "clpex -c read " + filename + " optimize";
  }

  @Override
  protected void handleLine(String line) throws LinearProgramException {
    if (line.contains("Dual simplex - Optimal:")) {
      setResult(parseDouble(line));
    } else if (line.contains("No problem exists.")) {
      setResult(0);
    } else if (line.contains("Infeasible.")) {
      returnInfeasiblity();
    }
  }
}
