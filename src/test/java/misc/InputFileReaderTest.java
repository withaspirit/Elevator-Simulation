package misc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for InputFileReader methods
 *
 * @author Ryan Dash
 */
public class InputFileReaderTest {

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
        // Expand upon this, ex do for all inputs
        ArrayList<ElevatorRequest> queue = inputFileReader.readInputFile("inputs");
        ElevatorRequest elevatorRequest1 = queue.get(0);

        JSONArray jsonArray = (JSONArray) inputFileReader.getJSONFileAsObject("inputs").get("inputs");
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        ElevatorRequest elevatorRequest2 = inputFileReader.createElevatorRequest(jsonObject);
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
    }

    void formatTest() {
        // request.toString = LocalTime.DateTimeFormatter("hh:mm:ss.mmm"); or something
    }
}