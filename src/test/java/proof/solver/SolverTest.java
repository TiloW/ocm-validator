package proof.solver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import proof.exception.LinearProgramException;

public abstract class SolverTest {
  private Solver solver;

  public SolverTest(Solver solver) {
    this.solver = solver;
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
