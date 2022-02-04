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

	public ServiceRequest(LocalTime time, int floorNumber, Direction direction  ) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
	}

	public LocalTime getTime() {
		return time;
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public Direction getDirection() {
		return direction;
	}

	/**
	 * Sets the Request's direction to the provided one.
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
