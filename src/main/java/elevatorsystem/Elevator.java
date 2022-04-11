package elevatorsystem;

import requests.ApproachEvent;
import requests.ElevatorMonitor;
import requests.ServiceRequest;
import requests.SubsystemPasser;
import systemwide.Direction;
import systemwide.Origin;
import systemwide.SystemStatus;

import java.time.LocalTime;
import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeoutException;

/**
 * Elevator is a model for simulating an elevator.
 *
 * @author Liam Tripp, Brady Norton, Ramit Mahajan
 */
public class Elevator implements Runnable, SubsystemPasser {

	// Elevator Subsystem
	private final ElevatorSubsystem elevatorSubsystem;
	private final SystemStatus systemStatus;
	private final RequestQueue requestQueue;
	private final ElevatorMotor motor;
	private final Doors doors;

	// Elevator Properties
	private final int elevatorNumber;
	private int currentFloor;
	private Direction serviceDirection;
	// FIXME: should we allow for there to be one or more faults?
	private Fault fault;

	// toggles for Elevator sending messages and taking time to move
	private boolean messageTransferEnabled;
	private volatile ApproachEvent approachEvent;
	private int travelTime;
	private int doorTime;
	private volatile boolean doorsMalfunctioning;

	// Elevator Measurements
	private float speed;
	public static final float ACCELERATION = 0.304f; // meters/second^2
	/**
	 * Constructor for Elevator.
	 * Instantiates subsystem, currentFloor, speed, displacement, and status
	 *
	 * @param elevatorNumber the number of the elevator
	 * @param elevatorSubsystem the elevator subsystem for elevators
	 */
	public Elevator(int elevatorNumber, ElevatorSubsystem elevatorSubsystem) {
		this.elevatorNumber = elevatorNumber;
		this.elevatorSubsystem = elevatorSubsystem;
		requestQueue = new RequestQueue();
		motor = new ElevatorMotor();
		doors = new Doors();
		systemStatus = new SystemStatus(false);
		currentFloor = 1;
		serviceDirection = Direction.UP;
		travelTime = -1;
		doorTime = -1;
		fault = Fault.NONE;
		messageTransferEnabled = true;
		approachEvent = null;
		doorsMalfunctioning = false;
	}

	/**
	 * Checks if there are any more requests to process and processes
	 * and new requests.
	 */
	@Override
	public void run() {
		systemStatus.setSystemActivated(true);
		while (systemStatus.activated()) {
			moveElevatorWhilePossible();
		}
		System.out.println(getClass().getSimpleName() + " #" + elevatorNumber + " Thread terminated");
	}

	/**
	 * Moves the elevator while it has requests in its queue.
	 */
	public void moveElevatorWhilePossible() {
		while (!requestQueue.isEmpty()) {
			// Swap service direction check
			swapServiceDirectionIfNecessary();
			// Loop until the active queue is empty
			while (!requestQueue.isCurrentQueueEmpty()) {
				respondToRequest();
			}
		}
	}

	/**
	 * Elevator responds to the floor at the top of the RequestQueue.
	 */
	public void respondToRequest() {
		System.out.println();

		ServiceRequest nextRequest = requestQueue.peekNextRequest();
		int requestFloor = nextRequest.getFloorNumber();

		// Print status
		printStatus(requestFloor);
		// Compare the request floor and the next floor
		compareFloors(requestFloor);
		moveToNextFloor(nextRequest);
		// stop elevator if moving and new floor is request floor
		if (!motor.isIdle()) {
			compareFloors(requestFloor);
		}
	}

