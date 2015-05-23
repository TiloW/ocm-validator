package proof.exception;


/**
 * Thrown whenever there is invalid input, such that the information could not be read correctly.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class ReaderException extends InvalidProofException {

  public ReaderException(String description) {
    super(description);
  }
}
