package proof.validator;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import proof.ValidatorTest;
import proof.exception.InvalidConfigurationException;
import proof.exception.InvalidProofException;

import java.io.IOException;

/**
 * Invalidity-tests for the {@link MainValidator}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class MainValidatorInvalidityTest extends ValidatorTest {

  public MainValidatorInvalidityTest() throws InvalidConfigurationException {
    super(MainValidatorTest.DIR + "/invalid");
  }

  @Test(expected = InvalidProofException.class)
  public void testMissingConstraint() throws InvalidProofException, IOException {
    new MainValidator().validate(loadJson("missing-constraint.json"));
  }

  @Test(expected = InvalidProofException.class)
  public void testMissingPath() throws InvalidProofException, IOException {
    new MainValidator().validate(loadJson("missing-path.json"));
  }

  @Test(expected = InvalidProofException.class)
  public void testInvalidCrossing() throws InvalidProofException, IOException {
    new MainValidator().validate(loadJson("invalid-crossing.json"));
  }

  @Test(expected = InvalidProofException.class)
  public void testNonTrivial() throws InvalidProofException, IOException {
    new MainValidator().validate(loadJson("non-trivial.json"));
  }

  @Test(expected = JSONException.class)
  public void testEmpty() throws InvalidProofException {
    new MainValidator().validate(new JSONObject());
  }
}
