package proof.exception;

/**
 * Thrown when the user hands illegal arguments to the program.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class InvalidConfigurationException extends Exception {
  public InvalidConfigurationException(String message) {
    super(message);
  }
}
