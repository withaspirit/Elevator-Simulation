package systemwide;

import client_server_host.Port;
import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import floorsystem.Floor;
import floorsystem.FloorSubsystem;
import scheduler.Presenter;
import scheduler.Scheduler;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Structure instantiates the overall system.
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Structure implements Serializable {

	private int numberOfFloors;
	private int numberOfElevators;
	private boolean timeToggle;

	/**
	 * Constructor for Structure.
	 *
	 * @param numberOfFloors the number of floors in the structure
	 * @param numberOfElevators the number of elevators in the structure
	 */
	public Structure(int numberOfFloors, int numberOfElevators) {
		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;
		timeToggle = false;
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
	 * Indicates whether time has been enabled for the system.
	 *
	 * @return true if travel time and door time have been enabled for the Elevators, false otherwise
	 */
	public boolean timeIsEnabled() {
		return timeToggle;
	}

	/**
	 * Sets the value of the time toggle.
	 *
	 * @param timeToggleValue the value of the time toggle for the system
	 */
	public void setTimeToggle(boolean timeToggleValue) {
		timeToggle = timeToggleValue;
	}


	/**
	 * Initializes the Structure's properties.
	 */
	public void initializeStructure(Presenter presenter) {
		//BoundedBuffer elevatorSubsystemBuffer = new BoundedBuffer();
		//BoundedBuffer floorSubsystemBuffer = new BoundedBuffer();

		Scheduler schedulerClient = new Scheduler(Port.CLIENT_TO_SERVER.getNumber());
		Scheduler schedulerServer = new Scheduler(Port.SERVER_TO_CLIENT.getNumber());

		if (presenter != null){
			schedulerClient.setPresenter(presenter);
		}

		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
		ArrayList<Elevator> elevatorList = new ArrayList<>();
		for (int elevatorNumber = 1; elevatorNumber <= numberOfElevators; elevatorNumber++) {
			Elevator elevator = new Elevator(elevatorNumber, elevatorSubsystem);
			elevatorSubsystem.addElevator(elevator);
			schedulerClient.addElevatorMonitor(elevatorNumber);
			elevatorList.add(elevator);
		}
		elevatorSubsystem.initializeElevators(this);

		FloorSubsystem floorSubsystem = new FloorSubsystem();
		for (int i = 1; i <= numberOfFloors; i++) {
			Floor floor = new Floor(i, floorSubsystem);
			floorSubsystem.addFloor(floor);
		}

		Thread schedulerClientOrigin, schedulerServerOrigin, elevatorSubsystemOrigin, floorSubsystemOrigin;

		schedulerClientOrigin = new Thread(schedulerClient, schedulerClient.getClass().getSimpleName());
		schedulerServerOrigin = new Thread(schedulerServer, schedulerServer.getClass().getSimpleName());
		elevatorSubsystemOrigin = new Thread(elevatorSubsystem, elevatorSubsystem.getClass().getSimpleName());
		floorSubsystemOrigin = new Thread(floorSubsystem, floorSubsystem.getClass().getSimpleName());

		schedulerClientOrigin.start();
		schedulerServerOrigin.start();
		elevatorSubsystemOrigin.start();
		floorSubsystemOrigin.start();
	}

	public static void main(String[] args) {
		Structure structure = new Structure(10, 2);
		structure.initializeStructure(null);
	}
}
