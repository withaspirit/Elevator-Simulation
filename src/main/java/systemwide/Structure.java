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
 * Structure contains information to initialize the simulation.
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Structure implements Serializable {

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
	public int getDoorsTime() {
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
}
