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
	
	// NOTE: should these immutable constants be accessed globally or passed along? 
	// passing along creates dependencies

	/**
	 * Constructor for Structure.
	 *
	 * @param numberOfFloors
	 * @param numberOfElevators
	 */
	public Structure(int numberOfFloors, int numberOfElevators) {
		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;
	}

	public static void main(String[] args) {

		Thread scheduler, elevatorSubsystem, floorSubsystem;
		BoundedBuffer schedulerElevatorBuffer, schedulerFloorsubBuffer;

		schedulerElevatorBuffer = new BoundedBuffer();
		schedulerFloorsubBuffer = new BoundedBuffer();

		scheduler = new Thread(new Scheduler(schedulerElevatorBuffer, schedulerFloorsubBuffer), "Scheduler");
		elevatorSubsystem = new Thread(new ElevatorSubsystem(schedulerElevatorBuffer), "Elevator Subsystem");
		floorSubsystem = new Thread(new FloorSubsystem(schedulerFloorsubBuffer), "Floor Subsystem");

		scheduler.start();
		elevatorSubsystem.start();
		floorSubsystem.start();
	}
}
