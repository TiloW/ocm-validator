package proof.exception;

/**
 * Exception to be thrown when a linear program solver is requested that is not available on the
 * command line.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class UnsupportedSolverException extends RuntimeException {

  /**
   * Initializes a new unsupported solver exception.
   *
   * @param description description of the problem, should name the unsupported solvers
   */
  public UnsupportedSolverException(String description) {
    super(description);
  }
}
