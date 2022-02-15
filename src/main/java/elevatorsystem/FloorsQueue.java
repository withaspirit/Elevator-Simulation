package elevatorsystem;

import java.util.*;

/**
 * 
 * 
 * @author Julian
 */
public class FloorsQueue {

	private PriorityQueue<Integer> upwardRequests;
	private PriorityQueue<Integer> downwardRequests;
	
	public FloorsQueue() {
		this.upwardRequests =  new PriorityQueue<Integer>();
		this.downwardRequests =  new PriorityQueue<Integer>(Collections.reverseOrder());
	}

	public void addFloor(int floorNum, String direction) throws Exception {
		if (floorNum < 0) {
			throw new Exception("Invalid floor number");
		}
		
		if (direction == "Up") {
			upwardRequests.add(floorNum);
		} else if (direction == "Down") {
			downwardRequests.add(floorNum);
		} else {
			throw new Exception("Direction is invalid");
		}
	}
	
	public int visitNextFloor(String direction) {
		if (direction == "Up") {
			return upwardRequests.remove();
		} else if (direction == "Down") {
			return downwardRequests.remove();
		} else {
			return -1;
		}
	}
	
	public int peekNextFloor(String direction) {
		if (direction == "Up") {
			return upwardRequests.peek();
		} else if (direction == "Down") {
			return downwardRequests.peek();
		} else {
			return -1;
		}
	}
	
	public int isEmpty() {
		int status = 0;
		
		if (!upwardRequests.isEmpty() && !downwardRequests.isEmpty()){
			status = 3;
		} else if (!downwardRequests.isEmpty()){
			status = 2;
		} else if (!upwardRequests.isEmpty()) {
			status = 1;
		}
		
		return status;
	}
}
