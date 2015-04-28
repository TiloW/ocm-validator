package proof.solver;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import proof.exception.UnsupportedSolverException;

/**
 * Basic tests for the {@link SolverFactory}.
 *
 * @author Tilo Wiedera
 */
public class SolverFactoryTest {
  private SolverFactory solverFactory;

  @Before
  public void setUp() {
    solverFactory = new SolverFactory();
  }

  @Test
  public void testDefault() {
    try {
      Solver solver = solverFactory.getSolver(null);
      assertSame(solver, solverFactory.getSolver(null));
    } catch (UnsupportedSolverException expected) {
    }
  }

  @Test
  public void testCplex() {
    try {
      assertTrue(solverFactory.getSolver("cplex") instanceof Cplex);
    } catch (UnsupportedSolverException expected) {
    }
  }

  @Test
  public void testScip() {
    try {
      assertTrue(solverFactory.getSolver("scip") instanceof Scip);
    } catch (UnsupportedSolverException expected) {
    }

  }

  @Test
  public void testGurobi() {
    try {
      assertTrue(solverFactory.getSolver("gurobi") instanceof Gurobi);
    } catch (UnsupportedSolverException expected) {
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalid() {
    try {
      solverFactory.getSolver("foobar");
    } catch (UnsupportedSolverException expected) {
    }
  }
}