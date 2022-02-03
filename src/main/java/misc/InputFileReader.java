package misc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.runner.Request;
import systemwide.Direction;

/**
 * InputFileReader creates takes a given file name and returns the file as a usable object.
 *
 * @author Liam Tripp, Ryan Dash
 */
public class InputFileReader {

    private RequestFactory requestFactory;

    /**
     * Constructor for InputFileReader.
     */
    public InputFileReader() {
        this.requestFactory = new RequestFactory();
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
     * Reads an input file containing events that simulate passengers
     * making requests to travel up or down a floor using an elevator.
     *
     * @param name the name of the file for reading inputs
     * @return an ArrayList containing ElevatorRequests for the FloorSubsystem
     */
    public ArrayList<ElevatorRequest> readInputFile(String name){
        ArrayList<ElevatorRequest> queue = new ArrayList<>();
        JSONObject jsonObject = getJSONFileAsObject(name);
        JSONArray jsonArray = (JSONArray) jsonObject.get("inputs");
        String[] data;

        for (Object obj: jsonArray) {
            JSONObject inputObject = (JSONObject) obj;
            data = convertJSONToStringArray(inputObject);
            ElevatorRequest elevatorRequest = requestFactory.createElevatorRequest(data);
            queue.add(elevatorRequest);
        }
        return queue;
    }

    /**
     * Converts a JSONObject to a String array in the format:
     * "hh:mm:ss.mmm CurrentFloorNumber Direction DesiredFloorNumber".
     *
     * @param jsonObject the input object as a JSONObject
     * @return a String array containing information in the specified format
     */
    public String[] convertJSONToStringArray(JSONObject jsonObject) {
        return ((String) jsonObject.get("event")).split(" ");
    }

}
