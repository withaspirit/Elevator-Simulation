package misc;

import systemwide.Direction;

import java.time.LocalTime;

/**
 * RequestFactory is a factory for creating requests.
 *
 * @author Liam Tripp
 */
public class RequestFactory {

    /**
     * Creates an ElevatorRequest from a given String array.
     *
     * @param data a String array containing information for the ElevatorRequest
     * @return elevatorRequest and
     */
    public ElevatorRequest createElevatorRequest(String[] data) {
        LocalTime time = LocalTime.parse(data[0]);
        int floorNumber = Integer.parseInt(data[1]);
        Direction direction = Direction.getDirection(data[2]);
        int floorToVisit = Integer.parseInt(data[3]);

        return new ElevatorRequest(time, floorNumber, direction, floorToVisit);
    }
}
