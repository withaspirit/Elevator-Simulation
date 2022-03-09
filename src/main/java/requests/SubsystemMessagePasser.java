package requests;

import systemwide.BoundedBuffer;
import systemwide.Origin;

/**
 * SubsystemMessagePasser allows ____ and ____ to respond to ServiceRequests.
 *
 * @author Liam Tripp, Ryan Dash
 */
public interface SubsystemMessagePasser {
	/**
	 * Adds an object to a buffer.
	 *
	 * @param event a SystemEvent which holds a request
	 * @param buffer a BoundedBuffer which holds serviceRequests
	 * @param origin the origin of the system trying to send a message
	 * @return true if request is successful, false otherwise
	 */
	default boolean sendMessage(SystemEvent event, BoundedBuffer buffer, Origin origin) {
		if (!(event instanceof ApproachEvent && origin == Origin.SCHEDULER)) {
			System.out.println(origin + " sending: " + event.getClass().getCanonicalName() + " " + event);
		}
		buffer.addLast(event, origin);
		return true;
	}

	/**
	 * Removes and returns an object from a buffer.
	 *
	 * @param buffer a buffer which holds SystemEvents
	 * @param origin the origin of the system trying to receive a message
	 * @return the first SystemEvent from the buffer
	 */
	default SystemEvent receiveMessage(BoundedBuffer buffer, Origin origin) {
		SystemEvent event = buffer.removeFirst(origin);
		if (!(event instanceof ApproachEvent && origin == Origin.SCHEDULER)) {
			System.out.println(origin + " received: "  + event.getClass().getCanonicalName() + " " + event);
		}
		return event;
	}
}
