package elevatorsystem;

/**
 * MovementState indicates the state of movement activity of the elevator.
 *
 * @author Liam Tripp, Brady Norton
 */
public enum MovementState {
    IDLE("IDLE"),
    ACTIVE("ACTIVE"),
    STUCK("STUCK");

    private String name;


    /**
     * Constructor for MovementState class
     * @param name the name of the MovementState
     */
    MovementState(String name){ this.name = name;}


    /**
     * Gets the name of the MovementState as a String
     *
     * @return name of MovementState
     */
    public String getName(){ return name; }


    /**
     * Returns the MovementState with the specified name
     *
     * @param s the name of the MovementState as a String
     * @return the MovementState and it's name
     */
    public static MovementState getState(String s) {
        if ("IDLE".equals(s)) {return IDLE;}
        else if ("ACTIVE".equals(s)) {return ACTIVE;}
        else {return STUCK;}
    }
}
