package requests;

import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ApproachEvent is a SystemEvent that is sent by an Elevator to a Floor's ArrivalSensor when
 * an Elevator approaches a Floor.
 *
 * @author Liam Tripp
 */
public class ApproachEvent extends ServiceRequest {

    /**
     * Indicates whether an elevator should stop at a floor.
     */
    private boolean elevatorMayStop;

    private int floorToVisit;

    /**
     * Constructor for ApproachEvent.
     *
     * @param time the time elevator's approach began
     * @param floorNumber the number of the floor the elevator is approaching
     * @param direction the direction of the elevator
     * @param elevatorNumber the number of the elevator that created the event
     * @param origin the system from which the message originated
     */
    public ApproachEvent(LocalTime time, int floorNumber, Direction direction, int elevatorNumber, Origin origin) {
        super(time, floorNumber, direction, origin);
        this.elevatorMayStop = false;
        setElevatorNumber(elevatorNumber);
    }

    /**
     * Constructor for ApproachEvent using an ElevatorRequest.
     *
     * @param elevatorRequest the request for which the approachEvent is made
     * @param floorNumber the number of the floor the elevator is approaching
     * @param elevatorNumber the number of the Elevator servicing the ApproachEvent
     */
    public ApproachEvent(ElevatorRequest elevatorRequest, int floorNumber, int elevatorNumber) {
        this(elevatorRequest.getTime(), floorNumber,
                elevatorRequest.getDirection(), elevatorNumber, elevatorRequest.getOrigin());
        // Set the destination floor of the elevator
        floorToVisit = elevatorRequest.getDesiredFloor();
    }

    /**
     * Indicates whether an elevator is allowed to stop.
     *
     * @return true if the elevator may stop, false otherwise
     */
    public boolean elevatorMayStop() {
        return elevatorMayStop;
    }

    /**
     * Allows an elevator to stop.
     */
    public void allowElevatorStop() {
        elevatorMayStop = true;
    }

    /**
     * Gets the elevators floor to visit.
     *
     * @return int floorToVisit
     */
    public int getFloorToVisit() { return floorToVisit; }

    /**
     * Convert ApproachEvent to a String.
     */
    @Override
    public String toString() {
        String formattedString = "[Time, Elevator #, FloorNumber, ElevatorDirxn, ElevatorMayStop]:\n";
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        String formattedDate = getTime().format(dateTimeFormat);
        formattedString += formattedDate + " " + getElevatorNumber() + " " + getFloorNumber() + " " + getDirection().getName() + " " + elevatorMayStop;
        return formattedString;
    }
}
