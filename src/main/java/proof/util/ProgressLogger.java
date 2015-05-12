package proof.util;

import java.io.PrintStream;

/**
 * Global logger. Keeps track of the progress of the validation.
 *
 * @author Tilo Wiedera
 */
public class ProgressLogger {
  private int progress = 0;
  private int maxProgress = 1;
  private String lastMessage = null;
  private final boolean verbose;
  private static final int SIZE = 32;
  private final PrintStream out;

  /**
   * Creates a new logger.
   *
   * @param out The output stream to use
   * @param verbose Whether to print everything
   */
  public ProgressLogger(PrintStream out, boolean verbose) {
    this.out = out;
    this.verbose = verbose;
  }

  /**
   * Resets the progress to zero.
   *
   * @param maxProgress The progress to reach until finished
   */
  public void reset(int maxProgress) {
    if (maxProgress < 1) {
      throw new IllegalArgumentException("Size must be greater zero.");
    }

    this.maxProgress = maxProgress;
    this.progress = 0;
  }

  /**
   * Prints a message even when {@link #verbose} is disabled.
   *
   * @param message The message to be printed
   */
  public void println(String message) {
    lastMessage = null;
    out.println("\r" + message);
  }

  /**
   * Prints out a single line message. The message is skipped if {@link #verbose} is disabled.
   *
   * @param message The line to be printed
   */
  public void print(String message) {
    if (verbose) {
      if (lastMessage != null) {
        out.print("\r" + String.format("%" + (lastMessage.length() + 6) + "s", ""));
      }

      out.println("[" + String.format("%3d%%", (progress * 99) / maxProgress) + "] "
          + (message == null ? "" : message));
    } else {
      out.print("\r["
          + String.format("%-" + SIZE + "s",
              new String(new char[(progress * SIZE) / maxProgress]).replace("\0", "=")) + "]");
    }
  }

  /**
   * Increases the current progress by one and prints a single line message.
   *
   * @param message The line to be printed
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
      print(lastMessage);
    }
  }
}
