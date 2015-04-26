package proof.solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import proof.exception.LinearProgramException;

/**
 * Common interface for all linear program solvers.
 * 
 * @author Tilo Wiedera
 */
public abstract class Solver {
  private Double result;
  private String filename;

  /**
   * Solves the linear program contained in the given file. The file must contain a problem
   * described in CPLEX lp format. Will return 0 if the given file is empty.
   *
   * @param filename The file containing the problem
   * @return The optimal objective value
   */
  public double solve(String filename) throws LinearProgramException {
    Process process = null;
    result = null;
    this.filename = filename;

    prepareSolver();

    try {
      process = Runtime.getRuntime().exec(getCommand(filename));
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader errorReader =
          new BufferedReader(new InputStreamReader(process.getErrorStream()));

      for (String line = reader.readLine(); result == null && line != null; line =
          reader.readLine()) {
        if (errorReader.ready()) {
          throw new LinearProgramException(this, filename, errorReader.readLine());
        } else {
          handleLine(line);
        }
      }
    } catch (IOException e) {
      Exception exception = new LinearProgramException(this, filename);
      exception.initCause(e);
    } finally {
      if (process != null) {
        process.destroy();
      }
    }

    if (result == null) {
      throw new LinearProgramException(this, filename, "output is missing some information.");
    }

    return result;
  }

  /**
   * Sets the result of the current computation.
   *
   * @param value The optimal objective value
   */
  protected void setResult(double value) {
    result = value;
  }

  /**
   * Throws an exception to mark this linear program as infeasible.
   *
   * @throws LinearProgramException since the linear program is declared infeasible
   */
  protected void returnInfeasiblity() throws LinearProgramException {
    throw new LinearProgramException(this, filename, "linear program is infeasible.");
  }

  /**
   * Called just before parsing any lines from the solver output.
   */
  protected void prepareSolver() {}

  /**
   * Parses the last double value contained in the line.
   *
   * @param line The line containing the double, typically the optimal objective value.
   * @return The parsed value
   */
  protected double parseDouble(String line) {
    StringTokenizer st = new StringTokenizer(line);
    String value = null;

    while (st.hasMoreTokens()) {
      value = st.nextToken();
    }

    return Double.parseDouble(value);
  }

  /**
   * Returns the command used to execute this solver via command line.
   *
   * @param filename The file containing the linear program to be solved
   * @return The string used to execute the solver
   */
  protected abstract String getCommand(String filename);

  /**
   * Called for each line in the solvers output. This method must be overridden to parse the actual
   * results.
   *
   * @param line The currently investigated line from the solvers output
   */
  protected abstract void handleLine(String line) throws LinearProgramException;
}
