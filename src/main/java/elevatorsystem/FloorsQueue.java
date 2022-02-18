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

	/**
	 * Constructor for the class
	 */
	public FloorsQueue() {
		this.upwardRequests = new PriorityQueue<Integer>();
		this.downwardRequests = new PriorityQueue<Integer>(Collections.reverseOrder());
	}

	/**
	 * Adds a floor to be visited
	 * 
	 * @param floorNum  the number of the floor to be visited
	 * @param direction the direction the elevator comes to the floor
	 */
	public void addFloor(int floorNum, Direction direction) {
		if (floorNum < 0) {
			throw new RuntimeException("Invalid floor number");
		}

		if (direction == Direction.UP) {
			upwardRequests.add(floorNum);
		} else if (direction == Direction.DOWN) {
			downwardRequests.add(floorNum);
		} else {
			throw new RuntimeException("Direction is invalid");
		}
		System.out.println("\nFloor #" + floorNum + " QueueUP# "+ upwardRequests.size()+ " QueueDOWN# "+ downwardRequests.size()+"\n");
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
			}
		} else if (direction == Direction.DOWN) {
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
	 * Returns the occupancy status of the queues
	 * 
	 * @return status the status of the queues, 3 for both not empty, 2 for
	 *         downwardRequest not empty, 1 for upwardRequest not empty, 0 for both
	 *         empty
	 */
	public int isEmpty() {
		int status = 0;

		if (!upwardRequests.isEmpty() && !downwardRequests.isEmpty()) {
			status = 3;
		} else if (!downwardRequests.isEmpty()) {
			status = 2;
		} else if (!upwardRequests.isEmpty()) {
			status = 1;
		}

		return status;
	}
}