	/**
	 * Moves the Elevator to the next floor.
	 *
	 * @param request at the floor at the top of the RequestQueue
	 */
	// FIXME: this is deeply nested and could be broken into 2 or more methods:
	//  attemptToMove (boolean ???) and printElevatorAction (maybe)
	public void moveToNextFloor(ServiceRequest request) {
		int requestFloor = request.getFloorNumber();
		int nextFloor = motor.move(currentFloor, requestFloor);

		// in future iterations, shouldStopAtNextFloor will be followed by sending an ApproachRequest
		if (messageTransferEnabled) {
			// communicate with Scheduler to see if Elevator should stop at this floor
			ApproachEvent newApproachEvent = new ApproachEvent(request.getTime(), nextFloor,
					request.getDirection(), elevatorNumber, Origin.ELEVATOR_SYSTEM);
			passApproachEvent(newApproachEvent);
			// stall while waiting to receive the approachEvent from ElevatorSubsystem

			// if travelTime enabled, wait a set amount of time.
			// otherwise, wait forever
		}
		// FIXME: this is too deeply nested. extract into methods
		if (travelTime <= 0 && messageTransferEnabled) {
			while (approachEvent == null) {
			}
		} else if (travelTime > 0) {
			synchronized (this) {
				try {
					// wait to simulate movement
					wait(travelTime);

					if (messageTransferEnabled && approachEvent == null) {
						String errorMessage = "Elevator #" + elevatorNumber + " did not receive ApproachEvent before " + travelTime + " expired.";
						throw new TimeoutException(errorMessage);
					}
				} catch (InterruptedException ie) {
					setFault(Fault.ELEVATOR_STUCK);
					// shut down elevator
					motor.setMovementState(MovementState.STUCK);
					motor.setDirection(Direction.NONE);
					shutDownElevator();
					approachEvent = null;
					return;
				} catch (TimeoutException te) {
					setFault(Fault.ARRIVAL_SENSOR_FAIL);
					// shut down elevator
					motor.setMovementState(MovementState.STUCK);
					motor.setDirection(Direction.NONE);
					shutDownElevator();
					approachEvent = null;
					return;
				}
			}
		}
		approachEvent = null;

		// Move output message
		String messageToPrint = LocalTime.now().toString() + "\n";
		if (nextFloor != currentFloor) {
			messageToPrint += "Elevator #" + elevatorNumber + " moved to floor " + nextFloor;
		} else {
			messageToPrint += "Elevator #" + elevatorNumber + " moved (stayed) on floor " + nextFloor;
		}

		System.out.println(messageToPrint);
		setCurrentFloor(nextFloor);
	}

	/**
	 * Attempts to remove a floor from the requestQueue, throwing exceptions if unsuccessful.
	 *
	 * @param requestFloor the floor to be removed from the requestQueue
	 */
	public void attemptToRemoveFloor(int requestFloor) {
		ServiceRequest removedRequest = requestQueue.removeRequest();
		int removedFloor = removedRequest.getFloorNumber();
		boolean sameFloorRemovedAsPeeked = removedFloor == requestFloor;

		if (!sameFloorRemovedAsPeeked) {
			String messageToPrint = "\nFloor peeked " + requestFloor + ", Floor Removed: " + removedFloor + "\n";
			messageToPrint += "A request was added to Elevator " + elevatorNumber + " while the current request was being processed.";
			throw new ConcurrentModificationException(messageToPrint);
		}
	}

	/**
	 * Compares the requestFloor to the next floor and updates the Motor accordingly.
	 *
	 * @param requestFloor the floor the elevator is going to visit
	 */
	public void compareFloors(int requestFloor) {
		// Next floor in service direction
		int floorToVisit = motor.move(currentFloor, requestFloor);

		if (motor.isIdle()) {
			if (currentFloor == requestFloor) {
				stopAtFloor(requestFloor);
			} else {
				// floorToVisit == requestFloor || floorToVisit != requestFloor
				startMovingToFloor(floorToVisit);
			}
		} else { // elevator is moving
			if (currentFloor == requestFloor) {
				// elevator has reached destination after moving one floor
				stopAtFloor(requestFloor);
			}
			// do nothing if floorToVisit == requestFloor || floorToVisit != requestFloor
		}
		elevatorSubsystem.addEventToQueue(makeElevatorMonitor());
	}

	/**
	 * Closes the doors and updates elevator properties to be moving towards a floor.
	 *
	 * @param floorToVisit the next floor the elevator will visit
	 */
	public void startMovingToFloor(int floorToVisit) {

		// proceed until door closing successful
		while (!changeDoorState(Doors.State.CLOSED)) {
			elevatorSubsystem.addEventToQueue(makeElevatorMonitor());
			setDoorsMalfunctioning(false);
		}
		System.out.println("\n" + LocalTime.now() + "\nElevator #" + elevatorNumber + " closed its doors");
		motor.startMoving();
		motor.changeDirection(currentFloor, floorToVisit);
	}

