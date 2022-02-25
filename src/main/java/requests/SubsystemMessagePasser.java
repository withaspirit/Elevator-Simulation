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
	 *
	 * @param fromOrigin the origin which sent the message
	 * @param event a SystemEvent which holds a request
	 * @param buffer a BoundedBuffer which holds serviceRequest
	 * @param toOrigin the origin which the message is being sent
	 * @return true if request is successful, false otherwise
	 */
	default boolean sendMessage(Origin fromOrigin, SystemEvent event, BoundedBuffer buffer, Origin toOrigin) {
		System.out.println(fromOrigin + " sending: " + event);
		buffer.addLast(event, toOrigin);
		return true;
	}

	/**
	 * Removes and returns an object from a buffer.
	 *
	 * @param buffer a buffer which holds SystemEvents
	 * @return the first SystemEvent from the buffer
	 */
	default SystemEvent receiveMessage(BoundedBuffer buffer, Origin origin) {
		SystemEvent request = buffer.removeFirst(origin);
		System.out.println(origin + " received: " + request);
		return request;
	}
}
