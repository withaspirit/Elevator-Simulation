package elevatorsystem;

import floorsystem.ArrivalSensor;

/**
 * Fault represents the different error states of the Elevator.
 *
 * @author Liam Tripp, Ryan Dash
 */
public enum Fault {
    ARRIVAL_SENSOR_FAIL(ArrivalSensor.class.getSimpleName() + " Failed"),
    DOORS_STUCK("Doors Stuck"),
    DOORS_INTERRUPTED("Doors Interrupted"),
    ELEVATOR_STUCK("Elevator Stuck"), // Emergency stop
    NONE("None");

    private final String name;

    /**
     * Constructor for Fault.
     *
     * @param name name of the fault as displayed by the console and GUI
     */
    Fault(String name) {
        this.name = name;
    }

    /**
     * Returns the Fault with the specified name.
     *
     * @param name the name of the fault
     * @return the Fault with the specified name
     */
    public static Fault getFault(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException iae) {
            System.out.println("Fault is incorrect: " + name.toUpperCase());
            iae.printStackTrace();
            return Fault.NONE;
        }
    }

    /**
     * Gets the name of the Fault as a String.
     *
     * @return name of the Fault.
     */
    public String getName() {
        return name;
    }
}
