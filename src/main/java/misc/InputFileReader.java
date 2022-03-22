package misc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import requests.ElevatorRequest;
import requests.SystemEvent;
import systemwide.Direction;
import systemwide.Origin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * InputFileReader takes a given file name and returns the file as a usable object.
 *
 * @author Liam Tripp, Ryan Dash
 */
public class InputFileReader {

    public static final String INPUTS_FILENAME = "inputs";

    /**
     * Constructor for InputFileReader (No-args for now).
     */
    public InputFileReader() {
    }

    /**
     * Reads an input file containing events that simulate passengers
     * making requests to travel up or down a floor using an elevator.
     *
     * @param name the name of the file for reading inputs
     * @return an ArrayList containing ElevatorRequests for the FloorSubsystem
     */
    public ArrayList<SystemEvent> readInputFile(String name) {
        ArrayList<SystemEvent> queue = new ArrayList<>();
        JSONArray jsonArray = createJSONArray(name);

        for (Object obj : jsonArray) {
            JSONObject inputObject = (JSONObject) obj;
            ElevatorRequest elevatorRequest = createElevatorRequest(inputObject);
            queue.add(elevatorRequest);
        }
        return queue;
    }

    /**
     * Initializes a JSONArray for a JSON file with the specified name.
     *
     * @return JSONArray the JSON file converted to a JSON array
     */
    public JSONArray createJSONArray(String name) {
        return (JSONArray) getJSONFileAsObject(name).get(name);
    }

    /**
     * Returns a JSON file with the specified name as a JSONObject.
     *
     * @param name the name of the file (and optionally, its folder)
     * @return JSONObject a JSON file as a JSONObject
     */
    public JSONObject getJSONFileAsObject(String name) {
        try {
            InputStreamReader inputStreamReader = createInputStreamReader(name + ".json");
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(inputStreamReader);
            return (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates an ElevatorRequest from a given String array.
     *
     * @param jsonObject a ServiceRequest as a JSONObject
     * @return elevatorRequest a request for an elevator made from an input file
     */
    public ElevatorRequest createElevatorRequest(JSONObject jsonObject) {
        String[] data = convertJSONToStringArray(jsonObject);
        LocalTime time = LocalTime.parse(data[0]);
        int floorNumber = Integer.parseInt(data[1]);
        Direction direction = Direction.getDirection(data[2]);
        int floorToVisit = Integer.parseInt(data[3]);
        // FIXME: this is true only for origin
        return new ElevatorRequest(time, floorNumber, direction, floorToVisit, Origin.FLOOR_SYSTEM);
    }

    /**
     * Converts a JSONObject to a String array in the format:
     * "hh:mm:ss.mmm CurrentFloorNumber Direction DesiredFloorNumber".
     *
     * @param jsonObject a ServiceRequest as a JSONObject
     * @return a String array containing information in the specified format
     */
    public String[] convertJSONToStringArray(JSONObject jsonObject) {
        return ((String) jsonObject.get("event")).split(" ");
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
        } catch (Exception e) {
            System.err.println(name + " was not found.");
            e.printStackTrace();
            return null;
        }
    }
}
