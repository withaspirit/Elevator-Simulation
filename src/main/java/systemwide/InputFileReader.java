package systemwide;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import requests.ElevatorRequest;
import requests.SystemEvent;

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
            String[] data;
            LocalTime time = LocalTime.now();
            if (inputObject.containsKey("event")) {
                data = ((String) inputObject.get("event")).split(" ");
                queue.add(createElevatorRequest(data, time));
            } else if (inputObject.containsKey("fault")) {
                data = ((String) inputObject.get("fault")).split(" ");
                queue.add(createFaultRequest(data, time));
            }
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
     * @param data a string array containing elevator request data.
     * @param time the current local time
     * @return an elevator request for an elevator made from the data and time
     */
    public ElevatorRequest createElevatorRequest(String[] data, LocalTime time) {
        int floorNumber = Integer.parseInt(data[0]);
        Direction direction = Direction.getDirection(data[1]);
        int floorToVisit = Integer.parseInt(data[2]);
        // FIXME: this is true only for origin
        return new ElevatorRequest(time, floorNumber, direction, floorToVisit, Origin.FLOOR_SYSTEM);
    }

    /**
     * Create an FaultRequest for a given string array
     *
     * @param data a string array containing fault request data.
     * @param time the current local time
     * @return a faultRequest for from the data and time
     */
    public SystemEvent createFaultRequest(String[] data, LocalTime time) {
        return new SystemEvent(time, Origin.FLOOR_SYSTEM);
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
