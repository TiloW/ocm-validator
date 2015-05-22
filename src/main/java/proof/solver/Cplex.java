package proof.solver;

import proof.exception.LinearProgramException;

/**
 * Wrapper class for calling the CPLEX optimization suite linear program solver.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
class Cplex extends Solver {
  @Override
  protected String getCommand(String filename) {
    return "cplex -c read " + filename + " optimize quit";
  }

  @Override
  protected void handleLine(String line) throws LinearProgramException {
    if (line.contains(" - Optimal:")) {
      setResult(parseDouble(line));
    } else if (line.contains("No problem exists.")) {
      setResult(0);
    } else if (line.contains("Infeasible.") || line.contains("CPLEX Error")) {
      returnInfeasiblity();
    }
  }
}
