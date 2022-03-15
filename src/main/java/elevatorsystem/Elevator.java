package elevatorsystem;

import requests.*;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Elevator is a model for simulating an elevator.
 *
 * @author Liam Tripp, Brady Norton
 */
public class Elevator implements Runnable, SubsystemPasser {

	// Elevator Subsystem
	private final ElevatorSubsystem elevatorSubsystem;

	// Elevator Measurements
	public static final float MAX_SPEED = 2.67f; // meters/second
	public static final float ACCELERATION = 0.304f; // meters/second^2
	public static final float LOAD_TIME = 9.5f; // seconds
	public static final float FLOOR_HEIGHT = 3.91f; // meters (22 steps/floor @ 0.1778 meters/step)
	public static final double ACCELERATION_DISTANCE = Math.pow(MAX_SPEED, 2) / (2 * ACCELERATION); // Vf^2 = Vi^2 + 2as therefore s = vf^2/2a
	public static final double ACCELERATION_TIME = Math.sqrt((FLOOR_HEIGHT * 2) / ACCELERATION); //s = 1/2at^2 therefore t = sqrt(s*2/a)

	// Elevator Properties
	private final int elevatorNumber;
	private int currentFloor;
	private Direction serviceDirection;
	private float speed;
	private double queueTime;

	private final Doors doors;
	private final ElevatorMotor motor;
	private final RequestQueue requestQueue;

