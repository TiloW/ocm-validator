package proof.validator.base;

import org.json.JSONArray;

import proof.exception.InvalidProofException;

/**
 * Generic JSON Array Validator.
 * 
 * Used to define validators for certain sections of the JSON log file.
 * 
 * @author Tilo Wiedera
 *
 */
public interface ArrayValidator extends Validator {

  /**
   * Validates the given portion of the log file.
   * 
   * @param array The parsed JSON to be validated
   * @throws InvalidProofException Unless all required conditions are met
   */
  public void validate(JSONArray array) throws InvalidProofException;

}
