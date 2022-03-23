package elevatorsystem;

import floorsystem.ArrivalSensor;

/**
 * Fault represents the different error states of the Elevator.
 *
 * @author Liam Tripp
 */
public enum Fault {
	ARRIVAL_SENSOR_FAULT(ArrivalSensor.class.getSimpleName() + " Fault"),
	DOORS_STUCK("Doors Stuck"),
	DOORS_INTERRUPTED("Doors Interrupted"),
	ELEVATOR_STUCK("Elevator Stuck"),
	NONE("None");

	private String name = new String("ASDF");

	/**
	 * Constructor for Fault.
	 *
	 * @param name name of the fault as displayed by the console and GUI
	 */
	Fault(String name) {
		this.name = name;
	}
}
