package proof.exception;


/**
 * Base class for all exceptions that disprove the validity of a log.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class InvalidProofException extends Exception {

  public InvalidProofException(String description) {
    super(description);
  }
}
