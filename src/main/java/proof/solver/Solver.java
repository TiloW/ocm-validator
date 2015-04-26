package proof.solver;

import proof.exception.InfeasibleLinearProgramException;
import proof.exception.InvalidLinearProgramException;

/**
 * Common interface for all linear program solvers.
 * 
 * @author Tilo Wiedera
 */
public interface Solver {
  /**
   * Solves the linear program contained in the given file. The file must contain a problem
   * described in CPLEX lp format.
   * 
   * @param filename The file containing the problem
   * @return The optimal objective value
   */
  public double solve(String filename) throws InfeasibleLinearProgramException,
      InvalidLinearProgramException;
}
