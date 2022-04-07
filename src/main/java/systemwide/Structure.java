package systemwide;

import client_server_host.Port;
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
	private int elevatorTime;
	private int doorsTime;

	/**
	 * Constructor for Structure.
	 *
	 * @param numberOfFloors the number of floors in the structure
	 * @param numberOfElevators the number of elevators in the structure
	 * @param elevatorTime the time for an Elevator to wait at or travel to a floor
	 * @param doorsTime the time for doors in the simulation to open and close
	 */
	public Structure(int numberOfFloors, int numberOfElevators, int elevatorTime, int doorsTime) {
		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;
		this.elevatorTime = elevatorTime;
		this.doorsTime = doorsTime;
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
	 * Returns the Elevator's travel and floor waiting time.
	 *
	 * @return the travel and floor waiting time for the Elevator
	 */
	public int getElevatorTime() {
		return elevatorTime;
	}

	/**
	 * Sets the value of the Elevator's travel and floor waiting time.
	 *
	 * @param time the new value for the elevatorTime
	 */
	public void setElevatorTime(int time) {
		elevatorTime = time;
	}

	/**
	 * Returns the time for doors to open and close.
	 *
	 * @return time for the doors to open and close
	 */
	public int getElevatorDoorTime() {
		return doorsTime;
	}

	/**
	 * Sets the time it takes for doors to open and close.
	 *
	 * @param time the new time value for the opening and closing time of the doors
	 */
	public void setDoorsTime(int time) {
		doorsTime = time;
	}

	/**
	 * Initializes the Structure's properties.
	 */
	public void initializeStructure() {

		Scheduler schedulerClient = new Scheduler(Port.CLIENT_TO_SERVER.getNumber());
		Scheduler schedulerServer = new Scheduler(Port.SERVER_TO_CLIENT.getNumber());

		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
		ArrayList<Elevator> elevatorList = new ArrayList<>();
		for (int elevatorNumber = 1; elevatorNumber <= numberOfElevators; elevatorNumber++) {
			Elevator elevator = new Elevator(elevatorNumber, elevatorSubsystem);
			elevatorSubsystem.addElevator(elevator);
			schedulerClient.addElevatorMonitor(elevatorNumber);
			elevatorList.add(elevator);
		}
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

		// Start elevator Origins
		for (int i = 0; i < numberOfElevators; i++) {
			(new Thread(elevatorList.get(i), elevatorList.get(i).getClass().getSimpleName())).start();
		}
	}

	public static void main(String[] args) {
		Structure structure = new Structure(10, 2, -1, -1);
		structure.initializeStructure();
	}
}
