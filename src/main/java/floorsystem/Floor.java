package floorsystem;

import requests.ApproachEvent;
import requests.ServiceRequest;
import requests.SubsystemPasser;
import systemwide.Direction;

/**
 * Floor simulates a level of a structure that an elevator can visit.
 *
 * @author Liam Tripp
 */
public class Floor implements SubsystemPasser {

	private final int floorNumber;
	private final FloorSubsystem floorSubsystem;
	private final ArrivalSensor arrivalSensor;

	public Floor(int floorNumber, FloorSubsystem floorSubsystem) {
		this.floorNumber = floorNumber;
		this.floorSubsystem = floorSubsystem;
		arrivalSensor = new ArrivalSensor(floorNumber);
		// createButtons(floorNumber);
	}

	/* public void createButtons(int floorNumber, Scheduler scheduler)
	 *
	 * if (floorNumber == bottom || floorNumber == top) {
	 *     buttons = new ___[1];
	 * } else {
	 * 		buttons = new ___[2];
	 * }
	 * buttons.forEach(button -> button.addActionListener(...));
	 *
	 */

	/**
	 * Passes an ApproachEvent to the corresponding subsystem.
	 *
	 * @param approachEvent the ApproachEvent that is examined by the ArrivalSensor
	 */
	public void passApproachEvent(ApproachEvent approachEvent) {
		floorSubsystem.handleApproachEvent(approachEvent);
	}

	/**
	 * Receives an ApproachEvent from the Subsystem and returns it to the component.
	 *
	 * @param approachEvent the ApproachEvent to be received from the Subsystem
	 */
	public void receiveApproachEvent(ApproachEvent approachEvent) {
		arrivalSensor.compareToListOfRequests(approachEvent);
	}

	/**
	 * Adds a floor request to the list of requests held in the ArrivalSensor
	 *
	 * @param serviceRequest the floor request to be added
	 */
	public void addRequestToSensor(ServiceRequest serviceRequest) { arrivalSensor.addRequest(serviceRequest); }

	/**
	 * Removes a floor request from the list of requests held in the ArrivalSensor
	 *
	 * @param serviceRequest the floor request to be removed
	 */
	public void removeRequestFromSensor(ServiceRequest serviceRequest) { arrivalSensor.removeRequest(serviceRequest); }

	/**
	 * Gets the number of requests the floor has active
	 *
	 * @return the number of requests on this floor
	 */
	public int getNumberOfRequests() { return arrivalSensor.getRequestsOnFloorSize(); }

	/**
	 * Gets the number of this Floor
	 *
	 * @return the number of this floor as int
	 */
	public int getFloorNumber() { return floorNumber; }

	/**
	 * Checks if any of the ServiceRequests held the floors requestsOnFloor list have
	 * an elevator assigned to it that matches the requested elevatorNumber
	 *
	 * @param elevatorNumber the elevator number of the requested elevator
	 * @return true if the ArrivalSensor is expecting the given elevator number, false otherwise
	 */
	public boolean isElevatorExpected(int elevatorNumber, Direction elevatorDirection) {
		for (ServiceRequest serviceRequest : arrivalSensor.requestsOnFloor) {
			if (serviceRequest.getElevatorNumber() == elevatorNumber && serviceRequest.getDirection() == elevatorDirection) {
				return true;
			}
		}
		return false;
	}
}
