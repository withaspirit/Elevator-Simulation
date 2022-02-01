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
        ArrayList<String> queue = inputFileReader.readInputFile();
        assertEquals(queue.get(0),"hh:mm:ss.mmm 1 Up 2");
    }
}