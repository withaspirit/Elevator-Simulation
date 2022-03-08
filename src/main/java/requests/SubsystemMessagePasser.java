package requests;

import systemwide.UnboundedBuffer;
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
	 * @param buffer an UnboundedBuffer that holds SystemEvents
	 * @return true if request is successful, false otherwise
	 */
	default boolean sendMessage(SystemEvent event, UnboundedBuffer buffer, Origin origin) {
		System.out.println(origin + " sending: " + event.getClass() + " " + event);
		buffer.addLast(event, origin);
		return true;
	}

	/**
	 * Removes and returns an object from a buffer.
	 *
	 * @param buffer an UnboundedBuffer that holds SystemEvents
	 * @return the first SystemEvent from the buffer
	 */
	default SystemEvent receiveMessage(UnboundedBuffer buffer, Origin origin) {
		SystemEvent request = buffer.removeFirst(origin);
		System.out.println(origin + " received: "  + request.getClass() + " "+ request);
		return request;
	}
}
