package proof.solver;

import proof.exception.InfeasibleLinearProgramException;


/**
 * Simple wrapper for executing the SCIP optimization suite linear program solver. This requires the
 * scip command to be available on the console.
 *
 * @author Tilo Wiedera
 */
public class Scip extends Solver {
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
  protected void handleLine(String filename, String line) throws InfeasibleLinearProgramException {
    if (line.contains("problem is solved [optimal solution found]")) {
      isFeasible = true;
    } else if (line.contains("problem is solved [infeasible]")) {
      throw new InfeasibleLinearProgramException(filename);
    } else if (isFeasible && line.contains("objective value:")) {
      setResult(parseDouble(line));
    }
  }
}
