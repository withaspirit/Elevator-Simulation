package elevatorsystem;

import requests.*;
import systemwide.Direction;
import systemwide.Origin;

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
	private final ElevatorMonitor monitor;
	private final RequestQueue requestQueue;
	private final ElevatorMotor motor;
	private final Doors doors;

	// Elevator Properties
	private final int elevatorNumber;

	// toggles for Elevator sending messages and taking time to move
	private boolean messageTransferEnabled;
	private volatile ApproachEvent approachEvent;
	private int travelTime;
	private int doorTime;
	private volatile boolean doorsMalfunctioning;

	// Elevator Measurements
	private float speed;
	public static final float MAX_SPEED = 2.67f; // meters/second
	public static final float ACCELERATION = 0.304f; // meters/second^2
	public static final float LOAD_TIME = 9.5f; // seconds
	public static final float FLOOR_HEIGHT = 3.91f; // meters (22 steps/floor @ 0.1778 meters/step)
	public static final double ACCELERATION_DISTANCE = Math.pow(MAX_SPEED, 2) / (2 * ACCELERATION); // Vf^2 = Vi^2 + 2as therefore s = vf^2/2a
	public static final double ACCELERATION_TIME = Math.sqrt((FLOOR_HEIGHT * 2) / ACCELERATION); //s = 1/2at^2 therefore t = sqrt(s*2/a)

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
		int currentFloor = 1;
		Direction serviceDirection = Direction.UP;
		travelTime = -1;
		doorTime = -1;
		messageTransferEnabled = true;
		approachEvent = null;
		doorsMalfunctioning = false;
		monitor = new ElevatorMonitor(elevatorNumber, currentFloor, serviceDirection, motor.getMovementState(), motor.getDirection(), doors.getState(), Fault.NONE, requestQueue.isEmpty(), 0);
	}

	/**
	 * Checks if there are any more requests to process and processes
	 * and new requests.
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
		int currentFloor = monitor.getCurrentFloor();
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
		if (travelTime < 0 && messageTransferEnabled) {
			while (approachEvent == null) {
			}
		} else if (travelTime >= 0 ) {

			synchronized (this) {
				try {
					// wait to simulate movement
					wait(travelTime);

					if (messageTransferEnabled && approachEvent == null) {
						String errorMessage = "Elevator #" + elevatorNumber + " did not receive ApproachEvent before [travelTime] expired.";
						throw new TimeoutException(errorMessage);
					}
				} catch (InterruptedException ie) {
					monitor.setFault(Fault.ELEVATOR_STUCK);
					// shut down elevator
					motor.setMovementState(MovementState.STUCK);
					motor.setDirection(Direction.NONE);
					shutDownElevator();
					updateMonitor();
					elevatorSubsystem.handleElevatorMonitorUpdate(monitor);
					approachEvent = null;
					return;
				} catch (TimeoutException te) {
					monitor.setFault(Fault.ARRIVAL_SENSOR_FAIL);
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
		monitor.setCurrentFloor(nextFloor);
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
			System.out.println("Floor peeked " + requestFloor + ", Floor Removed: " + removedFloor);
			throw new ConcurrentModificationException("A request was added to Elevator " + elevatorNumber + " while the current request was being processed.");
		}
	}

	/**
	 * Compares the requestFloor to the next floor and updates the Motor accordingly.
	 *
	 * @param requestFloor the floor the elevator is going to visit
	 */
	public void compareFloors(int requestFloor) {
		// Next floor in service direction
		int currentFloor = monitor.getCurrentFloor();
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
		updateMonitor();
		elevatorSubsystem.handleElevatorMonitorUpdate(monitor);
	}

	/**
	 * Closes the doors and updates elevator properties to be moving towards a floor.
	 *
	 * @param floorToVisit the next floor the elevator will visit
	 */
	public void startMovingToFloor(int floorToVisit) {
		// proceed only if door closing successful
		if (attemptToCloseDoors()) {
			motor.startMoving();
			motor.changeDirection(monitor.getCurrentFloor(), floorToVisit);
			// if doors opening also unsuccessful, shut down elevator
		} else if (monitor.getFault() == Fault.DOORS_INTERRUPTED) {
			if (!attemptToOpenDoors()) {
				doors.setToStuck();
				shutDownElevator();
			}
		} else if (monitor.getFault() == Fault.DOORS_STUCK) {
			// door malfunction behavior
			shutDownElevator();
		}
	}

	/**
	 * Stops the elevator at the specified floor and opens the doors.
	 *
	 * @param requestFloor the floor at the top of the requestQueue
	 */
	public void stopAtFloor(int requestFloor) {
		attemptToRemoveFloor(requestFloor);
		motor.stop();
		updateMonitor();
		elevatorSubsystem.handleElevatorMonitorUpdate(monitor);

		// proceed only if door opening successful
		if (attemptToOpenDoors()) {
			System.out.println("Elevator #" + elevatorNumber + " reached destination");
		} else {
			// door malfunction behavior
			doors.setToStuck();
			shutDownElevator();
		}
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
	 * Attempts to open the Elevator's Doors. If DoorTime is enabled, the
	 * elevator waits before taking action on the Doors. If the Doors have
	 * malfunctioned, the Elevator takes action accordingly.
	 *
	 * @return true if attempt is successful, false otherwise
	 */
	// FIXME: attemptToOpenDoors and attemptToCloseDoors are very similar
	public boolean attemptToOpenDoors() {
		synchronized (this) {
			try {
				if (doorTime >= 0) {
					wait(doorTime);
				}

				if (!doorsMalfunctioning) {
					doors.open();
					return true;
				} else {
					String messageToPrint = "Elevator #" + elevatorNumber + "'s doors are malfunctioning.";
					throw new IllegalStateException(messageToPrint);
				}
			} catch (InterruptedException e) {
				// do nothing. doors opening can never be interrupted
				return true;
			} catch (IllegalStateException ise) {
				monitor.setFault(Fault.DOORS_STUCK);
				ise.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * Attempts to close the Elevator's Doors. If DoorTime is enabled, the
	 * elevator waits before taking action on the Doors. If the Doors have
	 * malfunctioned, the Elevator takes action accordingly. If the Doors
	 * are interrupted, the doors reverse course.
	 *
	 * @return true if attempt is successful, false otherwise
	 */
	public boolean attemptToCloseDoors() {
		synchronized (this) {
			try {
				if (doorTime >= 0) {
					wait(doorTime);
				}

				if (!doorsMalfunctioning) {
					doors.close();
					return true;
				} else {
					String messageToPrint = "Elevator #" + elevatorNumber + "'s doors are malfunctioning.";
					throw new IllegalStateException(messageToPrint);
				}
			} catch (InterruptedException ie) {
				monitor.setFault(Fault.DOORS_INTERRUPTED);
				ie.printStackTrace();
				return false;
			} catch (IllegalStateException ise) {
				monitor.setFault(Fault.DOORS_STUCK);
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
		int elevatorFloorToPass = monitor.getCurrentFloor();
		requestQueue.addRequest(elevatorFloorToPass, monitor.getServiceDirection(), serviceRequest);
		updateMonitor();
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
			monitor.setServiceDirection(Direction.swapDirection(monitor.getServiceDirection()));
			System.out.println("Elevator #" + elevatorNumber + " swapped queues, changed serviceDirection to " + monitor.getServiceDirection() + "\n");
		}
	}

	/**
	 * Returns the Elevator's elevatorMonitor.
	 *
	 * @return the elevatorMonitor containing status information of the elevator
	 */
	public ElevatorMonitor getMonitor() {
		return monitor;
	}

	/**
	 * Updates the ElevatorMonitor with information from the Elevator.
	 */
	public void updateMonitor() {
		monitor.setMovementDirection(motor.getDirection());
		monitor.setMovementState(motor.getMovementState());
		monitor.setDoorsState(doors.getState());
		monitor.setRequestsStatus(hasNoRequests());
		monitor.setQueueTime(requestQueue.getExpectedTime(monitor.getCurrentFloor()));
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
	 * Toggles the Door Malfunction flag of the Elevator.
	 */
	public void toggleDoorMalfunction() {
		doorsMalfunctioning = !doorsMalfunctioning;
	}

	/**
	 * Interrupts Elevator's executing Thread.
	 */
	public void interrupt() {
		Thread.currentThread().interrupt();
	}

	/**
	 * Prints the status of the elevator (current floor, requestFloor, door state, motor state, motor direction).
	 *
	 * @param requestFloor the floor the elevator is to service
	 */
	public void printStatus(int requestFloor) {
		int currentFloor = monitor.getCurrentFloor();
		String messageToPrint = LocalTime.now().toString() + "\n";
		messageToPrint += "Elevator #" + elevatorNumber + " Status:\n";
		messageToPrint += "[currentFloor, requestFloor]: [" + currentFloor + ", " + requestFloor + "]\n";
		messageToPrint += "[ServiceDirxn, MoveStatus, MotorDirxn, Doors, Fault]: ";
		messageToPrint += "[" + monitor.getServiceDirection() + " " + motor.getMovementState().getName() +
				" " + motor.getDirection() + " " + doors.getState() + " " + monitor.getFault().getName() + "]\n";
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
