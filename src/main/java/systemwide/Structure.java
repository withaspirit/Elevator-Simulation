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
	
	private final int numberOfFloors;
	private final int numberOfElevators;

	/**
	 * Constructor for Structure.
	 *
	 * @param numberOfFloors the number of floors in the structure
	 * @param numberOfElevators
	 */
	public Structure(int numberOfFloors, int numberOfElevators) {
		this.numberOfFloors = numberOfFloors;
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
