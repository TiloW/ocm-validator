package proof.exception;


/**
 * Base class for all exceptions that disprove the validity of a log.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class InvalidProofException extends Exception {

  public InvalidProofException(String description) {
    super(description);
  }
}
