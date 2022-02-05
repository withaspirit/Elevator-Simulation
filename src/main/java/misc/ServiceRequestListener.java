package misc;

/**
 * ServiceRequestListener allows ____ and ____ to respond to ServiceRequests.
 *
 * @author Liam Tripp, Ryan Dash
 */
public interface ServiceRequestListener {

	/**
	 * Adds an object to a buffer.
	 *
	 * @param object an arbitrary object (a Request for this implementation)
	 * @param buffer a BoundedBuffer which holds the object
	 * @return true if request is successful, false otherwise
	 */
	default boolean sendMessage(Object object, BoundedBuffer buffer) {
		System.out.println(Thread.currentThread().getName() + " sending: " + object);
		buffer.addLast((ServiceRequest) object);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		return true;
	}

	/**
	 * Removes and returns an object from a buffer.
	 *
	 * @param buffer a buffer which holds object
	 * @return object the first object in the buffer
	 */
	default Object receiveMessage(BoundedBuffer buffer) {
		Object object = buffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received: " + object);

		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
		}
		return object;
	}
}
