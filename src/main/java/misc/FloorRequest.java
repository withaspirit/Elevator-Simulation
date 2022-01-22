package misc;

import systemwide.Direction;

/**
 * FloorRequest is generated when a user presses a button in an elevator.
 * 
 * @author Liam Tripp
 *
 */
public class FloorRequest implements ServiceRequest {

	private int time;
	private int floorNumber; // floorToVisit
	private Direction direction;
	private int elevatorNumber;
	
	public FloorRequest(int time, int floorNumber, Direction direction, int elevatorNumber) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
	}
	
	public int getElevatorNumber() {
		return elevatorNumber;
	}
	
	@Override
	public int getTime() {
		return time;
	}

	@Override
	public int getFloorNumber() {
		return floorNumber;
	}

	@Override
	public Direction getDirection() {
		return direction;
	}
}
