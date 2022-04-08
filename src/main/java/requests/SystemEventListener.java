package requests;

import systemwide.Structure;

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
     * @param approachEvent the ApproachEvent for the system
     */
    void handleApproachEvent(ApproachEvent approachEvent);

    /**
     * Receives and returns a Structure from the Scheduler.
     *
     * @return Structure contains information to initialize the floors and elevators
     */
    Structure receiveStructure();
}
