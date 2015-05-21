package proof.exception;

/**
 * Helper for throwing exceptions.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public abstract class ExceptionHelper {
  /**
   * Initializes the cause of {@code outerException} with {@code innerException}. Can be used
   * directly in a {@code throw} statement since it returns the {@code outerException}.
   *
   * @param innerException The exception to be wrapped
   * @param outerException The wrapping exception
   *
   * @return the wrapping exception
   */
  public static <T extends Exception> T wrap(Exception innerException, T outerException) {
    outerException.initCause(innerException);
    return outerException;
  }
}
