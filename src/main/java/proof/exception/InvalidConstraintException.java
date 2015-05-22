package proof.exception;

import proof.validator.ConstraintValidator;

/**
 * Thrown whenever the Kuratowski {@link ConstraintValidator} encounteres an error.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class InvalidConstraintException extends InvalidProofException {

  public InvalidConstraintException(String description) {
    super(description);
  }
}
