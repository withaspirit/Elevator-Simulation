package misc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemwide.Direction;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for InputFileReader methods
 *
 * @author Ryan Dash, Brady
 */
public class InputFileReaderTest {

    ElevatorRequest elevatorRequest1;
    ElevatorRequest elevatorRequest2;
    JSONObject jsonObject;

    InputFileReader inputFileReader;

    @BeforeEach
    void setUp() {
        inputFileReader = new InputFileReader();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testReadInputFile() {
        // Fill queue with inputs
        ArrayList<ElevatorRequest> queue = inputFileReader.readInputFile("inputs");

        // Fill JSONArray with inputs
        JSONArray jsonArray = (JSONArray) inputFileReader.getJSONFileAsObject("inputs").get("inputs");

        // Event 1
        elevatorRequest1 = queue.get(0);
        jsonObject = (JSONObject) jsonArray.get(0);
        elevatorRequest2 = inputFileReader.createElevatorRequest(jsonObject);

        assertEquals(elevatorRequest1.toString(), elevatorRequest2.toString());

        // Event 2
        elevatorRequest1 = queue.get(1);
        jsonObject = (JSONObject) jsonArray.get(1);
        elevatorRequest2 = inputFileReader.createElevatorRequest(jsonObject);

        assertEquals(elevatorRequest1.toString(), elevatorRequest2.toString());

        // Event 3
        elevatorRequest1 = queue.get(2);
        jsonObject = (JSONObject) jsonArray.get(2);
        elevatorRequest2 = inputFileReader.createElevatorRequest(jsonObject);

        assertEquals(elevatorRequest1.toString(), elevatorRequest2.toString());

    }

    @Test
    void inputFormatTest() {
        // test all inputs in input file
        // LocalTime in proper format
        // floorNumber is a valid number ( > 0)
        // Direction is Up or Down
        //      (special cases: no down on first floor)
        //      (no way to know which is top floor so no test for that)
        //
        // Could create an inputs file just for testing incorrect inputs
        /*
        if (Integer.parseInt(data[1]) == 1 && data[2].equals("Down")){
            System.err.println("There is no down button on the First floor");
        }

         */
        // Create JSONArray
        JSONArray jsonArray = (JSONArray) inputFileReader.getJSONFileAsObject("inputs").get("inputs");

        // Event 1 -> "00:00:00.000 1 Up 2"

        jsonObject = (JSONObject) jsonArray.get(0);
        elevatorRequest1 = inputFileReader.createElevatorRequest(jsonObject);
        String[] data = ((String) jsonObject.get("event")).split(" ");

        assertEquals(LocalTime.MIDNIGHT, LocalTime.parse(data[0])); // midnight = 00:00:00.000
        assertEquals(1, Integer.parseInt(data[1]));
        assertEquals(Direction.UP, Direction.getDirection(data[2]));
        assertEquals(2, Integer.parseInt(data[3]));


    }

    void formatTest() {
        // request.toString = LocalTime.DateTimeFormatter("hh:mm:ss.mmm"); or something
    }
}