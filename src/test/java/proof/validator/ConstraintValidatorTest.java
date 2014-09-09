// package proof.validator;
//
// import java.io.IOException;
// import java.util.HashMap;
//
// import org.junit.Test;
//
// import proof.ResourceBasedTest;
// import proof.data.CrossingIndex;
// import proof.data.Graph;
// import proof.exception.InvalidConstraintException;
// import proof.exception.InvalidProofException;
//
// public class ConstraintValidatorTest extends ResourceBasedTest {
//
// private ConstraintValidator getValidator() {
// return getValidator(666);
// }
//
// /**
// * Returns a basic validator that can be used for testing.
// *
// * Should not be used for testing problem specific behaviour since the underlying graph is not
// * valid. No branching variables are set.
// *
// * @param numberOfSegments The crossing number
// */
// private ConstraintValidator getValidator(int numberOfSegments) {
// return new ConstraintValidator(new HashMap<CrossingIndex, Boolean>(), numberOfSegments,
// new Graph(1, 1));
// }
//
// @Override
// protected String getResourceSubdir() {
// return "constraint";
// }
//
// @Test(expected = InvalidConstraintException.class)
// public void testValidate_empty() throws IOException, InvalidProofException {
// getValidator().validate(loadJSON("invalid/empty"));
// }
// }
