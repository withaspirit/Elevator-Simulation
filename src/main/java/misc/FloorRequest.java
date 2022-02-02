package misc;

import systemwide.Direction;

import java.time.LocalTime;

/**
 * FloorRequest is generated when a user presses a button in an elevator.
 * 
 * @author Liam Tripp
 *
 */
public class FloorRequest implements ServiceRequest {

	private LocalTime time;
	private int floorNumber; // floorToVisit
	private Direction direction;
	private int elevatorNumber;
	
	public FloorRequest(LocalTime time, int floorNumber, Direction direction, int elevatorNumber) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.elevatorNumber = elevatorNumber;
	}

	public FloorRequest(LocalTime time, int floorNumber, Direction direction) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
	}
	
	public int getElevatorNumber() {
		return elevatorNumber;
	}
	
	@Override
	public LocalTime getTime() {
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
