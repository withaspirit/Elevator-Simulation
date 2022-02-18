package systemwide;

import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import floorsystem.Floor;
import floorsystem.FloorSubsystem;
import scheduler.Scheduler;
import systemwide.BoundedBuffer;

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

	/**
	 * Initializes the Structure's properties.
	 */
	public void initializeStructure() {
		BoundedBuffer elevatorSubsystemBuffer = new BoundedBuffer();
		BoundedBuffer floorSubsystemBuffer = new BoundedBuffer();

		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorSubsystemBuffer);
		for (int i = 0; i < numberOfElevators; i++) {
			Elevator elevator = new Elevator(elevatorSubsystem);
			elevatorSubsystem.addElevator(elevator);
		}

		FloorSubsystem floorSubsystem = new FloorSubsystem(floorSubsystemBuffer);
		for (int i = 0; i < numberOfFloors; i++) {
			Floor floor = new Floor(i, floorSubsystem);
			floorSubsystem.addFloor(floor);
		}

		Scheduler scheduler = new Scheduler(elevatorSubsystemBuffer, floorSubsystemBuffer);

		Thread schedulerThread, elevatorSubsystemThread, floorSubsystemThread;

		schedulerThread = new Thread(scheduler, scheduler.getClass().getSimpleName());
		elevatorSubsystemThread = new Thread(elevatorSubsystem, elevatorSubsystem.getClass().getSimpleName());
		floorSubsystemThread = new Thread(floorSubsystem, floorSubsystem.getClass().getSimpleName());

		schedulerThread.start();
		elevatorSubsystemThread.start();
		floorSubsystemThread.start();
	}

	public static void main(String[] args) {
		Structure structure = new Structure(10, 1);
		structure.initializeStructure();
	}
}
