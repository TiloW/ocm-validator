package proof;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

import proof.exception.InvalidProofException;
import proof.validator.MainValidator;

/**
 * Main class for running the application.
 *
 * @author Tilo Wiedera
 *
 */
public class Main {

  public static void main(String[] args) throws IOException, InvalidProofException {
    Path filepath = null;

    try {
      filepath = Paths.get(args[0]);
    } catch (RuntimeException e) {
      System.err.println("You need to specify a valid file as the first argument");
      System.exit(1);
    }

    String input = new String(Files.readAllBytes(filepath));

    JSONObject main = new JSONObject(input);
    new MainValidator().validate(main);

    System.out.println("Validation succeeded!");
  }
}
