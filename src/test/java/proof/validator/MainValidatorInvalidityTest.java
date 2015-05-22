package proof.validator;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import proof.ValidatorTest;
import proof.exception.InvalidConfigurationException;
import proof.exception.InvalidProofException;

/**
 * Invalidity-tests for the {@link MainValidator}.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class MainValidatorInvalidityTest extends ValidatorTest {

  public MainValidatorInvalidityTest() throws InvalidConfigurationException {
    super(MainValidatorTest.DIR + "/invalid");
  }

  @Test(expected = InvalidProofException.class)
  public void testMissingConstraint() throws InvalidProofException, IOException {
    new MainValidator().validate(loadJSON("missing-constraint.json"));
  }

  @Test(expected = InvalidProofException.class)
  public void testMissingPath() throws InvalidProofException, IOException {
    new MainValidator().validate(loadJSON("missing-path.json"));
  }

  @Test(expected = InvalidProofException.class)
  public void testInvalidCrossing() throws InvalidProofException, IOException {
    new MainValidator().validate(loadJSON("invalid-crossing.json"));
  }

  @Test(expected = InvalidProofException.class)
  public void testNonTrivial() throws InvalidProofException, IOException {
    new MainValidator().validate(loadJSON("non-trivial.json"));
  }

  @Test(expected = JSONException.class)
  public void testEmpty() throws InvalidProofException {
    new MainValidator().validate(new JSONObject());
  }
}
