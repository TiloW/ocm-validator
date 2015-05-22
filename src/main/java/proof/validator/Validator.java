package proof.validator;

import proof.exception.InvalidProofException;


/**
 * Generic JSON Validator.
 *
 * Used to define validators for certain sections of the JSON log file.
 *
 * @param <T> The type of object this validator can handle
 *
 * @author Tilo Wiedera
 */
public interface Validator<T> {
  static int level = 0;

  /**
   * Validates the given portion of the log file.
   *
   * @param object The parsed JSON to be validated
   * @throws InvalidProofException Unless all required conditions are met
   */
  public void validate(T object) throws InvalidProofException;
}
