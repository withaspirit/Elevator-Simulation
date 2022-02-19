package requests;

import systemwide.BoundedBuffer;

/**
 * ServiceRequestListener allows ____ and ____ to respond to ServiceRequests.
 *
 * @author Liam Tripp, Ryan Dash
 */
public interface ServiceRequestListener {
	/**
	 * Adds an object to a buffer.
	 *
	 * @param event a SystemEvent which holds a request
	 * @param buffer a BoundedBuffer which holds serviceRequests
	 * @return true if request is successful, false otherwise
	 */
	default boolean sendMessage(SystemEvent event, BoundedBuffer buffer, Thread origin) {
		System.out.println(Thread.currentThread().getName() + " sending: " + event);
		buffer.addLast(event, origin);
		return true;
	}

	/**
	 * Removes and returns an object from a buffer.
	 *
	 * @param buffer a buffer which holds SystemEvents
	 * @return the first SystemEvent from the buffer
	 */
	default SystemEvent receiveMessage(BoundedBuffer buffer, Thread origin) {
		SystemEvent request = buffer.removeFirst(origin);
		System.out.println(Thread.currentThread().getName() + " received: " + request);
		return request;
	}
}
