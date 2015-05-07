package proof.util;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import proof.exception.ExceptionHelper;
import proof.exception.InvalidConfigurationException;
import proof.exception.UnsupportedSolverException;
import proof.solver.Solver;
import proof.solver.SolverFactory;

/**
 * Singleton configuration object. Parses command line arguments and provides global information.
 *
 * @author Tilo Wiedera
 */
public class Config {
  private static Config config = null;

  /**
   * Usage synopsis to be given to the user.
   */
  public static final String usage = "SYNOPSIS\n" + "  validator -f <file> [-v] [-s <solver>]\n\n"
      + "OPTIONS\n\n" + "  -f <file>, --file <file>\n"
      + "\tValidate the proof contained in <file>.\n\n" + "  -v, --verbose\n"
      + "\tPrint verbose information during validation.\n\n" + "  -s <solver>, --solver <solver>\n"
      + "\tUse <solver> as the linear program solver for validating lower bounds.\n"
      + "\tValid choices are {scip,cplex,gurobi}.";

  /**
   * Whether verbose mode is enabled. Instead of accessing this field directly, the {@link #logger}
   * should be utilized for all output.
   */
  public final boolean verbose;

  /**
   * The currently used linear program solver.
   */
  public final Solver solver;

  /**
   * A synopsis of the current configuration.
   */
  public final String report;

  /**
   * The file to be validated.
   */
  public final Path file;

  /**
   * The global output stream. Equals {@code System.out} if {@link #verbose} is set to {@code true}.
   */
  public final PrintStream logger;

  /**
   * Creates a new configuration. Must be called exactly once.
   *
   * @param args The command line arguments as given to the main method.
   * @throws InvalidConfigurationException If any arguments do not comply with the {@link #usage}.
   */
  public static void Create(String[] args) throws InvalidConfigurationException {
    if (Config.config != null) {
      throw new RuntimeException("Configuration has already been initialized.");
    }

    Config.config = new Config(args);
  }

  /**
   * Returns the configuration. Assumes that {@link #Create(String[])} has already been called.
   *
   * @return The configuration instance
   */
  public static Config get() {
    if (Config.config == null) {
      throw new RuntimeException("Configuration not yet initialized.");
    }

    return Config.config;
  }

  /**
   * Initializes a new configuration based on the given command line arguments.
   *
   * @param args The command line arguments as given to the main method.
   * @throws InvalidConfigurationException If any arguments do not comply with the {@link #usage}.
   */
  Config(String[] args) throws InvalidConfigurationException {
    boolean finalVerbose = false;
    String finalSolver = null;
    String finalFile = null;

    for (int i = 0; i < args.length; i++) {
      switch (args[i].trim()) {
        case "-v":
        case "--verbose":
          if (finalVerbose) {
            throw new InvalidConfigurationException("Duplicate argument given: " + args[i]);
          }
          finalVerbose = true;
          break;

        case "-s":
        case "--solver":
          if (i == args.length - 1) {
            throw new InvalidConfigurationException("No solver specified for " + args[i]);
          } else {
            if (finalSolver != null) {
              throw new InvalidConfigurationException("Duplicate argument given: " + args[i]);
            }

            finalSolver = args[++i];
          }
          break;

        case "-f":
        case "--file":
          if (i == args.length - 1) {
            throw new InvalidConfigurationException("No file specified for " + args[i]);
          } else {
            if (finalFile != null) {
              throw new InvalidConfigurationException("Duplicate argument given: " + args[i]);
            }

            finalFile = args[++i];
          }
          break;

        default:
          throw new InvalidConfigurationException("Unknown command line parameter: " + args[i]);
      }
    }

    if (finalFile == null) {
      throw new InvalidConfigurationException("No input file specified");
    }

    File f = new File(finalFile);
    boolean fileExists = f.exists() && !f.isDirectory();

    if (!fileExists) {
      throw new InvalidConfigurationException("File does not exist: " + finalFile);
    } else {
      file = Paths.get(finalFile);
    }

    try {
      solver = new SolverFactory().getSolver(finalSolver);
    } catch (IllegalArgumentException | UnsupportedSolverException e) {
      throw ExceptionHelper.wrap(e, new InvalidConfigurationException(
          finalSolver == null ? "No linear program solver available."
              : (finalSolver + " is not available on this system.")));
    }

    verbose = finalVerbose;
    report = getReport();

    logger = verbose ? System.out : new PrintStream(new OutputStream() {
      @Override
      public void write(int b) {}
    });
  }

  /**
   * Returns a summary of all set options.
   */
  private String getReport() {
    return "CONFIGURATION\n\n  verbose: " + verbose + "\n  linear program solver: "
        + solver.getClass().getSimpleName() + "\n  file to validate: " + file;
  }
}
