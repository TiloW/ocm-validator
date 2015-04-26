package proof.solver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import proof.exception.InfeasibleLinearProgramException;
import proof.exception.InvalidLinearProgramException;

public abstract class SolverTest {
  private Solver solver;

  public SolverTest(Solver solver) {
    this.solver = solver;
  }

  @Test
  public void testSimple() throws InfeasibleLinearProgramException, InvalidLinearProgramException {
    assertEquals(116, (int) solver.solve("src/test/resources/linear-program/simple.lp"));
  }

  @Test(expected = InfeasibleLinearProgramException.class)
  public void testInfeasible() throws InfeasibleLinearProgramException,
      InvalidLinearProgramException {
    solver.solve("src/test/resources/linear-program/infeasible.lp");
  }

  @Test
  public void testEmpty() throws InfeasibleLinearProgramException, InvalidLinearProgramException {
    assertEquals(0, (int) solver.solve("src/test/resources/linear-program/empty.lp"));
  }

  @Test(expected = InvalidLinearProgramException.class)
  public void testInvalid() throws InvalidLinearProgramException, InfeasibleLinearProgramException {
    assertEquals(0, (int) solver.solve("src/test/resources/linear-program/invalid.lp"));
  }
}
