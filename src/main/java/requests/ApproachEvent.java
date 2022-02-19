package requests;

import systemwide.Direction;

import java.time.LocalTime;

/**
 * ApproachEvent is a SystemEvent that is sent by an Elevator to a Floor's ArrivalSensor when
 * an Elevator approaches a Floor.
 *
 * @author Liam Tripp
 */
public class ApproachEvent extends ServiceRequest {

    private final int elevatorNumber;
    /**
     * Indicates whether an elevator should stop at a floor
     */
    private boolean elevatorMayStop;

    /**
     * Constructor for ApproachEvent.
     *
     * @param time the time elevator's approach began
     * @param floorNumber the number of the floor the elevator is approaching
     * @param direction the direction of the elevator
     * @param elevatorNumber the number of the elevator that created the event
     * @param origin the system from which the message originated
     */
    public ApproachEvent(LocalTime time, int floorNumber, Direction direction, int elevatorNumber, Thread origin) {
        super(time, floorNumber, direction, origin);
        this.elevatorNumber = elevatorNumber;
        this.elevatorMayStop = false;
    }

    /**
     * Constructor for ApproachEvent using an ElevatorRequest.
     *
     * @param elevatorRequest the request for which the approachEvent is made
     * @param elevatorNumber the number of the Elevator servicing the ApproachEvent
     */
    public ApproachEvent(ElevatorRequest elevatorRequest, int elevatorNumber) {
        this(elevatorRequest.getTime(), elevatorRequest.getDesiredFloor(),
                elevatorRequest.getDirection(), elevatorNumber, Thread.currentThread());
    }

    /**
     * Returns the number of the Elevator that created the event.
     *
     * @return number the number of the elevator that created the event
     */
    public int getElevatorNumber() {
        return elevatorNumber;
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
}
