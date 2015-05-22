package proof.exception;

import proof.validator.BranchCoverageValidator;

/**
 * Represents an error in the variable configuration of the branching tree. Should be thrown by
 * {@link BranchCoverageValidator} only.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class InvalidCoverageException extends InvalidProofException {

  public InvalidCoverageException(String description) {
    super(description);
  }
}
