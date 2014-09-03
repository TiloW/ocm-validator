package proof.validator;

import org.json.JSONArray;
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
public interface Validator {

  /**
   * Validates the given portion of the log file.
   * 
   * Each {@link JSONArray} must be wrapped by a {@link JSONObject}.
   * 
   * @param object The parsed JSON to be validated
   * @throws InvalidProofException Unless all required conditions are met
   */
  public void validate(JSONObject object) throws InvalidProofException;

}
