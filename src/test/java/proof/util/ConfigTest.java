package proof.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

import proof.exception.InvalidConfigurationException;
import proof.exception.UnsupportedSolverException;
import proof.solver.SolverFactory;

/**
 * Tests for {@link Config}.
 *
 * @author Tilo Wiedera
 */
public class ConfigTest {
  private final static String FILE = "src/test/resources/log/invalid/empty.json";
  private final PrintStream out = new PrintStream(new OutputStream() {
    @Override
    public void write(int b) {}
  });

  @Test
  public void testSimple() throws InvalidConfigurationException {
    String[] args = {"-f", FILE};
    Config config = new Config(args, out);

    assertFalse(config.verbose);
    assertEquals(FILE, config.file.toString());
  }

  @Test
  public void testVerbose() throws InvalidConfigurationException {
    String[] args = {"-f", "src/test/resources/log/invalid/empty.json", "-v"};
    Config config = new Config(args, out);

    assertTrue(config.verbose);
  }

  @Test
  public void testSolvers() throws InvalidConfigurationException {
    SolverFactory factory = new SolverFactory();

    try {
      factory.getSolver("gurobi");
      String[] args = {"-f", FILE, "-s", "gurobi"};
      new Config(args, out);
    } catch (UnsupportedSolverException expected) {
    }

    try {
      factory.getSolver("cplex");
      String[] args = {"-f", FILE, "-s", "cplex"};
      new Config(args, out);
    } catch (UnsupportedSolverException expected) {
    }

    try {
      factory.getSolver("scip");
      String[] args = {"-f", FILE, "-s", "scip"};
      new Config(args, out);
    } catch (UnsupportedSolverException expected) {
    }
  }

  @Test(expected = InvalidConfigurationException.class)
  public void testInvalidFile() throws InvalidConfigurationException {
    String[] args = {"-f", "invalid-file-path"};
    new Config(args, out);
  }

  @Test(expected = InvalidConfigurationException.class)
  public void testMissingFile() throws InvalidConfigurationException {
    String[] args = {"-f", "-v"};
    new Config(args, out);
  }

  @Test(expected = InvalidConfigurationException.class)
  public void testEmpty() throws InvalidConfigurationException {
    new Config(new String[0], out);
  }

  @Test(expected = InvalidConfigurationException.class)
  public void testInvalidFlag() throws InvalidConfigurationException {
    String[] args = {"-f", FILE, "some-invalid-flag"};
    new Config(args, out);
  }
}
