package elevatorsystem;

/**
 * Doors is the mechanism that opens and closes the doors
 *
 * @author Ramit Mahajan
 */
public class Doors {
	
	private State state;

	/**
	 * Constructor for Doors.
	 */
	public Doors(){
		this.state = State.CLOSE;
	}
	
	/**
	 * Declaring the states of the door
	 */
	public enum State { 
		OPEN,
		CLOSE;
	}
	
	/**
     * Gets the state of the doors
     *
     * @return the current state of the doors
     */
	public State getState() {
		return state;
	}
	
	/**
	 * Open the doors.
	 */
	public void opendoors() {
		this.state = State.OPEN;
	}
	
	/**
	 * Closes the doors.
	 */
	public void closedoor() {
		this.state = State.CLOSE;
	}	
}

