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
     * Adds a SystemEvent to a System's queue of events.
     *
     * @param systemEvent the SystemEvent to add
     */
    void addEventToQueue(SystemEvent systemEvent);

    /**
     * Receives and returns a Structure from the Scheduler.
     *
     * @return Structure contains information to initialize the floors and elevators
     */
    Structure receiveStructure();
}
