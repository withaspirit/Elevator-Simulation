package requests;

import systemwide.Origin;

import java.time.LocalTime;
import java.io.Serializable;

/**
 * SystemEvent is a generic event that indicates the time an event occurred
 * and the event's origin.
 *
 * @author Liam Tripp
 */
public class SystemEvent implements Serializable {

    private final LocalTime time;
    private Origin origin;

    /**
     * Constructor for SystemEvent.
     *
     * @param time the time the event occurred was made
     * @param origin the system from which the message originated
     */
    public SystemEvent(LocalTime time, Origin origin) {
        this.time = time;
        this.origin = origin;
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
}
