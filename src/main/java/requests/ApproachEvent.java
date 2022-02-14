package requests;

import systemwide.Direction;

import java.time.LocalTime;

/**
 * ApproachEvent is a SystemEvent for when an Elevator is approaching a floor.
 *
 * @author Liam Tripp
 */
public class ApproachEvent extends ServiceRequest {

    private int elevatorNumber;
    /**
     * Indicates whether an elevator should stop at a floor
     */
    private boolean elevatorCanStop;

    /**
     * Constructor for ApproachEvent.
     *
     * @param time the time the Request was made
     * @param floorNumber the number of the floor on which the request was made
     * @param direction the direction selected by the user
     * @param origin the system from which the message originated
     * @param elevatorNumber the number of the elevator to which the
     */
    public ApproachEvent(LocalTime time, int floorNumber, Direction direction, Thread origin, int elevatorNumber) {
        super(time, floorNumber, direction, origin);
        this.elevatorNumber = elevatorNumber;
        this.elevatorCanStop = false;
    }

    /**
     * Returns the number corresponding to the Elevator for which this event occurred.
     *
     * @return number the number of the elevator
     */
    public int getElevatorNumber() {
        return elevatorNumber;
    }

    /**
     * Indicates whether an elevator is allowed to stop.
     *
     * @return true if the elevator is allowed to stop, false otherwise
     */
    public boolean getElevatorStopStatus() {
        return elevatorCanStop;
    }

    /**
     * Allows an elevator to stop.
     */
    public void allowElevatorStop() {
        elevatorCanStop = true;
    }
}
