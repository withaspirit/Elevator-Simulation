package requests;

import elevatorsystem.MovementState;

import java.time.LocalTime;

public class StatusResponse extends Requests{
    private double expectedTime;
    private MovementState status;

    public StatusResponse(LocalTime currentTime, Thread currentThread, double expectedTime, MovementState status) {
        super(currentTime, currentThread);
        this.expectedTime = expectedTime;
        this.status = status;
    }

    /**
     * Returns the status of the elevator
     *
     * @return the status of the elevator
     */
    public MovementState getStatus() {
        return status;
    }

    /**
     * Returns the expected time it takes for the elevator to arrive
     *
     * @return an integer representing the expected time for the elevator to arrive
     */
    public Double getExpectedTime() {
        return expectedTime;
    }
}
