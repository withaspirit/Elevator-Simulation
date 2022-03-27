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
	private final RequestQueue requestQueue;
	private final ElevatorMotor motor;
	private final Doors doors;

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
	// FIXME: should we allow for there to be one or more faults?
	private Fault fault;

	private volatile ApproachEvent approachEvent;
	// variable for allowing / disallowing Elevator's message transfer
	private boolean messageTransferEnabled;
	private boolean travelTimeEnabled;
	private boolean doorTimeEnabled;
	private volatile boolean doorsMalfunctioning;

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
		fault = Fault.NONE;
		currentFloor = 1;
		serviceDirection = Direction.UP;
		approachEvent = null;
		messageTransferEnabled = true;
		travelTimeEnabled = false;
		doorTimeEnabled = false;
		doorsMalfunctioning = false;
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

		int requestFloor = requestQueue.peekNextRequest();
		//int requestFloor = requestQueue.removeRequest();

		// Print status
		printStatus(requestFloor);
		// Compare the request floor and the next floor
		compareFloors(requestFloor);
		moveToNextFloor(requestFloor);
		// stop elevator if moving and new floor is request floor
		if (!motor.isIdle()) {
			compareFloors(requestFloor);
		}
	}

	/**
	 * Moves the Elevator to the next floor.
	 *
	 * @param requestFloor the floor at the top of the queue of requests
	 */
	// FIXME: this is deeply nested and could be broken into 2 or more methods:
	//  attemptToMove (boolean ???) and printElevatorAction (maybe)
	public void moveToNextFloor(int requestFloor) {
		int nextFloor = motor.move(currentFloor, requestFloor);

		// in future iterations, shouldStopAtNextFloor will be followed by sending an ApproachRequest
		if (messageTransferEnabled) {
			// communicate with Scheduler to see if Elevator should stop at this floor
			ApproachEvent newApproachEvent = new ApproachEvent(LocalTime.now(), nextFloor,
					serviceDirection, elevatorNumber, Origin.ELEVATOR_SYSTEM);
			passApproachEvent(newApproachEvent);
			// stall while waiting to receive the approachEvent from ElevatorSubsystem

			// if travelTime enabled, wait a set amount of time.
			// otherwise, wait forever
		}
		// FIXME: this is too deeply nested. extract into methods
		if (!travelTimeEnabled && messageTransferEnabled) {
			while (approachEvent == null) {
			}
		} else if (travelTimeEnabled) {

			synchronized (this) {
				try {
					// wait to simulate movement
					wait(300);

					if (messageTransferEnabled && approachEvent == null) {
						String errorMessage = "Elevator #" + elevatorNumber + " did not receive ApproachEvent before [travelTime] expired.";
						throw new TimeoutException(errorMessage);
					}
				} catch (InterruptedException ie) {
					setFault(Fault.ELEVATOR_STUCK);
					// handle ApproachEvent wait interrupt
					// TODO: Not sure if should have if-else for (approachEvent == null)
					ie.printStackTrace();
				} catch (TimeoutException te) {
					setFault(Fault.ARRIVAL_SENSOR_FAIL);
					// handle ArrivalSensor Fault
					te.printStackTrace();
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

		elevatorSubsystem.handleElevatorMonitorUpdate(makeElevatorMonitor());

		System.out.println(messageToPrint);
		setCurrentFloor(nextFloor);
	}

	/**
	 * Attempts to remove a floor from the requestQueue, throwing exceptions if unsuccessful.
	 *
	 * @param requestFloor the floor to be removed from the requestQueue
	 */
	public void attemptToRemoveFloor(int requestFloor) {
		int removedFloor = requestQueue.removeRequest();
		boolean sameFloorRemovedAsPeeked = removedFloor == requestFloor;

		if (removedFloor == -1) {
			throw new IllegalArgumentException("A value of -1 was received from the requestQueue.");
		} else if (!sameFloorRemovedAsPeeked) {
			System.out.println("Floor peeked " + requestFloor + ", Floor Removed: " + removedFloor);
			throw new ConcurrentModificationException("A request was added to Elevator " + elevatorNumber + " while the current request was being processed.");
		}
	}

	/**
	 * Compares the requestFloor to the next floor and updates the Motor accordingly
	 *
	 * @param requestFloor the floor the elevator is going to visit
	 */
	public void compareFloors(int requestFloor) {
		// Next floor in service direction
		int floorToVisit = motor.move(currentFloor, requestFloor);

		if (motor.isIdle()) {
			// elevator is stopped
			if (currentFloor == requestFloor) {
				stopAtFloor(requestFloor);
			} else if (floorToVisit == requestFloor) {
				startMovingToFloor(floorToVisit);
			} else {
				// requestFloor != floorToVisit
				startMovingToFloor(floorToVisit);
			}
		} else {
			// elevator is moving
			if (currentFloor == requestFloor) {
				// elevator has reached destination after moving one floor
				stopAtFloor(requestFloor);
			} else if (floorToVisit == requestFloor) {
				// (???) do nothing
			} else {
				// floorToVisit != requestFloor
				// keep moving
			}
		}
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
				if (doorTimeEnabled) {
					wait(300);
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
				setFault(Fault.DOORS_STUCK);
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
				if (doorTimeEnabled) {
					wait(300);
				}

				if (!doorsMalfunctioning) {
					doors.close();
					return true;
				} else {
					String messageToPrint = "Elevator #" + elevatorNumber + "'s doors are malfunctioning.";
					throw new IllegalStateException(messageToPrint);
				}
			} catch (InterruptedException ie) {
				setFault(Fault.DOORS_INTERRUPTED);
				ie.printStackTrace();
				return false;
			} catch (IllegalStateException ise) {
				setFault(Fault.DOORS_STUCK);
				ise.printStackTrace();
				return false;
			}
		}
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
			motor.changeDirection(currentFloor, floorToVisit);
		} else if (!attemptToOpenDoors()) {
			// if doors opening also unsuccessful, shut down elevator
			doors.setToStuck();
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
		// proceed only if door opening successful
		if (attemptToOpenDoors()) {
			System.out.println("Elevator #" + elevatorNumber + " reached destination");
		} else {
			// door malfunction behavior ???
		}
	}

	/**
	 * Shuts down the elevator and prevents further use
	 */
	public void shutDownElevator() {
		// empty the request queue
		int removeRequest;
		do {
			removeRequest = requestQueue.removeRequest();
		} while (removeRequest != -1);
		motor.setDirection(Direction.NONE);
	}

	/**
	 * Swaps the requestQueue and changes the service direction before elevator moves to next floor.
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
	 * Returns the elevator number
	 *
	 * @return an integer corresponding to the elevator's number
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
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
	 * Adds a request to the queue of requests for Elevator to service.
	 *
	 * @param serviceRequest a service request for the elevator to perform
	 */
	public void addRequest(ServiceRequest serviceRequest) {
		//TODO remove after queueTime updated properly and serviceDirection is updated properly
		int elevatorFloorToPass = currentFloor;
		requestQueue.addRequest(elevatorFloorToPass, serviceDirection, serviceRequest);
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
	 * Toggles the Elevator thread waiting while moving to simulate movement.
	 * If TravelTime is enabled, the Elevator may experience interrupts.
	 */
	public void toggleTravelTime() {
		travelTimeEnabled = !travelTimeEnabled;
	}

	/**
	 * Toggles whether an Elevator Thread waits time when the Doors are opening and closing.
	 */
	public void toggleDoorTime() {
		doorTimeEnabled = !doorTimeEnabled;
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
	 * Gets the request queue of the elevator
	 *
	 * @return the request queue of the elevator
	 */
	public RequestQueue getRequestQueue() {
		return requestQueue;
	}

	/**
	 * Create a status response when a rew elevator request is added
	 * that will change the status.
	 *
	 * @return a StatusUpdate containing new elevator information.
	 */
	public ElevatorMonitor makeElevatorMonitor() {
		return new ElevatorMonitor(requestQueue.getExpectedTime(currentFloor), motor.getMovementState(), currentFloor, serviceDirection, elevatorNumber, requestQueue.isEmpty());
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
			elevatorSubsystem.handleElevatorMonitorUpdate(makeElevatorMonitor());
		}
	}
}
