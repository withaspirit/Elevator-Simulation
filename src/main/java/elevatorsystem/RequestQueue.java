package elevatorsystem;

import requests.ElevatorRequest;
import requests.ServiceRequest;
import systemwide.Direction;

import java.util.Collections;
import java.util.TreeSet;

import static elevatorsystem.Elevator.*;

/**
 * RequestQueue maintains queues of floor numbers for an elevator to visit.
 * It also provides methods to manage and modify the queues.
 *
 * @author Julian, Liam Tripp
 */
public class RequestQueue {

	private volatile TreeSet<Integer> currentDirectionQueue;
	private volatile TreeSet<Integer> oppositeDirectionQueue;
	/**
	 * MissedRequests is for requests in the elevators' serviceDirection whose
	 * floorNumbers are below (if serviceDirection is UP) or above
	 * (if serviceDirection is DOWN) the elevator's floor.
	 */
	private final TreeSet<Integer> missedRequests;

	/**
	 * Constructor for the class
	 */
	public RequestQueue() {
		currentDirectionQueue = new TreeSet<>();
		oppositeDirectionQueue = new TreeSet<>(Collections.reverseOrder());
		missedRequests = new TreeSet<>();
	}

	/**
	 * Adds the floor numbers of a ServiceRequest to the RequestQueue.
	 *
	 * @param elevatorFloorNumber the floorNumber of the elevator (nextFloor if moving, currentFloor is stopped)
	 * @param serviceDirection the direction that the elevator is currently serving
	 * @param request the ServiceRequest to be added to the RequestQueue.
	 */
	public void addRequest(int elevatorFloorNumber, Direction serviceDirection, ServiceRequest request) {
		int floorNumber = request.getFloorNumber();
		Direction requestDirection = request.getDirection();

		if (floorNumber < 0 || elevatorFloorNumber < 0) {
			throw new IllegalArgumentException("FloorNumber must be greater than zero.");
		}

		TreeSet<Integer> queueToAddTo;
		// if the elevator's floor number == request floor number
		if (elevatorFloorNumber == floorNumber) {
			// if serviceDirection is the same as the request direction,
			if (serviceDirection == requestDirection) {
				queueToAddTo = currentDirectionQueue;
			} else {
				queueToAddTo = oppositeDirectionQueue;
			}
		} else {
			// request is in same direction as elevator
			if (serviceDirection == requestDirection) {

				// elevator can serve requests
				// case: requestFloor is above elevatorFloor and request direction is Up
				// OR requestFloor is below elevatorFloor and serviceDirection is DOwn
				if ((floorNumber < elevatorFloorNumber && serviceDirection == Direction.DOWN) ||
						(floorNumber > elevatorFloorNumber && serviceDirection == Direction.UP)) {
					queueToAddTo = currentDirectionQueue;
				} else {
					// elevator can't serve request this cycle
					queueToAddTo = missedRequests;
				}
			} else {
				// serviceDirection is opposite direction to elevatorDirection
				queueToAddTo = oppositeDirectionQueue;
			}
		}
		// add to selected queue
		queueToAddTo.add(floorNumber);
		if (request instanceof ElevatorRequest elevatorRequest) {
			queueToAddTo.add(elevatorRequest.getDesiredFloor());
		}
	}

	/**
	 * Removes a request from the head of the currentDirectionQueue.
	 *
	 * @return the request at the head of the currentDirectionQueue, -1 if queue is empty
	 */
	public int removeRequest() {
		if (!currentDirectionQueue.isEmpty()) {
			return currentDirectionQueue.pollFirst();
		} else {
			return -1;
		}
	}

	/**
	 * Returns the next floor in queue for the direction
	 *
	 * @return nextFloor the next floor in queue, -1 if the currentDirectionQueue is empty
	 */
	public int peekNextRequest() {
		if (!currentDirectionQueue.isEmpty()) {
			return currentDirectionQueue.first();
		} else {
			System.err.println("RequestQueue.peekNextFloor should not be accessed " +
					"while the active queue is empty. Swapping should be done beforehand.");
			return -1;
		}
	}

