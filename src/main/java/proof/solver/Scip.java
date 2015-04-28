package proof.solver;

import proof.exception.LinearProgramException;


/**
 * Simple wrapper for executing the SCIP optimization suite linear program solver. This requires the
 * scip command to be available on the console.
 *
 * @author Tilo Wiedera
 */
class Scip extends Solver {
  private boolean isFeasible;

  @Override
  protected void prepareSolver() {
    isFeasible = false;
  }

  @Override
  protected String getCommand(String filename) {
    return "scip -f " + filename;
  }

  @Override
  protected void handleLine(String line) throws LinearProgramException {
    if (line.contains("problem is solved [optimal solution found]")) {
      isFeasible = true;
    } else if (line.contains("problem is solved [infeasible]")) {
      returnInfeasiblity();
    } else if (isFeasible && line.contains("objective value:")) {
      setResult(parseDouble(line));
    }
  }
}