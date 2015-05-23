package proof.util;

import java.io.PrintStream;

/**
 * Global logger. Keeps track of the progress of the validation.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class ProgressLogger {
  private int progress = 0;
  private int maxProgress = 1;
  private final boolean verbose;
  private static final int SIZE = 32;
  private final PrintStream out;

  /**
   * Creates a new logger.
   *
   * @param out output stream to use
   * @param verbose whether to print everything
   */
  public ProgressLogger(PrintStream out, boolean verbose) {
    this.out = out;
    this.verbose = verbose;
  }

  /**
   * Resets the progress to zero.
   *
   * @param maxProgress progress to reach until finished
   */
  public void reset(int maxProgress) {
    if (maxProgress < 1) {
      throw new IllegalArgumentException("Size must be greater zero.");
    }

    this.maxProgress = maxProgress;
    this.progress = 0;
  }

  /**
   * Prints a (multi-line) message even when {@link #verbose} is disabled.
   *
   * @param message message to be printed
   */
  public void println(String message) {
    out.println("\r" + message);
  }

  /**
   * Prints out a single line message. The message is skipped if {@link #verbose} is disabled.
   *
   * @param message line to be printed
   */
  public void print(String message) {
    if (verbose) {
      out.println("[" + String.format("%3d%%", (progress * 99) / maxProgress) + "] " + message);
    } else {
      out.print("\r["
          + String.format("%-" + SIZE + "s",
              new String(new char[(progress * SIZE) / maxProgress]).replace("\0", "=")) + "]");
    }
  }

  /**
   * Increases the current progress by one and prints a single line message.
   *
   * @param message line to be printed
   */
  public void progress(String message) {
    progress();
    print(message);
  }

  /**
   * Increases the current progress.
   */
  public void progress() {
    progress++;

    if (progress > maxProgress) {
      throw new IllegalArgumentException("Maximum progress already reached (" + maxProgress + ").");
    }

    if (!verbose) {
      print("");
    }
  }
}
