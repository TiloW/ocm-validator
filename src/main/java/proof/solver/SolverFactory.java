package proof.solver;

import proof.exception.UnsupportedSolverException;

/**
 * Used for instantiating a linear program solver based on the given command line arguments.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class SolverFactory {

  /**
   * Returns the solver to be used for validating the results of all linear programs. The parameter
   * is respected only if the solver is requested for the first time.
   *
   * @param param command line argument to specify the solver
   * @return a wrapper for the chosen linear program solver
   *
   * @throws UnsupportedSolverException if the requested solver is not supported
   */
  public Solver getSolver(String param) {
    Solver result = null;

    if (param == null || param == "") {
      result = chooseSolver();
    } else {
      switch (param) {
        case "cplex":
          result = new Cplex();
          break;
        case "scip":
          result = new Scip();
          break;
        case "gurobi":
          result = new Gurobi();
          break;
        default:
          throw new UnsupportedSolverException(
              "The requested linear program solver is not supported: " + param);
      }
    }

    return result;
  }

  /**
   * Detects which linear program solvers are available and chooses one.
   *
   * @return the chosen solver
   * @throws UnsupportedSolverException if no solver is available
   */
  private Solver chooseSolver() {
    Solver result = null;

    try {
      result = new Gurobi();
    } catch (UnsupportedSolverException e) {
      result = null;
    }

    if (result == null) {
      try {
        result = new Cplex();
      } catch (UnsupportedSolverException e) {
        result = null;
      }
    }

    if (result == null) {
      try {
        result = new Scip();
      } catch (UnsupportedSolverException e) {
        throw new UnsupportedSolverException(
            "None of the supported linear program solvers is available on this system.");
      }
    }

    return result;
  }
}
