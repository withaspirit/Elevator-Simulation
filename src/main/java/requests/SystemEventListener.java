package requests;

/**
 * SystemEventListener is an interface for passing SystemEvents between
 * Subsystem components and their respective Subsystem.
 *
 * @author Liam Tripp
 */
public interface SystemEventListener {
    /**
     * Passes an ApproachEvent between a Subsystem component and the Subsystem.
     *
     * @param approachEvent the approach event for the system
     */
    void handleApproachEvent(ApproachEvent approachEvent);
}
