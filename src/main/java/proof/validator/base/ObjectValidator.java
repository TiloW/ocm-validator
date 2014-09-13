package proof.validator.base;

import org.json.JSONObject;

import proof.exception.InvalidProofException;

/**
 * Generic JSON Validator.
 * 
 * Used to define validators for certain sections of the JSON log file.
 * 
 * @author Tilo Wiedera
 *
 */
public interface ObjectValidator extends Validator {

  /**
   * Validates the given portion of the log file.
   * 
   * @param object The parsed JSON to be validated
   * @throws InvalidProofException Unless all required conditions are met
   */
  public void validate(JSONObject object) throws InvalidProofException;

}
