package proof.solver;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import proof.exception.UnsupportedSolverException;

/**
 * Basic tests for the {@link SolverFactory}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class SolverFactoryTest {
  private SolverFactory solverFactory;

  @Before
  public void setUp() {
    solverFactory = new SolverFactory();
  }

  @Test
  public void testDefault() {
    Solver solver = solverFactory.getSolver(null);
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

  @Test(expected = UnsupportedSolverException.class)
  public void testInvalid() {
    solverFactory.getSolver("foobar");
  }
}
