package proof.exception;

import proof.data.Path;

/**
 * Thrown whenever a parsed Kuratowski {@link Path} turns out to be invalid.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class InvalidPathException extends RuntimeException {

  public InvalidPathException(String description) {
    super(description);
  }

}
