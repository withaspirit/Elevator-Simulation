package elevatorsystem;

import java.util.*;
import systemwide.Direction;

/**
 * Data Structure that manages the floors to visit by the elevator
 * 
 * @author Julian
 */
public class FloorsQueue {

	private PriorityQueue<Integer> upwardRequests;
	private PriorityQueue<Integer> downwardRequests;
	private Queue<Integer> missedRequests;

	/**
	 * Constructor for the class
	 */
	public FloorsQueue() {
		this.upwardRequests = new PriorityQueue<Integer>();
		this.downwardRequests = new PriorityQueue<Integer>(Collections.reverseOrder());
		this.missedRequests = new LinkedList<>();
	}

	/**
	 * Adds a floor to be visited
	 * 
	 * @param floorNum  the number of the floor to be visited
	 * @param direction the direction the elevator comes to the floor
	 */
	public void addFloor(int floorNum, int currFloor, Direction direction) {
		if (floorNum < 0) {
			throw new RuntimeException("Invalid floor number");
		}

		if (direction == Direction.UP) {
			if (floorNum > currFloor) {
				upwardRequests.add(floorNum);
			} else {
				missedRequests.add(floorNum);
			}
			
		} else if (direction == Direction.DOWN) {
			if (floorNum < currFloor) {
				downwardRequests.add(floorNum);
			} else {
				missedRequests.add(floorNum);
			}
		} else {
			throw new RuntimeException("Direction is invalid");
		}
	}

	/**
	 * Removes the next floor to flag that the floor has been visited
	 * 
	 * @param direction the direction the elevator came to the floor
	 * @return floorVisited the floor that has been visited, -1 if not successful
	 */
	public int visitNextFloor(Direction direction) {
		int floorVisited = -1;

		if (direction == Direction.UP) {
			if (!upwardRequests.isEmpty()) {
				floorVisited = upwardRequests.remove();
				if (upwardRequests.isEmpty()) {    //It automatically swaps if the upward queue is emptied
					swapQueues(direction);	
				}
			}
		} else if (direction == Direction.DOWN) {
			if (!downwardRequests.isEmpty()) {
				floorVisited = downwardRequests.remove();
				if (downwardRequests.isEmpty()) {  //It automatically swaps if the downward queue is emptied
					swapQueues(direction);
				}
			}
		} else {
			throw new RuntimeException("Direction is invalid");
		}
		return floorVisited;
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
	 * @param direction the direction of the queue wanting to swap
	 * @return status 0 if successful, -1 if not successful
	 */
	public int swapQueues(Direction direction) {
		int status = -1;
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
		return status;
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
	 * Returns the occupancy status of the downward queue
	 * 
	 * @return status true if empty
	 */
	public boolean isMissedqueueEmpty() {
		return missedRequests.isEmpty();
	}
}
