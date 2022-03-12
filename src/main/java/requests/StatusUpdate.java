package requests;

import elevatorsystem.MovementState;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;

public class StatusUpdate extends SystemEvent{
    private final double expectedTime;
    private final MovementState state;
    private final int currentFloor;
    private Direction direction;

    public StatusUpdate(double expectedTime, MovementState state, int currentFloor, Direction direction, int elevatorNumber) {
        super(LocalTime.now(), Origin.ELEVATOR_SYSTEM);
        this.expectedTime = expectedTime;
        this.state = state;
        this.currentFloor = currentFloor;
        this.direction = direction;
        setElevatorNumber(elevatorNumber);
    }

    /**
     * Returns the expected time it takes for the elevator to arrive
     *
     * @return an integer representing the expected time for the elevator to arrive
     */
    public Double getExpectedTime() {
        return expectedTime;
    }

    /**
     * Returns the state of the elevator
     *
     * @return the status of the elevator
     */
    public MovementState getState() {
        return state;
    }

    /**
     * Gets the elevator's current floor
     *
     * @return the elevator's current floor
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Gets the elevator's current direction.
     *
     * @return the elevator's current direction
     */
    public Direction getDirection() {
        return direction;
    }
}
