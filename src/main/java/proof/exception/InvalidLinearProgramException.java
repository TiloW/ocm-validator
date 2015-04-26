package proof.exception;

/**
 * Thrown whenever a linear program could not be parsed.
 * 
 * @author Tilo Wiedera
 */
public class InvalidLinearProgramException extends InvalidProofException {
  /**
   * Creates a new invalidity exception.
   * 
   * @param filename The file containing the invalid linear program
   */
  public InvalidLinearProgramException(String filename, String message) {
    super(message == null ? "Could not parse invalid linear program: "
        : (message + " in " + filename));
  }

  /**
   * Creates a new invalidity exception.
   * 
   * @param filename The file containing the invalid linear program
   */
  public InvalidLinearProgramException(String filename) {
    this(filename, null);
  }
}
