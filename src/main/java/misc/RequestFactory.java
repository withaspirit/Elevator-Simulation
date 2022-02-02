package misc;

import systemwide.Direction;

import java.time.LocalTime;

/**
 * RequestFactory is a factory for creating ServiceRequests.
 *
 * @author Liam Tripp
 */
public class RequestFactory {

    /**
     * Creates an ElevatorRequest from a given String array.
     *
     * @param data a String array containing information for the ElevatorRequest
     * @return elevatorRequest a request for an elevator made from an input file
     */
    public ElevatorRequest createElevatorRequest(String[] data) {
        LocalTime time = LocalTime.parse(data[0]);
        int floorNumber = Integer.parseInt(data[1]);
        Direction direction = Direction.getDirection(data[2]);
        int floorToVisit = Integer.parseInt(data[3]);

        return new ElevatorRequest(time, floorNumber, direction, floorToVisit);
    }

    /**
     * Creates a FloorRequest given an ElevatorRequest and an Elevator's Number.
     *
     * @param elevatorRequest a ServiceRequest for an Elevator made by someone on a Floor
     * @param elevatorNumber the number of the elevator
     * @return floorRequest a request for an elevator to visit a floor made by an input file
     */
    public FloorRequest createFloorRequest(ElevatorRequest elevatorRequest, int elevatorNumber) {
        return new FloorRequest(elevatorRequest.getTime(), elevatorRequest.getDesiredFloor(),
                elevatorRequest.getDirection(), elevatorNumber);
    }
}
