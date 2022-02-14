package requests;

import java.time.LocalTime;

/**
 * System event is a generic event that indicates the time an event occurred
 * and the event's origin.
 *
 * @author Liam Tripp
 */
public class SystemEvent {

    private final LocalTime time;
    private Thread origin;

    /**
     * Constructor for SystemEvent.
     *
     * @param time the time the event occurred was made
     * @param origin the system from which the message originated
     */
    public SystemEvent(LocalTime time, Thread origin) {
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
     * Returns Origin, a Thread representing the Runnable system from which the event came from.
     *
     * @return origin, the Runnable system representing the event's origin
     */
    public Thread getOrigin() {
        return origin;
    }

    /**
     * Changes the request's origin.
     *
     * @param origin represents the Runnable system from which the event came from
     */
    public void setOrigin(Thread origin) {
        this.origin = origin;
    }
}
