package elevatorsystem;

import requests.ElevatorRequest;
import requests.ServiceRequest;
import systemwide.Direction;

import java.util.Collections;
import java.util.TreeSet;

import static elevatorsystem.Elevator.*;

/**
 * RequestQueue maintains queues of serviceRequests that indicate
 * the floors  for an elevator to visit. It also provides methods
 * to manage and modify the queues.
 *
 * @author Julian, Liam Tripp
 */
public class RequestQueue {

	private volatile TreeSet<ServiceRequest> currentDirectionQueue;
	private volatile TreeSet<ServiceRequest> oppositeDirectionQueue;
	/**
	 * MissedRequests is for requests in the elevators' serviceDirection whose
	 * floorNumbers are below (if serviceDirection is UP) or above
	 * (if serviceDirection is DOWN) the elevator's floor.
	 */
	private final TreeSet<ServiceRequest> missedRequests;

	private final int travelTime, loadTime;

	/**
	 * Constructor for RequestQueue.
	 */
	public RequestQueue(int travelTime, int loadTime) {
		currentDirectionQueue = new TreeSet<>();
		oppositeDirectionQueue = new TreeSet<>(Collections.reverseOrder());
		missedRequests = new TreeSet<>();
		this.travelTime = travelTime;
		this.loadTime = loadTime;
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

		TreeSet<ServiceRequest> queueToAddTo;
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
		if (request instanceof ElevatorRequest elevatorRequest) {
			ServiceRequest serviceRequest1 = new ServiceRequest(request.getTime(), elevatorRequest.getDesiredFloor(), request.getDirection(), request.getOrigin());
			ServiceRequest serviceRequest2 = new ServiceRequest(request.getTime(), request.getFloorNumber(), request.getDirection(), request.getOrigin());
			queueToAddTo.add(serviceRequest1);
			queueToAddTo.add(serviceRequest2);
		} else {
			queueToAddTo.add(request);
		}
	}

	/**
	 * Removes a request from the head of the currentDirectionQueue.
	 *
	 * @return the request at the head of the currentDirectionQueue, -1 if queue is empty
	 */
	public ServiceRequest removeRequest() {
		if (!currentDirectionQueue.isEmpty()) {
			return currentDirectionQueue.pollFirst();
		} else {
			return null;
		}
	}

	/**
	 * Returns the next floor in queue for the direction.
	 *
	 * @return nextFloor the next floor in queue, -1 if the currentDirectionQueue is empty
	 */
	public ServiceRequest peekNextRequest() {
		if (!currentDirectionQueue.isEmpty()) {
			return currentDirectionQueue.first();
		} else {
			System.err.println("RequestQueue.peekNextFloor should not be accessed " +
					"while the active queue is empty. Swapping should be done beforehand.");
			return null;
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
				TreeSet<ServiceRequest> tempQueue = currentDirectionQueue;
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
	 * Returns the occupancy status of the missed RequestQueue.
	 *
	 * @return status true if empty
	 */
	public boolean isMissedQueueEmpty() {
		return missedRequests.isEmpty();
	}

	/**
	 * Prints the various queues in RequestQueue.
	 */
	@Override
	public String toString() {
		String messageToPrint = "";
		if (!isCurrentQueueEmpty()) {
			messageToPrint += "CurrentDirectionQueue: " + currentDirectionQueue.toString() + "\n";
		}
		if (!isOppositeQueueEmpty()) {
			messageToPrint += "OppositeDirectionQueue: " + oppositeDirectionQueue.toString() + "\n";
		}
		if (!isMissedQueueEmpty()) {
			messageToPrint += "MissedQueue: " + missedRequests.toString() + "\n";
		}
		return messageToPrint;
	}

	/**
	 * Gets the total expected time that the elevator will need to take to
	 * perform its current requests along with the new elevatorRequest.
	 *
	 * @param elevatorFloor the floor the elevator starts at
	 * @return a double containing the elevator's total expected queue time
	 */
	public double getExpectedTime(int elevatorFloor) {
		double queueTime = 0;

		for (ServiceRequest request: currentDirectionQueue) {
			int floor = request.getFloorNumber();
			if (elevatorFloor != floor) {
				queueTime += loadTime + requestTime(elevatorFloor, floor);
				elevatorFloor = floor;
			}
		}

		for (ServiceRequest request: oppositeDirectionQueue) {
			int floor = request.getFloorNumber();
			if (elevatorFloor != floor) {
				queueTime += loadTime + requestTime(elevatorFloor, floor);
				elevatorFloor = floor;
			}
		}

		for (ServiceRequest request: missedRequests) {
			int floor = request.getFloorNumber();
			if (elevatorFloor != floor) {
				queueTime += loadTime + requestTime(elevatorFloor, floor);
				elevatorFloor = floor;
			}
		}

		return queueTime/1000;
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
		return Math.abs((finalFloor - initialFloor) * travelTime);
//		double distance = Math.abs(finalFloor - initialFloor) * FLOOR_HEIGHT;
//		if (distance > ACCELERATION_DISTANCE * 2) {
//			return (distance - ACCELERATION_DISTANCE * 2) / MAX_SPEED + ACCELERATION_TIME * 2;
//		} else {
//			return Math.sqrt(distance * 2 / ACCELERATION); // elevator accelerates and decelerates continuously
//		}
	}
}
