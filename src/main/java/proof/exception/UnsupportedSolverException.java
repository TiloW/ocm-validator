package proof.exception;

/**
 * Exception to be thrown when a linear program solver is requested that is not available on the
 * command line.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class UnsupportedSolverException extends RuntimeException {

  /**
   * Initializes a new unsupported solver exception.
   *
   * @param description The description should include the unsupported solvers.
   */
  public UnsupportedSolverException(String description) {
    super(description);
  }
}
