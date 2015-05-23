package proof.exception;

import proof.data.Path;

/**
 * Thrown whenever a parsed Kuratowski {@link Path} turns out to be invalid.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class InvalidPathException extends ReaderException {

  public InvalidPathException(String description) {
    super(description);
  }

}
