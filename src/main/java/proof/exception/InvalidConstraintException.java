package proof.exception;

import proof.validator.ConstraintValidator;

/**
 * Thrown whenever the Kuratowski {@link ConstraintValidator} encounteres an error.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class InvalidConstraintException extends InvalidProofException {

  public InvalidConstraintException(String description) {
    super(description);
  }
}
