package elevatorsystem;

import floorsystem.ArrivalSensor;

/**
 * Fault represents the different error states of the Elevator.
 *
 * @author Liam Tripp
 */
public enum Fault {
    ARRIVAL_SENSOR_FAIL(ArrivalSensor.class.getSimpleName() + " Failed"),
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
     * Gets the name of the Fault as a String.
     *
     * @return name of the Fault.
     */
    public String getName() {
        return name;
    }
}
