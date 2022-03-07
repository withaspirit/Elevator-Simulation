package elevatorsystem;

import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.util.ArrayList;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, SubsystemMessagePasser, SystemEventListener {

	private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
	private Elevator elevator;
	private ElevatorMotor motor;
	private FloorsQueue floorsQueue;
	private Origin origin;

	// Elevator Measurements
	public static final float MAX_SPEED = 2.67f; // meters/second
	public static final float ACCELERATION = 0.304f; // meters/second^2
	public static final float LOAD_TIME = 9.5f; // seconds
	public static final float FLOOR_HEIGHT = 3.91f; // meters (22 steps/floor @ 0.1778 meters/step)
	public static final double ACCELERATION_DISTANCE = Math.pow(MAX_SPEED, 2)/ (2 * ACCELERATION); // Vf^2 = Vi^2 + 2as therefore s = vf^2/2a
	public static final double ACCELERATION_TIME = Math.sqrt((FLOOR_HEIGHT * 2) / ACCELERATION); //s = 1/2at^2 therefore t = sqrt(s*2/a)

	/**
	 * Constructor for ElevatorSubsystem.
	 *
	 * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
	 */
	public ElevatorSubsystem(BoundedBuffer buffer, Elevator elevator, ElevatorMotor motor, FloorsQueue floorsQueue) {
		this.elevatorSubsystemBuffer = buffer;
		this.elevator = elevator;
		this.motor = motor;
		this.floorsQueue = floorsQueue;
		origin = Origin.ELEVATOR_SYSTEM;
	}

	/**
	 * Returns the motor associated with the Elevator.
	 *
	 * @return elevatorMotor the elevatorMotor for the elevator
	 */
	public ElevatorMotor getMotor() {
		return motor;
	}

	/**
	 * Gets the total expected time that the elevator will need to take to
	 * perform its current requests along with the new elevatorRequest.
	 *
	 * @param elevatorRequest an elevator request from the floorSubsystem
	 * @return a double containing the elevator's total expected queue time
	 */
	public double getExpectedTime(ElevatorRequest elevatorRequest) {
		return floorsQueue.getQueueTime() + LOAD_TIME + requestTime(elevatorRequest);
	}

	/**
	 * Gets the expected time of a new request for the current elevator
	 * based on distance.
	 *
	 * @param elevatorRequest a serviceRequest to visit a floor
	 * @return a double containing the time to fulfil the request
	 */
	public double requestTime(ElevatorRequest elevatorRequest) {
		double distance = Math.abs(elevatorRequest.getDesiredFloor() - elevator.getCurrentFloor()) * FLOOR_HEIGHT;
		if (distance > ACCELERATION_DISTANCE * 2) {
			return (distance - ACCELERATION_DISTANCE * 2) / MAX_SPEED + ACCELERATION_TIME * 2;
		} else {
			return Math.sqrt(distance * 2 / ACCELERATION); // elevator accelerates and decelerates continuously
		}
	}

	/**
	 * Adds a new service request to the list of requests
	 *
	 * @param elevatorRequest a service request for the elevator to perform
	 */
	public void addRequest(ElevatorRequest elevatorRequest) {
		floorsQueue.setQueueTime(getExpectedTime(elevatorRequest));
		if (motor.getDirection() == Direction.NONE){
			motor.setDirection(elevatorRequest.getDirection());
		}
		floorsQueue.addFloor(elevatorRequest.getFloorNumber(),elevator.getCurrentFloor(), elevatorRequest.getDesiredFloor(), motor.getDirection());
		if (motor.isIdle()){
			motor.setMovementState(MovementState.ACTIVE);
		}
	}

	/**
	 * Passes an ApproachEvent between a Subsystem component and the Subsystem.
	 *
	 * @param approachEvent the approach event for the system
	 */
	@Override
	public void handleApproachEvent(ApproachEvent approachEvent) {
		sendMessage(approachEvent, elevatorSubsystemBuffer, origin);
	}

	/**
	 * Returns the instance of elevator that the subsystem has.
	 *
	 * @return the instance of the elevator that the subsystem has.
	 */
	public Elevator getElevator(){
		return elevator;
	}

	/**
	 * Calculates the amount of time it will take for the elevator to stop at it's current speed
	 *
	 * @return total time it will take to stop as a float
	 */
	public float stopTime() {
		float numerator = 0 - elevator.getSpeed();
		return numerator / ACCELERATION;
	}

	/**
	 * Gets the distance until the next floor as a float
	 *
	 * @return distance until next floor
	 */
	public float getDistanceUntilNextFloor(){
		float distance = 0;
		float stopTime = stopTime();

		// Using Kinematics equation: d = vt + (1/2)at^2
		float part1 = elevator.getSpeed() * stopTime;
		// System.out.println("Part 1: " + part1);

		float part2 = (float) ((0.5)*(ACCELERATION)*(Math.pow(stopTime,2)));
		// System.out.println("Part 2: " + part2);

		return part1 - part2;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * ElevatorSubsystem
	 * Sends: ApproachEvent
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		while (true) {
			SystemEvent request = receiveMessage(elevatorSubsystemBuffer, origin);
			if(request instanceof ElevatorRequest elevatorRequest){
				addRequest(elevatorRequest);
				sendMessage(new FloorRequest(elevatorRequest, elevator.getElevatorNumber()), elevatorSubsystemBuffer, origin);
			} else if(request instanceof ApproachEvent approachEvent) {
				// do something
			}
		}
	}
}
