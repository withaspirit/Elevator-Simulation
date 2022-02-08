package misc;

/**
 * ServiceRequestListener allows ____ and ____ to respond to ServiceRequests.
 *
 * @author Liam Tripp
 */
public interface ServiceRequestListener {
	/**
	 * Adds an object to a buffer.
	 *
	 * @param request a ServiceRequest which holds a request
	 * @param buffer a BoundedBuffer which holds serviceRequests
	 * @return true if request is successful, false otherwise
	 */
	default boolean sendMessage(ServiceRequest request, BoundedBuffer buffer) {
		System.out.println(Thread.currentThread().getName() + " sending: " + request);
		buffer.addLast(request, Thread.currentThread());
		return true;
	}

	/**
	 * Removes and returns an object from a buffer.
	 *
	 * @param buffer a buffer which holds object
	 * @return object the first object in the buffer
	 */
	default ServiceRequest receiveMessage(BoundedBuffer buffer) {
		ServiceRequest request = buffer.removeFirst(Thread.currentThread());
		System.out.println(Thread.currentThread().getName() + " received: " + request);
		return request;
	}
}
