package proof.exception;

import proof.validator.BranchCoverageValidator;

/**
 * Should only be thrown by {@link BranchCoverageValidator}.
 *
 * Represents an error in the variable configuration of the branching tree.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class InvalidCoverageException extends InvalidProofException {

  public InvalidCoverageException(String description) {
    super(description);
  }
}
