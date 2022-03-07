package elevatorsystem;

import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.ArrayList;
/**
 * Elevator is a model for simulating an elevator.
 *
 * Requirements:
 * 1. Can move between floors
 * 2. Only services one elevator shaft of a structure
 * 3. Has speed
 * 4. Can stop at floors
 * 5. Knows its own location
 * 6. Takes time for elevator to move
 * 7. travels at SPEED to traverse FLOOR HEIGHT per second
 *
 * @author Liam Tripp, Brady Norton
 */
public class Elevator implements Runnable, SubsystemPasser {

	// Elevator Properties
	private final int elevatorNumber;
	private final ElevatorMotor motor;
	private FloorsQueue floorsQueue;
	private ElevatorSubsystem elevatorSubsystem;
	private int currentFloor;
	private float speed;
	private float displacement;

	private volatile ApproachEvent approachEvent;

	/**
	 * Constructor for Elevator class
	 * Instantiates subsystem, currentFloor, speed, displacement, and status
	 *
	 * @param elevatorNumber the number of the elevator
	 */
	public Elevator(int elevatorNumber, BoundedBuffer buffer) {
		this.elevatorNumber = elevatorNumber;
		motor = new ElevatorMotor();
		floorsQueue = new FloorsQueue();
		elevatorSubsystem = new ElevatorSubsystem(buffer, this, motor, floorsQueue);
		new Thread (elevatorSubsystem, elevatorSubsystem.getClass().getSimpleName()).start();
		speed = 0;
		displacement = 0;
		currentFloor = 0;
	}

	/**
	 * Checks if there are any more requests to process and processes
	 * and new requests
	 */
	@Override
	public void run() {
		while(true){
			if (motor.isActive()) {
				int nextFloor = floorsQueue.visitNextFloor(motor.getDirection());
				ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), currentFloor, motor.getDirection(), nextFloor, Origin.ELEVATOR_SYSTEM);
				currentFloor = nextFloor;
				System.out.println("Elevator#"+ elevatorNumber +" arrived at floor#:" + currentFloor);
				if (floorsQueue.isUpqueueEmpty() && floorsQueue.isDownqueueEmpty() && floorsQueue.isMissedqueueEmpty()){
					motor.setMovementState(MovementState.IDLE);
					motor.setDirection(Direction.NONE);
				}
			}
		}
	}

	/**
	 * Returns the elevator number
	 *
	 * @return an integer corresponding to the elevator's number
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}

	/**
	 * Gets the current floor the elevator is on
	 *
	 * @return the current floor as an int
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * Sets the currentFloor that the elevator is on
	 *
	 * @param currentFloor the floor to set the elevator on
	 */
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	/**
	 * Gets the speed of the elevator
	 *
	 * @return the speed as a float
	 */
	public float getSpeed(){
		return speed;
	}

	/**
	 * Sets the speed of the elevator
	 *
	 * @param speed the speed of the elevator
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Gets the displacement that the elevator has moved on the current floor
	 *
	 * @return displacement of elevator as float
	 */
	public float getFloorDisplacement(){
		return displacement;
	}

	/**
	 * Sets the displacement the elevator for the current floor
	 *
	 * @param displacement the displacement of the elevator as a float
	 */
	public void setFloorDisplacement(float displacement) {
		this.displacement = displacement;
	}

	/**
	 * TEMPORARY method to get the attached elevator Subsystem from elevator.
	 *
	 * @return the elevatorSubsystem attached to the elevator
	 */
	public ElevatorSubsystem getElevatorSubsystem() {
		return elevatorSubsystem;
	}

	/**
	 * Moves elevator to a floor, sending ApproachEvents just before a floor
	 * is reached if allowed.
	 *
	 * @param serviceRequest the request for which the elevator will move to
	 */
	public void moveToFloor(ServiceRequest serviceRequest) {
		// Set time of request
		// Request Properties
		if (serviceRequest instanceof ElevatorRequest elevatorRequest) {
			// Set floor of request
			int requestFloor = elevatorRequest.getDesiredFloor();

			// Set direction of request
			Direction requestedDirection = elevatorRequest.getDirection();

			if (floorsQueue.isDownqueueEmpty() && floorsQueue.isUpqueueEmpty()) {
				motor.setDirection(elevatorRequest.getDirection());
			}
			floorsQueue.addFloor(elevatorRequest.getFloorNumber(), currentFloor, elevatorRequest.getDesiredFloor(), elevatorRequest.getDirection());
			motor.setMovementState(MovementState.ACTIVE);

			while (currentFloor != requestFloor) {
				int nextFloor = motor.move(currentFloor, requestFloor, requestedDirection);
				ApproachEvent newApproachEvent = new ApproachEvent(elevatorRequest.getTime(), nextFloor,
						elevatorRequest.getDirection(), elevatorNumber, Origin.ELEVATOR_SYSTEM);
				passApproachEvent(newApproachEvent);
				// stall while waiting to receive the approachEvent from ElevatorSubsystem
				// the ApproachEvent is received in Elevator.receiveApproachEvent

				setCurrentFloor(nextFloor);
				System.out.println("Elevator moved to floor " + nextFloor);
			}
			// Set to idle once floor reached
			System.out.println("Elevator " + elevatorNumber + " reached floor " + currentFloor);
			motor.stop();
		}
	}

	/**
	 * Passes an ApproachEvent to the ElevatorSubsystem.
	 *
	 * @param approachEvent the ApproachEvent to be passed to the subsystem
	 */
	public void passApproachEvent(ApproachEvent approachEvent) {
		elevatorSubsystem.handleApproachEvent(approachEvent);
	}

	/**
	 * Receives an ApproachEvent from the Subsystem and returns it to the component.
	 *
	 * @param approachEvent the ApproachEvent to be received from the Subsystem
	 */
	@Override
	public void receiveApproachEvent(ApproachEvent approachEvent) {
		this.approachEvent = approachEvent;
	}
}
