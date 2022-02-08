package misc;

import systemwide.Direction;

import java.time.LocalTime;

/**
 * ServiceRequest is an abstract event data structure for when a user requests an
 * Elevator's service.
 *
 * @author Liam Tripp, Ramit Mahajan
 */
public class ServiceRequest {

	private final LocalTime time;
	private final int floorNumber;
	private Direction direction;
	private Thread origin;

	/**
	 * Constructor for ServiceRequest.
	 *
	 * @param time the time the Request was made
	 * @param floorNumber the number of the floor on which the request was made
	 * @param direction the direction selected by the user
	 * @param origin the system from which the message originated
	 */
	public ServiceRequest(LocalTime time, int floorNumber, Direction direction, Thread origin) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.origin = origin;
	}

	/**
	 * Constructor for ServiceRequest.
	 *
	 * @param time the time the Request was made
	 * @param floorNumber the number of the floor on which the request was made
	 * @param direction the direction selected by the user
	 */
	public ServiceRequest(LocalTime time, int floorNumber, Direction direction) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
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
	 * Returns the number of the floor the request was made on.
	 *
	 * @return number of the floor the request was made on
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * Returns the direction indicated by the request.
	 *
	 * @return direction indicated by the request
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Sets the Request's direction to the provided one.
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Returns Origin, an enum representing the Runnable system from which the request came from.
	 *
	 * @return origin, the Runnable system representing the request's origin
	 */
	public Thread getOrigin() {
		return origin;
	}

	/**
	 * Changes the request's origin.
	 *
	 * @param origin an enum representing the Runnable system from which the request came from
	 */
	public void setOrigin(Thread origin) {
		this.origin = origin;
	}
}
