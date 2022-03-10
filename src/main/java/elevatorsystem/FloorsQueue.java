package elevatorsystem;

import java.util.*;

import systemwide.Direction;

/**
 * FloorsQueue maintains queues of floors to visit for an elevator.
 * It also provides methods to manage and modify the lists.
 * 
 * @author Julian, Liam Tripp
 */
public class FloorsQueue {

	private PriorityQueue<Integer> upwardRequests;
	private PriorityQueue<Integer> downwardRequests;
	private Queue<Integer> missedRequests;
	private volatile PriorityQueue<Integer> currentDirectionQueue;

	/**
	 * Constructor for the class
	 */
	public FloorsQueue() {
		this.upwardRequests = new PriorityQueue<>();
		this.downwardRequests = new PriorityQueue<>(Collections.reverseOrder());
		this.missedRequests = new LinkedList<>();
		currentDirectionQueue = upwardRequests;
	}

	/**
	 * Adds a floor to be visited
	 *
	 * @param floorNum  the number of the floor to be visited
	 * @param desiredFloor the floor that a person travels to
	 * @param serviceDirection the direction the elevator comes to the floor
	 */
	public void addFloor(int floorNum, int currFloor, int desiredFloor, Direction serviceDirection) {
		if (floorNum < 0) {
			throw new RuntimeException("Invalid floor number");
		}

		if (serviceDirection == Direction.UP) {
			if (floorNum > currFloor) {
				upwardRequests.add(floorNum);
				upwardRequests.add(desiredFloor);
			} else {
				missedRequests.add(floorNum);
				missedRequests.add(desiredFloor);
			}

		} else if (serviceDirection == Direction.DOWN) {
			if (floorNum < currFloor) {
				downwardRequests.add(floorNum);
				downwardRequests.add(desiredFloor);
			} else {
				missedRequests.add(floorNum);
				missedRequests.add(desiredFloor);
			}
		} else {
			throw new RuntimeException("Direction is invalid");
		}
	}

	/**
	 * Removes a request from the head of the currentDirectionQueue.
	 *
	 * @return the request at the head of the currentDirectionQueue, -1 if queue is empty
	 */
	public int removeRequest() {
		if (!currentDirectionQueue.isEmpty()) {
			return currentDirectionQueue.remove();
		} else {
			return -1;
		}
	}

	/**
	 * Removes the next floor to flag that the floor has been visited
	 * 
	 * @param serviceDirection the serviceDirection the elevator came to the floor
	 * @return floorVisited the floor that has been visited, -1 if not successful
	 */
	public int visitNextFloor(Direction serviceDirection) {
		int floorVisited = -1;

		if (serviceDirection == Direction.UP) {
			if (!upwardRequests.isEmpty()) {
				floorVisited = upwardRequests.remove();
			}
		} else if (serviceDirection == Direction.DOWN) {
			if (!downwardRequests.isEmpty()) {
				floorVisited = downwardRequests.remove();
			}
		} else {
			throw new RuntimeException("Direction is invalid");
		}
		return floorVisited;
	}

	/**
	 * Returns the next floor in queue for the direction
	 *
	 * @return nextFloor the next floor in queue, -1 if the currentDirectionQueue is empty
	 */
	public int peekNextRequest() {
		if (!currentDirectionQueue.isEmpty()) {
			return currentDirectionQueue.peek();
		} else {
			System.err.println("FloorQueue.peekNextFloor should not be accessed " +
					"while the active queue is empty. Swapping should be done beforehand.");
			return -1;
		}
	}

	/**
	 * Returns the next floor in queue for the direction
	 * 
	 * @param direction the direction wanting to peek
	 * @return nextFloor the next floor in queue, -1 if not successful
	 */
	public int peekNextFloor(Direction direction) {
		int nextFloor = -1;
		if (direction == Direction.UP) {
			if (!upwardRequests.isEmpty()) {
				nextFloor = upwardRequests.peek();
			}
		} else if (direction == Direction.DOWN) {
			if (!downwardRequests.isEmpty()) {
				nextFloor = downwardRequests.peek();
			}
		} else {
			throw new RuntimeException("Direction is invalid");
		}
		return nextFloor;
	}

	/**
	 * Updates / Swaps the queues to include the missed requests
	 *
	 * @param serviceDirection the direction of the queue wanting to swap
	 * @return status 0 if successful, -1 if not successful
	 */
	// FIXME: change from int to boolean
	public int swapQueues(Direction serviceDirection) {
		int status = -1;

		if (currentDirectionQueue.isEmpty()) {
			// add any missed requests to current queue
			while (!missedRequests.isEmpty()) {
				currentDirectionQueue.add(missedRequests.remove());
			}

			// switch to opposite direction queue if possible
			/*
			if (!oppositeDirectionQueue.isEmpty()) {
				PriorityQueue<Integer> tempQueue = currentDirectionQueue;
				currentDirectionQueue = oppositeDirectionQueue;
				oppositeDirectionQueue = tempQueue;
				status = 0;
			}
			 */
			if (serviceDirection == Direction.UP) {
				if (!downwardRequests.isEmpty()) {
					currentDirectionQueue = downwardRequests;
					status = 0;
				}
			} else if (serviceDirection == Direction.DOWN) {
				if (!upwardRequests.isEmpty()) {
					currentDirectionQueue = upwardRequests;
					status = 0;
				}
			} else {
				throw new RuntimeException("Direction is invalid");
			}
		}

		/*
		if (direction == Direction.UP && upwardRequests.isEmpty()) {
			while (!missedRequests.isEmpty()) {
				upwardRequests.add(missedRequests.remove());
			}
			status = 0;
		} else if (direction == Direction.DOWN && downwardRequests.isEmpty()) {
			while (!missedRequests.isEmpty()) {
				downwardRequests.add(missedRequests.remove());
			}
			status = 0;
		}
		 */
		return status;
	}

	/**
	 * Determines whether the FloorsQueue is empty.
	 *
	 * @return true if the floorsQueue's active queue is empty, false otherwise
	 */
	public boolean isCurrentQueueEmpty() {
		return currentDirectionQueue.isEmpty();
	}

	/**
	 * Returns the occupancy status of the upward queue
	 * 
	 * @return status true if empty
	 */
	public boolean isUpqueueEmpty() {
		return upwardRequests.isEmpty();
	}

	/**
	 * Returns the occupancy status of the downward queue
	 * 
	 * @return status true if empty
	 */
	public boolean isDownqueueEmpty() {
		return downwardRequests.isEmpty();
	}

	/**
	 * Returns the occupancy status of the missed requests queue
	 * 
	 * @return status true if empty
	 */
	public boolean isMissedqueueEmpty() {
		return missedRequests.isEmpty();
	}
}
