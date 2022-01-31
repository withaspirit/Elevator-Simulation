package misc;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * InputFileReader creates takes a given file name and returns the file as a usable object.
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
     * @param name name the name of the file (and optionally, its folder)
     * @return JSONObject a file as a JSONObject
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
     * Reads an input file containing that simulate passengers
     * making requests to travel up or down a floor using an elevator.
     *
     * @return an ArrayList containing string events for the FloorSubsystem
     */
    public ArrayList<String> readInputFile(){
        ArrayList<String> queue = new ArrayList<>();
        String[] data;
        JSONObject jsonObject = getJSONFileAsObject("inputs");
        for (Object event: jsonObject.keySet()){
            data = ((String)jsonObject.get(event)).split(" ");
            if (Integer.parseInt(data[1]) == 1 && data[2].equals("Down")){
                System.err.println("There is no down button on the First floor");
            }
            data[0] = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
            queue.add(String.join(" ", data));
        }
        return queue;
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

        InputFileReader inputFileReader = new InputFileReader();
        for (String string: inputFileReader.readInputFile()){
            System.out.println(string);
        }
    }
}
