package requests;

import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * FloorRequest is an event data structure for when a user presses a
 * FloorButton in an elevator.
 *
 * @author Liam Tripp, Ramit Mahajan, Ryan Dash
 */
public class FloorRequest extends ServiceRequest {

    private final int elevatorNumber;

    /**
     * Constructor for FloorRequest read from an input file.
     *
     * @param time           the time the request was made
     * @param floorNumber    the number of the floor on which the request was made
     * @param direction      the direction selected by the user
     * @param elevatorNumber the number of the elevator taking the request
     * @param origin         the system from which the message originated
     */
    public FloorRequest(LocalTime time, int floorNumber, Direction direction, int elevatorNumber, Origin origin) {
        super(time, floorNumber, direction, origin);
        this.elevatorNumber = elevatorNumber;
    }

    /**
     * Constructor for FloorRequest given an ElevatorRequest and an Elevator's number.
     *
     * @param elevatorRequest a request for an Elevator made by someone on a Floor
     */
    public FloorRequest(ElevatorRequest elevatorRequest) {
        this(elevatorRequest.getTime(), elevatorRequest.getDesiredFloor(),
                elevatorRequest.getDirection(), elevatorRequest.getElevatorNumber(), elevatorRequest.getOrigin());
    }

    /**
     * Returns the number of the elevator servicing the request.
     *
     * @return elevatorNumber the number of the elevator corresponding to the request
     */
    public int getElevatorNumber() {
        return elevatorNumber;
    }

    /**
     * Convert FloorRequest to a String in the format:
     * "hh:mm:ss.mmm desiredFloor direction elevatorNumber"
     */
    @Override
    public String toString() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        String formattedDate = getTime().format(dateTimeFormat);
        return formattedDate + " " + getFloorNumber() + " " + getDirection().getName() + " " + elevatorNumber;
    }
}
