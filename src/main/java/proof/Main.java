package proof;

import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONException;
import org.json.JSONObject;

import proof.exception.InvalidConfigurationException;
import proof.exception.InvalidProofException;
import proof.util.Config;
import proof.validator.MainValidator;

/**
 * Main class for running the application.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class Main {

  /**
   * Validates a proof for the crossing number of a single graph.
   *
   * @param args The command line arguments
   * @throws InvalidProofException if the claimed proof is invalid
   */
  public static void main(String[] args) throws InvalidProofException {
    try {
      Config.Create(args);
    } catch (InvalidConfigurationException e) {
      System.out.println(e.getMessage() + "\n");
      System.out.println(Config.usage);
      System.exit(1);
    }

    Config.get().logger.println(Config.get().report + "\n");

    try {
      String input = new String(Files.readAllBytes(Config.get().file));
      JSONObject main = new JSONObject(input);
      Config.get().logger.println("START VALIDATION\n");
      new MainValidator().validate(main);
      Config.get().logger.println("\nVALIDATION SUCCESSFULL");
    } catch (IOException | JSONException e) {
      System.out.println("Failed to read the requested file.");
      e.printStackTrace();
      System.exit(1);
    }
  }
}
