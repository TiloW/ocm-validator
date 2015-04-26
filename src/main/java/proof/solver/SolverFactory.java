package proof.solver;

/**
 * Used for instantiating a linear program solver based on the given command line arguments.
 */
public class SolverFactory {
  private Solver solver;

  /**
   * Returns the solver to be used for validating the results of all linear programs.
   */
  public Solver getSolver(String param) {
    if (solver == null) {
      // TODO
    }

    return solver;
  }
}
