package elevatorsystem;

/**
 * Doors is the mechanism that opens and closes the doors.
 *
 * @author Ramit Mahajan
 */
public class Doors {

    private State state;

    /**
     * Constructor for Doors.
     */
    public Doors() {
        this.state = State.OPEN;
    }

    /**
     * Declaring the states of the door.
     */
    public enum State {
        OPEN,
        CLOSED,
        STUCK
    }

    /**
     * Gets the state of the doors.
     *
     * @return the current state of the doors
     */
    public State getState() {
        return state;
    }

    /**
     * Checks if the gates are open.
     *
     * @return state of the door as open
     */
    public boolean areOpen() {
        return state == State.OPEN;
    }

    /**
     * Checks if the gates are closed.
     *
     * @return state of the door as closed
     */
    public boolean areClosed() {
        return state == State.CLOSED;
    }

    /**
     * Checks if the gates are stuck.
     *
     * @return state of the door as stuck
     */
    public boolean areStuck() {
        return state == State.STUCK;
    }

    /**
     * Open the doors.
     */
    public void open() {
        this.state = State.OPEN;
    }

    /**
     * Closes the doors.
     */
    public void close() {
        this.state = State.CLOSED;
    }

    /**
     * State of the door is stuck.
     */
    public void setToStuck() {
        this.state = State.STUCK;
    }
}

