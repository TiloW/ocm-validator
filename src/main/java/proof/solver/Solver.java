package proof.solver;

import proof.exception.ExceptionHelper;
import proof.exception.LinearProgramException;
import proof.exception.UnsupportedSolverException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * Common interface for all linear program solvers.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public abstract class Solver {
  private Double result;
  private String filename;

  /**
   * Initializes the solver and asserts it is available.
   */
  public Solver() {
    if (!isAvailable()) {
      throw new UnsupportedSolverException(getClass().getName()
          + " is not available. Calling this solver would fail: "
          + getCommand("my-linear-program.lp"));
    }
  }

  /**
   * Solves the linear program contained in the given file. The file must contain a problem
   * described in CPLEX LP format. Will return 0 if the given file is empty.
   *
   * @param filename name of the file containing the problem
   * @return the optimal objective value
   *
   * @throws LinearProgramException if the generated program could not be solved or the optimal
   *         solution is too low
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
      throw ExceptionHelper.wrap(e, new LinearProgramException(this, filename));
    } finally {
      if (process != null) {
        process.destroy();
      }
    }

    if (result == null) {
      throw new LinearProgramException(this, filename, "Output is missing some information.");
    }

    return result;
  }

  /**
   * Sets the result of the current computation.
   *
   * @param value parsed optimal objective value
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
    throw new LinearProgramException(this, filename, "Linear program is infeasible.");
  }

  /**
   * Called just before parsing any lines from the solver output.
   */
  protected void prepareSolver() {}

  /**
   * Parses the last double value contained in the line.
   *
   * @param line line containing the double, typically the optimal objective value.
   * @return the parsed value
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
   * @param filename name of the file containing the linear program to be solved
   * @return the string used to execute the solver
   */
  protected abstract String getCommand(String filename);

  /**
   * Called for each line in the solvers output. This method must be overridden to parse the actual
   * results.
   *
   * @param line currently investigated line from the solvers output
   * @throws LinearProgramException if the line indicates the solver has failed
   */
  protected abstract void handleLine(String line) throws LinearProgramException;

  /**
   * Returns true if this solver is available on the command line. Availability is tested by solving
   * a tiny linear program.
   *
   * @return {@code true} if this solver can be used
   */
  private boolean isAvailable() {
    Integer opt = null;

    try {
      File file = File.createTempFile("simple-linear-program", ".lp");
      PrintWriter writer = new PrintWriter(file.getAbsoluteFile(), "UTF-8");

      writer.println("Minimize");
      writer.println("obj: x + y");
      writer.println("Subject To");
      writer.println("c1: x >= 2");
      writer.println("c2: y >= 1");
      writer.println("End");
      writer.close();

      opt = (int) solve(file.getAbsoluteFile().toString());
    } catch (LinearProgramException | IOException e) {
      // solver is not available
    }

    return opt != null && opt == 3;
  }
}