	/**
	 * Stops the elevator at the specified floor and opens the doors.
	 *
	 * @param requestFloor the floor at the top of the requestQueue
	 */
	public void stopAtFloor(int requestFloor) {
		attemptToRemoveFloor(requestFloor);
		motor.stop();
		elevatorSubsystem.addEventToQueue(makeElevatorMonitor());
		System.out.println("\n" + LocalTime.now() + "\n Elevator #" + elevatorNumber + " reached its destination");

		// try to open doors until successful
		while (!changeDoorState(Doors.State.OPEN)) {
			elevatorSubsystem.addEventToQueue(makeElevatorMonitor());
			setDoorsMalfunctioning(false);
		}
		System.out.println("\n" + LocalTime.now() + "\n Elevator #" + elevatorNumber + " opened its doors");
	}

	/**
	 * Shuts down the elevator by removing all Requests from its RequestQueue.
	 */
	public void shutDownElevator() {
		// empty the request queue
		ServiceRequest removeRequest;
		do {
			removeRequest = requestQueue.removeRequest();
		} while (removeRequest != null);
		motor.setDirection(Direction.NONE);
	}

	/**
	 * Attempts to open or close the door depending on the state provided.
	 * If DoorTime is enabled, the elevator waits before acting on the Doors.
	 * If the Doors have malfunctioned, the Elevator corrects it.
	 *
	 * @param state the new State of the Doors
	 * @return true if the change is successful, false if door was set to stuck
	 */
	public boolean changeDoorState(Doors.State state) {
		// throw error invalid argument
		if (!state.equals(Doors.State.OPEN) && !state.equals(Doors.State.CLOSED)) {
			System.err.println("Invalid argument for Doors State in changeDoorState");
			System.exit(1);
		}
		// process door change
		synchronized (this) {
			try {
				if (doorTime > 0) {
					wait(doorTime);
				}

				if (!doorsMalfunctioning) {
					if (state == Doors.State.OPEN) {
						doors.open();
					} else {
						doors.close();
					}
					return true;
				} else {
					String messageToPrint = "Elevator #" + elevatorNumber + "'s doors are malfunctioning.";
					throw new IllegalStateException(messageToPrint);
				}
			} catch (InterruptedException e) {
				// if interrupted, try to change state again
				return changeDoorState(state);
			} catch (IllegalStateException ise) {
				doors.setToStuck();
				ise.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * Adds a request to the RequestQueue for Elevator to service.
	 *
	 * @param serviceRequest a service request for the elevator to perform
	 */
	public void addRequest(ServiceRequest serviceRequest) {
		//TODO remove after queueTime updated properly and serviceDirection is updated properly
		int elevatorFloorToPass = currentFloor;
		requestQueue.addRequest(elevatorFloorToPass, serviceDirection, serviceRequest);
	}

	/**
	 * Returns the elevator number.
	 *
	 * @return an integer corresponding to the elevator's number
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}

	/**
	 * Gets the SystemStatus of the System.
	 *
	 * @return the SystemStatus of the System
	 */
	public SystemStatus getSystemStatus() {
		return systemStatus;
	}

	/**
	 * Gets the RequestQueue of the elevator.
	 *
	 * @return the RequestQueue of the elevator
	 */
	public RequestQueue getRequestQueue() {
		return requestQueue;
	}

	/**
	 * Returns whether the RequestQueue is empty.
	 *
	 * @return true if the RequestQueue is empty, false otherwise
	 */
	public boolean hasNoRequests() {
		return requestQueue.isEmpty();
	}

	/**
	 * Swaps the RequestQueue and changes the service direction before elevator moves to next floor.
	 * TODO: In the future, there should be a check when the ElevatorMotor
	 * TODO: MovementState is IDLE. If so, the elevator uses this method.
	 */
	public void swapServiceDirectionIfNecessary() {
		if (requestQueue.swapQueues()) {
			setServiceDirection(Direction.swapDirection(serviceDirection));
			System.out.println("Elevator #" + elevatorNumber + " swapped queues, changed serviceDirection to " + serviceDirection + "\n");
		}
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
	 * Gets the Elevator's Doors.
	 *
	 * @return the doors of the Elevator
	 */
	public Doors getDoors() {
		return doors;
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
	 * Sets the Elevator's time traveling between floors when MOVING
	 * and waiting on a floor when STOPPED.
	 */
	public void setTravelTime(int time) {
		travelTime = time;
	}

	/**
	 * Sets the Elevator's Doors' opening / closing time.
	 */
	public void setDoorTime(int time) {
		doorTime = time;
	}

	/**
	 * Toggles whether the elevator may send / receive messages
	 * to and from the Scheduler.
	 */
	public void toggleMessageTransfer() {
		messageTransferEnabled = !messageTransferEnabled;
	}

	/**
	 * Passes an ApproachEvent to the ElevatorSubsystem.
	 *
	 * @param approachEvent the ApproachEvent to be passed to the subsystem
	 */
	@Override
	public void passApproachEvent(ApproachEvent approachEvent) {
		elevatorSubsystem.addEventToQueue(approachEvent);
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
	 * Returns the Elevator's current Fault.
	 *
	 * @return the current Fault of the elevator
	 */
	public Fault getFault() {
		return fault;
	}

	/**
	 * Modifies the current Fault of the Elevator.
	 *
	 * @param fault the new Fault for the Elevator
	 */
	public void setFault(Fault fault) {
		this.fault = fault;
		System.out.println("Elevator #" + elevatorNumber + " Fault: " + this.fault.toString() + ".");
		if (fault == Fault.ELEVATOR_STUCK) {
			motor.setMovementState(MovementState.STUCK);
			elevatorSubsystem.addEventToQueue(makeElevatorMonitor());
		}
	}

	/**
	 * Indicates whether the Elevator's doors are malfunctioning.
	 *
	 * @return true if the doors are malfunctioning, false otherwise
	 */
	public boolean doorsAreMalfunctioning() {
		return doorsMalfunctioning;
	}

	/**
	 * Sets the toggle for the Elevator's Doors malfunctioning.
	 *
	 * @param doorsAreMalfunctioning true if the doors are malfunctioning, false otherwise
	 */
	public void setDoorsMalfunctioning(boolean doorsAreMalfunctioning) {
		doorsMalfunctioning = doorsAreMalfunctioning;
		if (doorsAreMalfunctioning) {
			doors.setToStuck();
		}
	}

	/**
	 * Interrupts Elevator's executing Thread.
	 */
	public void interrupt() {
		Thread.currentThread().interrupt();
	}

	/**
	 * Create a status response when a rew elevator request is added
	 * that will change the status.
	 *
	 * @return a StatusUpdate containing new elevator information.
	 */
	public ElevatorMonitor makeElevatorMonitor() {
		return new ElevatorMonitor(elevatorNumber, currentFloor, serviceDirection, motor.getMovementState(), motor.getDirection(), doors.getState(), fault , requestQueue.isEmpty(), requestQueue.getExpectedTime(currentFloor, doorTime * 2, travelTime));
	}

	/**
	 * Prints the status of the elevator (current floor, requestFloor, door state, motor state, motor direction).
	 *
	 * @param requestFloor the floor the elevator is to service
	 */
	public void printStatus(int requestFloor) {
		String messageToPrint = LocalTime.now().toString() + "\n";
		messageToPrint += "Elevator #" + elevatorNumber + " Status:\n";
		messageToPrint += "[currentFloor, requestFloor]: [" + currentFloor + ", " + requestFloor + "]\n";
		messageToPrint += "[ServiceDirxn, MoveStatus, MotorDirxn, Doors, Fault]: ";
		messageToPrint += "[" + serviceDirection + " " + motor.getMovementState().getName() +
				" " + motor.getDirection() + " " + doors.getState() + " " + fault.getName() + "]\n";
		messageToPrint += "RequestQueue: " + requestQueue;
		System.out.println(messageToPrint);
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
	 * Calculates the amount of time it will take for the elevator to stop at it's current speed
	 *
	 * @return total time it will take to stop as a float
	 */
	public float stopTime() {
		float numerator = 0 - speed;
		return numerator / ACCELERATION;
	}

	/**
	 * Gets the distance until the next floor as a float.
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
}