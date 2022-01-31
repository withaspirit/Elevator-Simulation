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
        assertEquals(queue.get(1),"14:05:15.0 2 Up 4");
    }

    public static void main(String[] args) {
//        JSONObject queue = new JSONObject();
//        queue.put("1", "hh:mm:ss.mmm 1 Up 2");
//        queue.put("2", "14:05:15.0 2 Up 4");
//
//        try (FileWriter fileWriter = new FileWriter("inputs.json")){
//            fileWriter.write(queue.toJSONString());
//            fileWriter.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}