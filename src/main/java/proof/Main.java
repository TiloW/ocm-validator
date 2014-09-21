package proof;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

import proof.exception.InvalidProofException;
import proof.validator.MainValidator;

public class Main {

  public static void main(String[] args) throws IOException, InvalidProofException {
    String input =
        new String(Files.readAllBytes(Paths.get("/home/tilo/workspace/ocm-logger/log.json")));
    JSONObject main = new JSONObject(input);
    new MainValidator().validate(main);
  }
}
