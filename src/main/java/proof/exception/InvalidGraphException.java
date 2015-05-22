package proof.exception;

import proof.data.Graph;

/**
 * Thrown when a constructed {@link Graph} turns out to be invalid. This might be due to ambiguous
 * edges or missing edges.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class InvalidGraphException extends RuntimeException {

  public InvalidGraphException(String description) {
    super(description);
  }

}
