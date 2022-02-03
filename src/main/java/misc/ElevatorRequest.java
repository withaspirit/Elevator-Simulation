package misc;

import systemwide.Direction;

import java.time.LocalTime;

public class ElevatorRequest implements ServiceRequest {
	
	private LocalTime time;
	private int floorNumber;
	private Direction direction;
	private int desiredFloor;

	public ElevatorRequest(LocalTime time, int floorNumber, Direction direction, int desiredFloor) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.desiredFloor = desiredFloor;
	}

	public ElevatorRequest(LocalTime time, int floorNumber, Direction direction) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
	}
	
	public int getDesiredFloor() {
		return desiredFloor;
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
