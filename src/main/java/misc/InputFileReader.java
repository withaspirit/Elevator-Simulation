package misc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * InputFileReader creates takes a given file name and returns the file as a usable object.
 *
 * @author Liam Tripp
 */
public class InputFileReader {

    /**
     * Constructor for InputFileReader.
     */
    public InputFileReader() {

    }

    /**
     * Returns a JSON file with the specified name as a JSONObject.
     *
     * @param name name the name of the file (and optionally, its folder)
     * @return JSONObject a file as a JSONObject
     */
    public JSONObject getJSONFileAsObject(String name) {
        try {
            InputStreamReader inputStreamReader = createInputStreamReader(name + ".json");
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(inputStreamReader);
            return (JSONObject) obj;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates an InputStreamReader for a file with the specified name.
     * Prepend "[file's package]/" if necessary.
     *
     * @param name the name of the file (and optionally, its folder)
     * @return inputStreamReader an InputStreamReader for the file
     */
    private InputStreamReader createInputStreamReader(String name) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(name);
            assert inputStream != null;
            // Specify CharSet as UTF-8
            return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        } catch(Exception e) {
            System.err.println(name + " was not found.");
            e.printStackTrace();
            return null;
        }
    }
}
