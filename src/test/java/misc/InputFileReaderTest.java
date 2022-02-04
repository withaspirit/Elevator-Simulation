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
 * @author Ryan Dash, Brady, Liam Tripp
 */
public class InputFileReaderTest {

    ElevatorRequest elevatorRequest1;
    ElevatorRequest elevatorRequest2;
    JSONObject jsonObject;
    InputFileReader inputFileReader;
    JSONArray jsonArray;

    @BeforeEach
    void setUp() {
        inputFileReader = new InputFileReader();
    }

    @AfterEach
    void tearDown() {}

    /**
     * Initializes the JSONArray for a test to the "inputs" file.
     */
    private void initStandardInputArray() {
        jsonArray = initJSONArray("inputs");
    }

    /**
     * Initializes the JSONArray for a JSON file with the specified name.
     *
     * @return JSONArray the JSON file with the specified name converted to a JSON array
     */
    private JSONArray initJSONArray(String name) {
        ArrayList<ElevatorRequest> queue = inputFileReader.readInputFile(name);
        // Fill JSONArray with inputs
        return (JSONArray) inputFileReader.getJSONFileAsObject(name).get(name);
    }

    // Assert all inputs are in correct format
    @Test
    void testConvertJSONToString() {
        initStandardInputArray();

        for (Object object : jsonArray) {
            jsonObject = (JSONObject) object;
            String[] data = inputFileReader.convertJSONToStringArray(jsonObject);

            // Test that time is valid
            // This should just throw an exception if the format is invalid
            LocalTime time = LocalTime.parse(data[0]);

            // floorNumber is a valid number ( > 0)
            int floorNumber = Integer.parseInt(data[1]);
            assertTrue(floorNumber > 0);

            // Direction is Up or Down
            // (special cases: no down on first floor)
            Direction direction = Direction.getDirection(data[2]);
            assertNotNull(direction);
            String directionName = direction.getName();
            if (floorNumber == 1) {
                assertNotEquals(directionName, Direction.DOWN.getName());
            } else {
                assertTrue(directionName.equals(Direction.UP.getName()) ||
                        directionName.equals(Direction.DOWN.getName()));
            }

            // floorToVisit is a valid number ( > 0)
            int floorToVisit = Integer.parseInt(data[3]);
            assertTrue(floorToVisit > 0);
        }
    }

    @Test
    void testReadInputFileEquality() {
        // Fill queue with inputs
        ArrayList<ElevatorRequest> queue = inputFileReader.readInputFile("inputs");

        // Fill JSONArray with inputs
        initStandardInputArray();

        // make sure both return same number of inputs
        assertEquals(jsonArray.size(), queue.size());

        // Assure contents of each method is the same
        for (int i = 0; i < queue.size(); i++) {
            elevatorRequest1 = queue.get(i);
            jsonObject = (JSONObject) jsonArray.get(i);
            elevatorRequest2 = inputFileReader.createElevatorRequest(jsonObject);
            assertEquals(elevatorRequest1.toString(), elevatorRequest2.toString());
        }
    }

    @Test
    void inputFormatTest() {
        initStandardInputArray();

        // Event 1 -> "00:00:00.000 1 Up 2"

        jsonObject = (JSONObject) jsonArray.get(0);
        elevatorRequest1 = inputFileReader.createElevatorRequest(jsonObject);
        String[] data = ((String) jsonObject.get("event")).split(" ");

        assertEquals(LocalTime.MIDNIGHT, LocalTime.parse(data[0])); // midnight = 00:00:00.000
        assertEquals(1, Integer.parseInt(data[1]));
        assertEquals(Direction.UP, Direction.getDirection(data[2]));
        assertEquals(2, Integer.parseInt(data[3]));
    }
}