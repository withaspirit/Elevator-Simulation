package systemwide;

import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import floorsystem.Floor;
import floorsystem.FloorSubsystem;
import scheduler.Scheduler;

import java.util.ArrayList;

/**
 * Structure instantiates the overall system.
 * 
 * @author Liam Tripp, Julian, Ryan Dash
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

		ArrayList<ElevatorSubsystem> elevatorList = new ArrayList<>();
		for (int elevatorNumber = 1; elevatorNumber <= numberOfElevators; elevatorNumber++) {
			Elevator elevator = new Elevator(elevatorNumber, elevatorSubsystemBuffer);
			new Thread (elevator, elevator.getClass().getSimpleName()).start();
			elevatorList.add(elevator.getElevatorSubsystem());
		}

		FloorSubsystem floorSubsystem = new FloorSubsystem(floorSubsystemBuffer);
		for (int i = 0; i < numberOfFloors; i++) {
			Floor floor = new Floor(i, floorSubsystem);
			floorSubsystem.addFloor(floor);
		}

		Scheduler scheduler = new Scheduler(elevatorSubsystemBuffer, floorSubsystemBuffer, elevatorList);

		Thread schedulerOrigin, floorSubsystemOrigin;

		schedulerOrigin = new Thread(scheduler, scheduler.getClass().getSimpleName());
		floorSubsystemOrigin = new Thread(floorSubsystem, floorSubsystem.getClass().getSimpleName());

		schedulerOrigin.start();
		floorSubsystemOrigin.start();
	}

	public static void main(String[] args) {
		Structure structure = new Structure(10, 1);
		structure.initializeStructure();
	}
}
