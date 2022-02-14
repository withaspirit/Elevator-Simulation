package systemwide;

import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import floorsystem.Floor;
import floorsystem.FloorSubsystem;
import scheduler.Scheduler;
import misc.BoundedBuffer;

import java.util.ArrayList;

/**
 * Structure instantiates the overall system.
 * 
 * @author Liam Tripp, Julian
 */
public class Structure {
	
	private int numberOfFloors;
	private int numberOfElevators;
	private ArrayList<Elevator> elevatorList;
	private ArrayList<Floor> floorList;

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
	 * Initializes the Structure.
	 */
	public void initializeStructure() {
		BoundedBuffer elevatorSubsystemBuffer = new BoundedBuffer();
		BoundedBuffer floorSubsystemBuffer = new BoundedBuffer();

		for (int i = 0; i < numberOfElevators; i++) {
			ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorSubsystemBuffer);
			elevatorList.add(new Elevator());
		}
		for (int i = 0; i < numberOfFloors; i++) {
			FloorSubsystem floorSubsystem = new FloorSubsystem(floorSubsystemBuffer);
			floorList.add(new Floor(i));
		}
		Scheduler scheduler = new Scheduler(elevatorSubsystemBuffer, floorSubsystemBuffer);
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

		Structure structure = new Structure(10, 1);

		structure.initializeStructure();
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
