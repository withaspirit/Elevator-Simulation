package misc;

import systemwide.Direction;

public class ElevatorRequest implements ServiceRequest {
	
	private int time;
	private int floorNumber;
	private Direction direction;
	private int desiredFloor;
	
	public ElevatorRequest(int time, int floorNumber, Direction direction, int desiredFloor) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.desiredFloor = desiredFloor;
	}
	
	public int getDesiredFloor() {
		return desiredFloor;
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
