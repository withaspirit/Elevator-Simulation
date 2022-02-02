package misc;

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
        ArrayList<ElevatorRequest> queue = inputFileReader.readInputFile("inputs");
        assertEquals(queue.get(0),"hh:mm:ss.mmm 1 Up 2");
    }

    @Test
    void inputFormatTest() {
        // test all inputs in input file
        // LocalTime in proper format
        // floorNumber is a valid number ( > 0)
        // Direction is Up or Down (special cases: down on first floor)
        //      (no way to know which is top floor)
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