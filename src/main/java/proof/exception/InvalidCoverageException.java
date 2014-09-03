package proof.exception;

/**
 * Should only be thrown by {@link BranchConverage}.
 * 
 * Represents an error in the variable configuration of the branching tree.
 * 
 * @author Tilo Wiedera
 *
 */
public class InvalidCoverageException extends InvalidProofException {

  public InvalidCoverageException(String description) {
    super(description);
  }
}
