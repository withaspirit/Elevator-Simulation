package requests;

import systemwide.Origin;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * SystemEvent is a generic event that indicates the time an event occurred
 * and the event's origin.
 *
 * @author Liam Tripp
 */
public class SystemEvent implements Serializable {

    private LocalTime time;
    private Origin origin;
    private int elevatorNumber;

    /**
     * Constructor for SystemEvent.
     *
     * @param time the time the event occurred was made
     * @param origin the system from which the message originated
     */
    public SystemEvent(LocalTime time, Origin origin) {
        this.time = time;
        this.origin = origin;
        elevatorNumber = 0;
    }

    /**
     * Returns the time the request was made.
     *
     * @return LocalTime the time the request was made
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Changes the time for the SystemEvent.
     *
     * @param time the new time value for the SystemEvent
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * Returns Origin, a Origin representing the Runnable system from which the event came from.
     *
     * @return origin, the Runnable system representing the event's origin
     */
    public Origin getOrigin() {
        return origin;
    }

    /**
     * Changes the request's origin.
     *
     * @param origin represents the Runnable system from which the event came from
     */
    public void setOrigin(Origin origin) {
        this.origin = origin;
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
     * Sets the number of the elevator for the request.
     *
     * @param elevatorNumber the number of the elevator corresponding to the request
     */
    public void setElevatorNumber(int elevatorNumber) {
        this.elevatorNumber =  elevatorNumber;
    }
}
