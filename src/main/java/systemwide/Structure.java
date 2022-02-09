package systemwide;

import elevatorsystem.ElevatorSubsystem;
import floorsystem.FloorSubsystem;
import scheduler.Scheduler;
import misc.BoundedBuffer;

/**
 * Structure instantiates the overall system.
 * 
 * @author Liam Tripp, Julian
 */
public class Structure {
	
	private int numberOfFloors;
	private int numberOfElevators;

	/**
	 * Constructor for Structure.
	 *
	 * @param numberOfFloors the number of floors in the structure
	 * @param numberOfElevators the number of elevators in the structure
	 */
	public Structure(int numberOfFloors, int numberOfElevators) {
		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;
	}

	/**
	 * Returns the number of floors in the structure.
	 *
	 * @return the number of floors
	 */
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	/**
	 * Sets the number of floors in the structure.
	 *
	 * @param numberOfFloors number of floors
	 */
	public void setNumberOfFloors(int numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	/**
	 * Returns the number of elevators in the structure.
	 *
	 * @return the number of elevators
	 */
	public int getNumberOfElevators() {
		return numberOfElevators;
	}

	/**
	 * Returns the number of elevators in the structure.
	 *
	 * @param numberOfElevators number of elevators
	 */
	public void setNumberOfElevators(int numberOfElevators) {
		this.numberOfElevators = numberOfElevators;
	}

	public static void main(String[] args) {

		Thread scheduler, elevatorSubsystem, floorSubsystem;
		BoundedBuffer elevatorSubsystemBuffer, floorSubsystemBuffer;

		elevatorSubsystemBuffer = new BoundedBuffer();
		floorSubsystemBuffer = new BoundedBuffer();

		scheduler = new Thread(new Scheduler(elevatorSubsystemBuffer, floorSubsystemBuffer), "Scheduler");
		elevatorSubsystem = new Thread(new ElevatorSubsystem(elevatorSubsystemBuffer), "Elevator Subsystem");
		floorSubsystem = new Thread(new FloorSubsystem(floorSubsystemBuffer), "Floor Subsystem");

		scheduler.start();
		elevatorSubsystem.start();
		floorSubsystem.start();
	}
}
