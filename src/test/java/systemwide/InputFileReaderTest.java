package systemwide;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorRequest;
import requests.SystemEvent;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for InputFileReader methods.
 *
 * @author Ryan Dash, Brady, Liam Tripp
 */
public class InputFileReaderTest {

    private ElevatorRequest elevatorRequest1;
    private ElevatorRequest elevatorRequest2;
    private JSONObject jsonObject;
    private InputFileReader inputFileReader;
    private JSONArray jsonArray;

    @BeforeEach
    void setUp() {
        inputFileReader = new InputFileReader();
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Initializes the JSONArray for a test to the "inputs" file.
     */
    private void initStandardInputArray() {
        jsonArray = inputFileReader.createJSONArray(InputFileReader.INPUTS_FILENAME);
    }

    // Assert all inputs are in correct format
    @Test
    void testConvertJSONToString() {
        initStandardInputArray();

        for (Object object : jsonArray) {
            jsonObject = (JSONObject) object;
            String[] data = ((String) jsonObject.get("event")).split(" ");

            // Test that time is valid
            // This should just throw an exception if the format is invalid

            // floorNumber is a valid number ( > 0)
            int floorNumber = Integer.parseInt(data[0]);
            assertTrue(floorNumber > 0);

            // Direction is Up or Down
            // (special cases: no down on first floor)
            Direction direction = Direction.getDirection(data[1]);
            assertNotNull(direction);
            String directionName = direction.getName();
            if (floorNumber == 1) {
                assertNotEquals(directionName, Direction.DOWN.getName());
            } else {
                assertTrue(directionName.equals(Direction.UP.getName()) ||
                        directionName.equals(Direction.DOWN.getName()));
            }

            // floorToVisit is a valid number ( > 0)
            int floorToVisit = Integer.parseInt(data[2]);
            assertTrue(floorToVisit > 0);
        }
    }

    @Test
    void testReadInputFileEquality() {
        // Fill queue with inputs
        ArrayList<SystemEvent> queue = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);

        // Fill JSONArray with inputs
        initStandardInputArray();

        // make sure both return same number of inputs
        assertEquals(jsonArray.size(), queue.size());

        // Assure contents of each method is the same
        int localTimePosition = "HH:mm:ss.SSS ".length();
        for (int i = 0; i < queue.size(); i++) {
            elevatorRequest1 = (ElevatorRequest) queue.get(i);
            jsonObject = (JSONObject) jsonArray.get(i);
            elevatorRequest2 = inputFileReader.createElevatorRequest(((String) jsonObject.get("event")).split(" "), LocalTime.now());
            assertEquals(elevatorRequest1.toString().substring(localTimePosition), elevatorRequest2.toString().substring(localTimePosition));
        }
    }

    @Test
    void inputFormatTest() {
        initStandardInputArray();

        // Event 1 -> "1 Up 2"
        jsonObject = (JSONObject) jsonArray.get(0);
        elevatorRequest1 = inputFileReader.createElevatorRequest(((String) jsonObject.get("event")).split(" "), LocalTime.now());
        String[] data = ((String) jsonObject.get("event")).split(" ");

        assertEquals(1, Integer.parseInt(data[0]));
        assertEquals(Direction.UP, Direction.getDirection(data[1]));
        assertEquals(2, Integer.parseInt(data[2]));
    }
}
