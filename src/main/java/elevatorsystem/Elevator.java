package elevatorsystem;

import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;
import java.time.LocalTime;

import java.util.concurrent.CopyOnWriteArrayList;

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

	// Elevator Subsystem
	private ElevatorSubsystem elevatorSubsystem;

	// Elevator Measurements
	public static final float MAX_SPEED = 2.67f; // meters/second
	public static final float ACCELERATION = 0.304f; // meters/second^2
	public static final float LOAD_TIME = 9.5f; // seconds
	public static final float FLOOR_HEIGHT = 3.91f; // meters (22 steps/floor @ 0.1778 meters/step)
	public static final double ACCELERATION_DISTANCE = Math.pow(MAX_SPEED, 2)/ (2 * ACCELERATION); // Vf^2 = Vi^2 + 2as therefore s = vf^2/2a
	public static final double ACCELERATION_TIME = Math.sqrt((FLOOR_HEIGHT * 2) / ACCELERATION); //s = 1/2at^2 therefore t = sqrt(s*2/a)


	// Elevator Properties
	private final int elevatorNumber;
	private int currentFloor;
	private Direction direction = Direction.UP;
	private Direction serviceDirection;
	private float speed;
	private float displacement;
	private double queueTime;

	private final Doors doors;
	private final ElevatorMotor motor;
	private Direction currentDirection;
	private FloorsQueue floorsQueue;
	private ElevatorRequest request;

	// list must be volatile so that origin checks if it's been updated
	// functionally, this is a stack (FIFO)
	private volatile CopyOnWriteArrayList<ServiceRequest> requests;
	private volatile ApproachEvent approachEvent;
	// variable for allowing / disallowing Elevator's message transfer
	private boolean messageTransferEnabled;

	// Variable to track if a passenger has been picked up

	/**
	 * Constructor for Elevator class
	 * Instantiates subsystem, currentFloor, speed, displacement, and status
	 *
	 * @param elevatorNumber the number of the elevator
	 * @param elevatorSubsystem the elevator subsystem for elevators
	 */
	public Elevator(int elevatorNumber, ElevatorSubsystem elevatorSubsystem) {
		this.elevatorNumber = elevatorNumber;
		this.elevatorSubsystem = elevatorSubsystem;
		speed = 0;
		displacement = 0;
		direction = Direction.UP;
		serviceDirection = Direction.UP;
		motor = new ElevatorMotor();
		doors = new Doors();
		queueTime = 0.0;
		floorsQueue = new FloorsQueue();
		request = null;
		requests = new CopyOnWriteArrayList<>();
		messageTransferEnabled = true;
	}

	/**
	 * Checks if there are any more requests to process and processes
	 * and new requests
	 */
	@Override
	public void run() {
		while (true) {
			/*
			while (!floorsQueue.isEmpty()) {
				swapServiceDirectionIfNecessary();
				while (!floorsQueue.isCurrentQueueEmpty()) {
					// move elevator and stuff
				}
			}
			*/

			if (!requests.isEmpty()) {
				System.out.println();
				System.out.println("Elevator #" + elevatorNumber + "'s remaining requests: " + requests);
				System.out.println("Current Status: ");
				printStatus();
				System.out.println("Requests in list: " + requests);
				//processRequest(getNextRequest());
				addRequest(getNextRequest());
			}

			while (!floorsQueue.isEmpty()) {
				// Swap service direction check
        		swapServiceDirectionIfNecessary();
				// Loop until the current queue is empty (all requests in the current floors queue have been completed)
				while(!floorsQueue.isCurrentQueueEmpty()){
					// Compare the request floor and the next floor
					compareFloors();
					// Move to next floor
					setCurrentFloor(motor.move(currentFloor, floorsQueue.peekNextRequest(), motor.getDirection()));
				}
			}
		}
	}

	/**
	 * Compares the destinationFloor to the next floor and updates the Motor accordingly
	 *
	 * @param
	 */
	public void compareFloors(){
		// Requested destination floor
		int destinationFloor = floorsQueue.peekNextRequest();

		// Next floor in service direction
		int nextFloor = motor.move(currentFloor, destinationFloor, motor.getDirection());

		// Motor is IDLE
		if(motor.isIdle()){
			// Next floor is the destination floor
			if(destinationFloor == nextFloor){
				// Remove request from queue
				floorsQueue.removeRequest();
				// Open doors
				/*
				if(!elevatorDoors.areOpen()){
					elevatorDoors.open();
				}
				 */
			}
			// Motor IDLE and next floor is not the destination floor
			else{
				// Close doors
				/*
				if(elevatorDoors.areOpen()){
					elevatorDoors.close();
				}
				 */
				// Update the Motor
				updateMotor(destinationFloor);
			}
		}
		// Motor is ACTIVE
		else{
			// Next floor is not the destination floor
			if(destinationFloor != nextFloor){
				// Don't change motor
			}
			// Next floor is the destination floor
			else {
				// Remove the request floor from the queue
				floorsQueue.removeRequest();

				// Current floorsQueue isn't empty and the next request is on a different floor
				if(destinationFloor != currentFloor && !floorsQueue.isCurrentQueueEmpty()){
					// Update the motor
					updateMotor(destinationFloor);
				}
			}
		}
	}

	/**
	 * Swaps the floorQueue and changes the service direction before elevator moves to next floor.
	 * TODO: In the future, there should be a check when the ElevatorMotor
	 * TODO: MovementState is IDLE. If so, the elevator uses this method.
	 */
	public void swapServiceDirectionIfNecessary() {
		System.out.println("Elevator attempting to change queues.");
		if (floorsQueue.swapQueues()) {
			serviceDirection = Direction.swapDirection(serviceDirection);
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
	 * Adds a request to the queue of requests for Elevator to service.
	 *
	 * @param serviceRequest a service request for the elevator to perform
	 */
	public void addRequest(ServiceRequest serviceRequest) {
		requests.add(serviceRequest);
		int elevatorFloorToPass = currentFloor;
		floorsQueue.addRequest(elevatorFloorToPass, serviceDirection, serviceRequest);
	}

	/**
	 * Returns the request at the front of the request list
	 * and removes it from the list.
	 *
	 * @return serviceRequest a service request containing a request for the elevator to perform
	 */
	public ServiceRequest getNextRequest() {
		return requests.remove(requests.size() - 1);
	}

	/**
	 * Returns the number of requests the elevator has left to fulfill.
	 *
	 * @return numberOfRequests the number of requests the elevator has remaining
	 */
	public int getNumberOfRequests() {
		return requests.size();
	}

	/**
	 * Calculates the amount of time it will take for the elevator to stop at it's current speed
	 *
	 * @return total time it will take to stop as a float
	 */
	public float stopTime() {
		float numerator = 0 - speed;
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
		float part1 = speed*stopTime;
		// System.out.println("Part 1: " + part1);

		float part2 = (float) ((0.5)*(ACCELERATION)*(Math.pow(stopTime,2)));
		// System.out.println("Part 2: " + part2);

		return part1 - part2;
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
	 * Gets the Direction the elevator is heading
	 *
	 * @return Direction
	 */
	public Direction getDirection(){
		return direction;
	}

	/**
	 * Sets the direction of the elevator
	 *
	 * @param direction the elevator will be moving
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
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
	 * Sets the current service request to process
	 *
	 * @param request
	 */
	public void setRequest(ServiceRequest request){
		this.request = (ElevatorRequest) request;
	}

	/**
	 * Toggles whether the elevator may send / receive messages
	 * to and from the Scheduler.
	 */
	public void toggleMessageTransfer() {
		messageTransferEnabled = !messageTransferEnabled;
	}

	/**
	 * Processes a serviceRequest and moves based on the request type
	 *
	 *
	 *
	 * @param serviceRequest the request that's sent to elevator
	 */
	public void processRequest(ServiceRequest serviceRequest){
		// If request is an elevator request (from outside the elevator)
		System.out.println("Elevator #" + elevatorNumber + " processing: " + serviceRequest);
		if(serviceRequest instanceof ElevatorRequest elevatorRequest){
			// Move to floor from which elevatorRequest originated


			// created a ServiceRequest going to the desired floor for the desired floor
			ServiceRequest request = new ServiceRequest(elevatorRequest.getTime(), elevatorRequest.getDesiredFloor(),
					elevatorRequest.getDirection(), elevatorRequest.getOrigin());
			addRequest(request);
		// Request from within the elevator
		} else {
			moveToFloor(serviceRequest);
		}
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
//		queueTime = getExpectedTime(serviceRequest);

		// Set floor of request
		int requestFloor = serviceRequest.getFloorNumber();

		// Set direction of request
		Direction requestedDirection = serviceRequest.getDirection();

		/*
		if (floorsQueue.isDownqueueEmpty() && floorsQueue.isUpqueueEmpty()){
			currentDirection = serviceRequest.getDirection();
		}
		System.out.print("Elevator# " + elevatorNumber + " ");
		floorsQueue.addFloor(serviceRequest.getFloorNumber(), requestFloor, currentFloor, serviceRequest.getDirection());
		motor.setMovementState(MovementState.ACTIVE);
		 */

		// loop until Elevator has reached the requested floor
		while (currentFloor != requestFloor) {

			int nextFloor = motor.move(currentFloor, requestFloor, requestedDirection);
			/*
			if (messageTransferEnabled) {
				// communicate with Scheduler to see if Elevator should stop at this floor
				ApproachEvent newApproachEvent = new ApproachEvent(serviceRequest.getTime(), nextFloor,
						serviceRequest.getDirection(), elevatorNumber, Origin.ELEVATOR_SYSTEM);
				passApproachEvent(newApproachEvent);
				// stall while waiting to receive the approachEvent from ElevatorSubsystem
				// the ApproachEvent is received in Elevator.receiveApproachEvent
				while (approachEvent == null) {
				}
				approachEvent = null;
			}

			 */
			setCurrentFloor(nextFloor);
			System.out.println("Elevator #" + elevatorNumber + " moved to floor " + nextFloor);
    	}
		// Set to idle once floor reached
		System.out.println("Elevator " + elevatorNumber + " reached floor " + getCurrentFloor());
		motor.stop();
	}

	/**
	 * Gets the total expected time that the elevator will need to take to
	 * perform its current requests along with the new elevatorRequest.
	 *
	 * @param serviceRequest a service request to visit a floor
	 * @return a double containing the elevator's total expected queue time
	 */
	public double getExpectedTime(ServiceRequest serviceRequest) {
		return queueTime + LOAD_TIME + requestTime(serviceRequest);
	}

	/**
	 * Gets the expected time of a new request for the current elevator
	 * based on distance.
	 *
	 * @param serviceRequest a serviceRequest to visit a floor
	 * @return a double containing the time to fulfil the request
	 */
	public double requestTime(ServiceRequest serviceRequest) {
		double distance = Math.abs(serviceRequest.getFloorNumber() - currentFloor) * FLOOR_HEIGHT;
		if (distance > ACCELERATION_DISTANCE * 2) {
			return (distance - ACCELERATION_DISTANCE * 2) / MAX_SPEED + ACCELERATION_TIME * 2;
		} else {
			return Math.sqrt(distance * 2 / ACCELERATION); // elevator accelerates and decelerates continuously
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

	/**
	 * Prints the status of the elevator (current floor, door state, motor state, motor direction)
	 *
	 */
	public void printStatus(){
		System.out.println("Elevator " + elevatorNumber + " current floor: " + currentFloor);
		//System.out.println("Elevator " + elevatorNumber + " doors are: " + );
		System.out.println("Elevator " + elevatorNumber + " motor is currently: " + motor.getMovementState().getName());
		System.out.println("Elevator " + elevatorNumber + " motor's current direction: " + motor.getDirection());
	}

	/**
	 * Update Motor properties based on the serviceRequest
	 *
	 * NOTE: Might be changed to simply use the first request in the queue
	 *
	 * @param reqFloor the number of the floor that is requested
	 */
	public void updateMotor(int reqFloor){
		// STOPPED
		if(motor.isIdle()){
			// Next floor = destination
			if(currentFloor == reqFloor){
				// Do nothing
			}
			// Next floor != destination
			else{
				// Close doors
				// elevatorDoor.setClose();

				// start moving
				// Change motor state
				motor.setMovementState(MovementState.ACTIVE);

				// Set motor Direction
				motor.changeDirection(currentFloor, reqFloor);
			}
		}
		// ACTIVE
		else if(motor.isActive()){
			// Next floor != destination
			if(currentFloor != reqFloor){
				// If motor is moving in the wrong direction
				motor.changeDirection(currentFloor, reqFloor);
			}
			// Next floor == destination
			else{
				motor.stop();
			}
		}
	}
}
