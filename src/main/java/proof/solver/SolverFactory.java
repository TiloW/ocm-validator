package proof.solver;

import proof.exception.UnsupportedSolverException;

/**
 * Used for instantiating a linear program solver based on the given command line arguments.
 */
public class SolverFactory {
  private Solver solver;

  /**
   * Returns the solver to be used for validating the results of all linear programs. The parameter
   * is respected only if the solver is requested for the first time.
   *
   * @param param The command line argument to specify the solver
   * @throws IllegalArgumentException if the requested solver is not supported
   */
  public Solver getSolver(String param) {
    if (solver == null) {
      if (param == null || param == "") {
        chooseSolver();
      } else if (param == "cplex") {
        solver = new Cplex();
      } else if (param == "scip") {
        solver = new Scip();
      } else if (param == "gurobi") {
        solver = new Gurobi();
      } else {
        throw new IllegalArgumentException("The requested linear program solver is not supported: "
            + param);
      }
    }

    return solver;
  }

  /**
   * Detects which linear program solvers are available and chooses one.
   *
   * @return The chosen solver
   */
  private void chooseSolver() {
    solver = null;

    try {
      solver = new Gurobi();
    } catch (UnsupportedSolverException e) {
    }

    if (solver == null) {
      try {
        solver = new Cplex();
      } catch (UnsupportedSolverException e) {
      }
    }

    if (solver == null) {
      try {
        solver = new Scip();
      } catch (UnsupportedSolverException e) {
        throw new UnsupportedSolverException(
            "None of the supported linear program solvers is available on this system.");
      }
    }
  }
}