	// list must be volatile so that origin checks if it's been updated
	// functionally, this is a stack (FIFO)
	private final CopyOnWriteArrayList<ServiceRequest> requests;
	private volatile ApproachEvent approachEvent;
	// variable for allowing / disallowing Elevator's message transfer
	private boolean messageTransferEnabled;

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
		currentFloor = 1;
		approachEvent = null;
		serviceDirection = Direction.UP;
		motor = new ElevatorMotor();
		doors = new Doors();
		queueTime = 0.0;
		requestQueue = new RequestQueue();
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
			moveElevatorWhilePossible();
		}
	}

	/**
	 * Moves the elevator while it has requests in its queue.
	 */
	public void moveElevatorWhilePossible() {
		while (!requestQueue.isEmpty()) {
			// Swap service direction check
			swapServiceDirectionIfNecessary();
			// Loop until the current queue is empty (all requests in the request queue have been completed)
			while (!requestQueue.isCurrentQueueEmpty()) {
				System.out.println();

				int requestFloor = requestQueue.peekNextRequest();
				//int requestFloor = requestQueue.removeRequest();

				// Print status
				printStatus(requestFloor);
				// Compare the request floor and the next floor
				compareFloors(requestFloor);

				moveToNextFloor(requestFloor);
			}
		}
	}

	/**
	 * Moves the Elevator to the next floor, stopping if it's the next floor.
	 *
	 * @param requestFloor the floor the elevator will move to
	 */
	public void moveToNextFloor(int requestFloor) {
		int nextFloor;
		nextFloor = motor.move(currentFloor, requestFloor);

		// in future iterations, shouldStopAtNextFloor will be followed by sending an ApproachRequest
		if (messageTransferEnabled) {
			// communicate with Scheduler to see if Elevator should stop at this floor
			ApproachEvent newApproachEvent = new ApproachEvent(LocalTime.now(), nextFloor,
					serviceDirection, elevatorNumber, Origin.ELEVATOR_SYSTEM);
			passApproachEvent(newApproachEvent);
			// stall while waiting to receive the approachEvent from ElevatorSubsystem
			// the ApproachEvent is received in Elevator.receiveApproachEvent
			while (approachEvent == null) {
			}
			approachEvent = null;
		}

		// stop output message
		if (nextFloor != currentFloor) {
			System.out.println("Elevator #" + elevatorNumber + " moved to floor " + nextFloor + " at " + LocalTime.now());
		} else {
			System.out.println("Elevator #" + elevatorNumber + " moved (stayed) on floor " + nextFloor + " at " + LocalTime.now());
		}
		boolean shouldStopAtNextFloor = nextFloor == requestFloor;
		setCurrentFloor(nextFloor);


		if (shouldStopAtNextFloor) {
			System.out.println("Elevator #" + elevatorNumber + " reached destination");
			// FIXME: this sometimes produces a Concurrency error due to a request being added to the
			//  elevator at the same time as the elevator is moving
			int removedFloor = requestQueue.removeRequest();
			boolean sameFloorRemovedAsPeeked = removedFloor == requestFloor;

			if (removedFloor == -1) {
				System.err.println("A value of -1 was received from the requestQueue.");
			} else if (!sameFloorRemovedAsPeeked) {
				throw new ConcurrentModificationException("A request was added while the current request was being processed.");
			}
		}
	}

	/**
	 * Compares the destinationFloor to the next floor and updates the Motor accordingly
	 *
	 * @param destinationFloor the floor the elevator is going to visit
	 */
	public void compareFloors(int destinationFloor) {
		// Next floor in service direction
		int nextFloor = motor.move(currentFloor, destinationFloor);

		// Motor is IDLE
		if (motor.isIdle()) {
			// Next floor is the destination floor
			if (destinationFloor == nextFloor) {
				// Remove request from queue
				// requestQueue.removeRequest();
				// Open doors
				/*
				if(!elevatorDoors.areOpen()){
					elevatorDoors.open();
				}
				 */
			} else {
				// Motor IDLE and next floor is not the destination floor
				// Close doors
				/*
				if(elevatorDoors.areOpen()){
					elevatorDoors.close();
				}
				 */
				updateMotor(destinationFloor);
			}
		} else {
			// Next floor is not the destination floor
			if (destinationFloor != nextFloor) {
				// Don't change motor
			} else {
				// Next floor is the destination floor
				// Remove the request floor from the queue
				// requestQueue.removeRequest();

				// Current requestQueue isn't empty and the next request is on a different floor
				if (destinationFloor != currentFloor && !requestQueue.isCurrentQueueEmpty()) {
					// Update the motor
					updateMotor(destinationFloor);
				}
			}
		}
	}

	/**
	 * Swaps the requestQueue and changes the service direction before elevator moves to next floor.
	 * TODO: In the future, there should be a check when the ElevatorMotor
	 * TODO: MovementState is IDLE. If so, the elevator uses this method.
	 */
	public void swapServiceDirectionIfNecessary() {
		System.out.println("Elevator " + elevatorNumber + " attempting to change queues.");
		if (requestQueue.swapQueues()) {
			serviceDirection = Direction.swapDirection(serviceDirection);
			System.out.println("Elevator " + elevatorNumber + " Changed direction to " + serviceDirection);
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
		//TODO remove after queueTime updated properly and serviceDirection is updated properly
		motor.setMovementState(MovementState.ACTIVE);
		if (serviceRequest instanceof ElevatorRequest elevatorRequest) {
			queueTime = getExpectedTime(elevatorRequest);
		}
		int elevatorFloorToPass = currentFloor;
		requestQueue.addRequest(elevatorFloorToPass, serviceDirection, serviceRequest);
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
	 * Returns whether the request queue is empty.
	 *
	 * @return true if the request queue is empty, false otherwise
	 */
	public boolean hasNoRequests() {
		return requestQueue.isEmpty();
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
	public float getDistanceUntilNextFloor() {
		float distance = 0;
		float stopTime = stopTime();

		// Using Kinematics equation: d = vt + (1/2)at^2
		float part1 = speed * stopTime;
		// System.out.println("Part 1: " + part1);

		float part2 = (float) ((0.5) * (ACCELERATION) * (Math.pow(stopTime, 2)));
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
	 * Gets the current floor the elevator is on.
	 *
	 * @return the current floor as an int
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * Sets the currentFloor that the elevator is on.
	 *
	 * @param currentFloor the floor to set the elevator on
	 */
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	/**
	 * Gets the Direction the elevator is heading.
	 *
	 * @return serviceDirection
	 */
	public Direction getServiceDirection() {
		return serviceDirection;
	}

	/**
	 * Sets the service direction of the elevator.
	 *
	 * @param direction the elevator will be moving
	 */
	public void setServiceDirection(Direction direction) {
		this.serviceDirection = direction;
	}

	/**
	 * Gets the speed of the elevator.
	 *
	 * @return the speed as a float
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Sets the speed of the elevator.
	 *
	 * @param speed the speed of the elevator
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Toggles whether the elevator may send / receive messages
	 * to and from the Scheduler.
	 */
	public void toggleMessageTransfer() {
		messageTransferEnabled = !messageTransferEnabled;
	}

	/**
	 * Processes a serviceRequest and moves based on the request type.
	 *
	 * @param serviceRequest the request that's sent to elevator
	 */
	public void processRequest(ServiceRequest serviceRequest) {
		// If request is an elevator request (from outside the elevator)
		System.out.println("Elevator #" + elevatorNumber + " processing: " + serviceRequest);
		if (serviceRequest instanceof ElevatorRequest elevatorRequest) {
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

		// loop until Elevator has reached the requested floor
		while (currentFloor != requestFloor) {

			int nextFloor = motor.move(currentFloor, requestFloor);
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
	 * @param elevatorRequest an elevator request to visit a floor
	 * @return a double containing the elevator's total expected queue time
	 */
	public double getExpectedTime(ElevatorRequest elevatorRequest) {
		return queueTime + LOAD_TIME + requestTime(elevatorRequest);
	}

	/**
	 * Gets the expected time of a new request for the current elevator
	 * based on distance.
	 *
	 * @param elevatorRequest a elevatorRequest to visit a floor
	 * @return a double containing the time to fulfil the request
	 */
	public double requestTime(ElevatorRequest elevatorRequest) {
		double distance = Math.abs(elevatorRequest.getFloorNumber() - currentFloor) * FLOOR_HEIGHT;
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
	 * Prints the status of the elevator (current floor, door state, motor state, motor direction).
	 *
	 * @param requestFloor the floor the elevator is to service
	 */
	public void printStatus(int requestFloor) {
		System.out.println("Elevator #" + elevatorNumber + " servicing floor " + requestFloor + " at " + LocalTime.now().toString());
		System.out.print("Elevator #" + elevatorNumber + " queue: ");
		requestQueue.printQueue();
		System.out.print("Elevator #" + elevatorNumber + " Status: [Floor, serviceDirxn, movement, motorDirxn]: [");
		System.out.print(currentFloor + " " + serviceDirection + " ");
		//System.out.println("Elevator " + elevatorNumber + " doors are: " + );
		System.out.print(motor.getMovementState().getName() + " ");
		System.out.println(motor.getDirection() + "]");
	}

	/**
	 * Update Motor properties based on the serviceRequest.
	 *
	 * NOTE: Might be changed to simply use the first request in the queue
	 *
	 * @param reqFloor the number of the floor that is requested
	 */
	public void updateMotor(int reqFloor) {
		// STOPPED
		if (motor.isIdle()) {
			// Next floor = destination
			if (currentFloor == reqFloor) {
				// Do nothing
			} else {
				// Next floor != destination
				// Close doors
				// elevatorDoor.setClose();

				// start moving
				// Change motor state
				motor.setMovementState(MovementState.ACTIVE);

				// Set motor Direction
				motor.changeDirection(currentFloor, reqFloor);
			}
		} else if (motor.isActive()) {
			// ACTIVE
			// Next floor != destination
			if (currentFloor != reqFloor) {
				// If motor is moving in the wrong direction
				motor.changeDirection(currentFloor, reqFloor);
			} else {
				motor.stop();
			}
		}
	}

	/**
	 * Create a status response when a rew elevator request is added
	 * that will change the status.
	 *
	 * @return a StatusUpdate containing new elevator information.
	 */
	public ElevatorMonitor makeElevatorMonitor() {
		return new ElevatorMonitor(queueTime, motor.getMovementState(), currentFloor, serviceDirection, elevatorNumber);
	}
}