	/**
	 * Swaps the currentDirectionQueue if necessary.
	 *
	 * @return true if swapped to queue in opposite direction of current queue, false otherwise
	 */
	public boolean swapQueues() {
		boolean status = false;

		if (currentDirectionQueue.isEmpty()) {
			// add any missed requests to current queue
			while (!missedRequests.isEmpty()) {
				currentDirectionQueue.add(missedRequests.pollFirst());
			}

			// switch to opposite direction queue if possible
			if (!oppositeDirectionQueue.isEmpty()) {
				TreeSet<Integer> tempQueue = currentDirectionQueue;
				currentDirectionQueue = oppositeDirectionQueue;
				oppositeDirectionQueue = tempQueue;
				status = true;
			}
		}
		return status;
	}

	/**
	 * Determines whether the RequestQueue is empty.
	 *
	 * @return true if all of the RequestQueue's queues are empty, false otherwise
	 */
	public boolean isEmpty() {
		return isCurrentQueueEmpty() && isOppositeQueueEmpty() && isMissedQueueEmpty();
	}

	/**
	 * Determines whether the RequestQueue's currentQueue is empty.
	 *
	 * @return true if the RequestQueue's active queue is empty, false otherwise
	 */
	public boolean isCurrentQueueEmpty() {
		return currentDirectionQueue.isEmpty();
	}

	/**
	 * Determines whether the RequestQueue is empty in the opposite direction.
	 *
	 * @return true if the requestQueue's active queue is empty, false otherwise
	 */
	public boolean isOppositeQueueEmpty() {
		return oppositeDirectionQueue.isEmpty();
	}

	/**
	 * Returns the occupancy status of the missed requests queue
	 *
	 * @return status true if empty
	 */
	public boolean isMissedQueueEmpty() {
		return missedRequests.isEmpty();
	}

	/**
	 * Prints the various queues in RequestQueue.
	 */
	public void printQueue() {
		if (!isCurrentQueueEmpty()) {
			System.out.println("CurrentDirectionQueue: " + currentDirectionQueue.toString());
		}
		if (!isOppositeQueueEmpty()) {
			System.out.println("OppositeDirectionQueue: " + oppositeDirectionQueue.toString());
		}
		if (!isMissedQueueEmpty()) {
			System.out.println("MissedQueue: " + missedRequests.toString());
		}
	}

	/**
	 * Gets the total expected time that the elevator will need to take to
	 * perform its current requests along with the new elevatorRequest.
	 *
	 * @param initialFloor the floor the elevator starts at
	 * @return a double containing the elevator's total expected queue time
	 */
	public double getExpectedTime(int initialFloor) {
		double queueTime = 0;

		for (int floor: currentDirectionQueue) {
			if (initialFloor != floor) {
				queueTime += LOAD_TIME + requestTime(initialFloor, floor);
				initialFloor = floor;
			}
		}

		for (int floor: oppositeDirectionQueue) {
			if (initialFloor != floor) {
				queueTime += LOAD_TIME + requestTime(initialFloor, floor);
				initialFloor = floor;
			}
		}

		for (int floor: missedRequests) {
			if (initialFloor != floor) {
				queueTime += LOAD_TIME + requestTime(initialFloor, floor);
				initialFloor = floor;
			}
		}

		return queueTime;
	}

	/**
	 * Gets the expected time of a new request for the current elevator
	 * based on distance.
	 *
	 * @param initialFloor the floor the elevator starts at
	 * @param finalFloor the destination floor for the elevator to stop at
	 * @return a double containing the time to fulfil the request
	 */
	public double requestTime(int initialFloor, int finalFloor) {
		double distance = Math.abs(finalFloor - initialFloor) * FLOOR_HEIGHT;
		if (distance > ACCELERATION_DISTANCE * 2) {
			return (distance - ACCELERATION_DISTANCE * 2) / MAX_SPEED + ACCELERATION_TIME * 2;
		} else {
			return Math.sqrt(distance * 2 / ACCELERATION); // elevator accelerates and decelerates continuously
		}
	}
}
