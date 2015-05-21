package proof.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests for {@link ProgressLogger}.
 *
 * @author Tilo Wiedera
 */
@RunWith(Parameterized.class)
public class ProgressLoggerTest {
  private final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
  private final boolean verbose;
  private final PrintStream printStream = new PrintStream(byteStream);
  private final ProgressLogger logger;
  private final int MAX_PROGRESS = 42;


  @Parameterized.Parameters(name = "verbose={0}")
  public static Boolean[] getParameters() {
    Boolean[] result = {true, false};
    return result;
  }

  public ProgressLoggerTest(boolean verbose) {
    this.verbose = verbose;
    logger = new ProgressLogger(printStream, verbose);
  }

  @Before
  public void setUp() throws IOException {
    logger.reset(MAX_PROGRESS);
    printStream.flush();
    byteStream.reset();
  }

  @Test
  public void testReset() {
    logger.reset(MAX_PROGRESS);
  }

  @Test
  public void testReset_progress() {
    for (int i = 0; i < MAX_PROGRESS; i++) {
      logger.progress();
    }

    try {
      logger.progress();
      fail("Progress should never exceed the maximum value of " + MAX_PROGRESS + ".");
    } catch (IllegalArgumentException expected) {
      // this is supposed to happen
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReset_invalidSizeZero() {
    logger.reset(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReset_invalidSizeNegative() {
    logger.reset(-MAX_PROGRESS);
  }

  @Test
  public void testProgress() {
    for (int i = 0; i < MAX_PROGRESS; i++) {
      logger.progress("foobar");
    }

    assertEquals(verbose ? MAX_PROGRESS : 1, getOutput().length);
  }

  @Test
  public void testPrint() {
    logger.println("stet clita kasd gubergren");

    for (int i = 0; i < 3; i++) {
      logger.print("sed diam voluptua");
    }

    assertEquals(verbose ? 4 : 2, getOutput().length);
  }

  @Test
  public void testPrintln() {
    for (int i = 0; i < 3; i++) {
      logger.println("dolor sit amet");
    }

    for (int i = 0; i < MAX_PROGRESS; i++) {
      logger.progress();
    }

    assertEquals(verbose ? 3 : 4, getOutput().length);
  }

  private String[] getOutput() {
    printStream.flush();
    return byteStream.toString().split("\\r?\\n");
  }
}
