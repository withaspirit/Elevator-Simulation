package systemwide;

import elevatorsystem.ElevatorSubsystem;
import floorsystem.FloorSubsystem;
import scheduler.Scheduler;
import misc.BoundedBuffer;

/**
 * 
 * 
 * @author Liam Tripp, Julian
 */
public class Structure {
	
	private final int numberOfFloors;
	private final int numberOfElevators;
	
	// NOTE: should these immutable constants be accessed globally or passed along? 
	// passing along creates dependencies
	public Structure(int numberOfFloors, int numberOfElevators) {
		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;
	}
	
	/**
	 * @param args
	 */
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
