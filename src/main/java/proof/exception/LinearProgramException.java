package proof.exception;

import proof.solver.Solver;

/**
 * Thrown whenever a linear program could not be solved.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class LinearProgramException extends InvalidProofException {
  /**
   * Creates a new linear program exception. Includes the provided error message.
   *
   * @param solver The executing solver
   * @param filename The file containing the linear program
   * @param errorMessage An optional error message, set to {@code null} to ignore
   */
  public LinearProgramException(Solver solver, String filename, String errorMessage) {
    super(solver.getClass().getSimpleName() + " error while solving " + filename
        + (errorMessage == null ? "" : ("\n" + errorMessage)));
  }

  /**
   * Creates a new linear program exception.
   *
   * @param solver The executing solver
   * @param filename The file containing the linear program
   */
  public LinearProgramException(Solver solver, String filename) {
    this(solver, filename, null);
  }
}
