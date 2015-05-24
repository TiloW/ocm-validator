package proof.validator;

import proof.exception.InvalidProofException;


/**
 * Generic JSON Validator. Used to define validators for certain sections of the JSON log file.
 *
 * @param <T> type of object this validator can handle
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public interface Validator<T> {
  /**
   * Validates the given portion of the log file.
   *
   * @param object parsed JSON to be validated
   * @throws InvalidProofException unless all required conditions are met
   */
  public void validate(T object) throws InvalidProofException;
}
