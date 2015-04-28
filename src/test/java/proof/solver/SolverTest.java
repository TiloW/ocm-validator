package proof.solver;

import static org.junit.Assert.assertEquals;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import proof.exception.LinearProgramException;
import proof.exception.UnsupportedSolverException;

public abstract class SolverTest {

  public static abstract class Initializer {
    public abstract Solver initialize();
  }

  private Solver solver = null;
  private Initializer initializer;

  public SolverTest(Initializer initializer) {
    this.initializer = initializer;
  }

  @Before
  public void setUp() {
    try {
      solver = initializer.initialize();
    } catch (UnsupportedSolverException expected) {
      solver = null;
    }

    Assume.assumeNotNull(solver);
  }

  @Test
  public void testSimple() throws LinearProgramException {
    assertEquals(116, (int) solver.solve("src/test/resources/linear-program/simple.lp"));
  }

  @Test(expected = LinearProgramException.class)
  public void testInfeasible() throws LinearProgramException {
    solver.solve("src/test/resources/linear-program/infeasible.lp");
  }

  @Test
  public void testEmpty() throws LinearProgramException {
    assertEquals(0, (int) solver.solve("src/test/resources/linear-program/empty.lp"));
  }

  @Test(expected = LinearProgramException.class)
  public void testInvalid() throws LinearProgramException {
    assertEquals(0, (int) solver.solve("src/test/resources/linear-program/invalid.lp"));
  }
}
