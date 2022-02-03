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
		this(time, floorNumber, direction);
		this.elevatorNumber = elevatorNumber;
	}

	public FloorRequest(LocalTime time, int floorNumber, Direction direction) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
	}

	/**
	 * Creates a FloorRequest given an ElevatorRequest and an Elevator's Number.
	 *
	 * @param elevatorRequest a ServiceRequest for an Elevator made by someone on a Floor
	 * @param elevatorNumber the number of the elevator
	 * @return floorRequest a request for an elevator to visit a floor made by an input file
	 */
	public FloorRequest(ElevatorRequest elevatorRequest, int elevatorNumber) {
		this(elevatorRequest.getTime(), elevatorRequest.getDesiredFloor(),
				elevatorRequest.getDirection(), elevatorNumber);
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
