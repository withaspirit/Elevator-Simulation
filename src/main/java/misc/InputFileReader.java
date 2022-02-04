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
 * InputFileReader takes a given file name and returns the file as a usable object.
 *
 * @author Liam Tripp, Ryan Dash
 */
public class InputFileReader {

    /**
     * Constructor for InputFileReader.
     */
    public InputFileReader() {
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
     * @param name the name of the file (and optionally, its folder)
     * @return JSONObject a JSON file as a JSONObject
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

        for (Object obj: jsonArray) {
            JSONObject inputObject = (JSONObject) obj;
            ElevatorRequest elevatorRequest = createElevatorRequest(inputObject);
            queue.add(elevatorRequest);
        }
        return queue;
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

        return new ElevatorRequest(time, floorNumber, direction, floorToVisit);
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
}
