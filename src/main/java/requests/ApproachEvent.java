package requests;

import systemwide.Direction;

import java.time.LocalTime;

/**
 * ApproachEvent is a SystemEvent for when an Elevator is approaching a floor.
 * The elevatorCanStop variable indicates whether an elevator should stop at a floor.
 *
 * @author Liam Tripp
 */
public class ApproachEvent extends ServiceRequest {

    private int elevatorNumber;
    private boolean elevatorCanStop;

    public ApproachEvent(LocalTime time, int floorNumber, Direction direction, Thread origin, int elevatorNumber) {
        super(time, floorNumber, direction, origin);
        this.elevatorNumber = elevatorNumber;
        this.elevatorCanStop = false;
    }

    public int getElevatorNumber() {
        return elevatorNumber;
    }

    public boolean getElevatorStopStatus() {
        return elevatorCanStop;
    }

    public void allowElevatorStop() {
        elevatorCanStop = true;
    }
}
