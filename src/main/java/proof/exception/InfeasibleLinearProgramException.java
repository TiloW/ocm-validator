package proof.exception;

/**
 * Thrown whenever a linear program turns out to be infeasible.
 * 
 * @author Tilo Wiedera
 */
public class InfeasibleLinearProgramException extends InvalidProofException {
  /**
   * Creates a new infeasibility exception.
   * 
   * @param filename The file containing the infeasible linear program
   */
  public InfeasibleLinearProgramException(String filename) {
    super("Infeasible linear program encountered: " + filename);
  }
}
