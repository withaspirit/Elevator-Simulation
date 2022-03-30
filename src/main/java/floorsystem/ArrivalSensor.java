package floorsystem;

import requests.ApproachEvent;
import requests.ServiceRequest;
import java.util.ArrayList;

/**
 * Arrival Sensor checks the floorNumber in ApproachEvent is equal to the Floor's floorNumber
 * if Both the values are equal allowElevatorStop method from ApproachEvent is called
 *
 * @author Ramit Mahajan, Brady Norton
 */
public class ArrivalSensor {

	private final int floorNumber;
	ArrayList<ServiceRequest> requestsOnFloor;

	/**
	 * The constructor for ArrivalSensor.
	 *
	 * @param floorNumber floorNumber from Floor class
	 */
	ArrivalSensor(int floorNumber) {
		this.floorNumber = floorNumber;
		requestsOnFloor = new ArrayList<>();
	}

	/**
	 * Check ApproachEvent's floorNumber with Floor's floorNumber and then allow the elevator to stop.
	 *
	 * @param approachEvent the ApproachEvent to be passed to the subsystem
	 */
	public void checkFloorNumber(ApproachEvent approachEvent) {
		if (approachEvent.getFloorNumber() == floorNumber) {
			approachEvent.allowElevatorStop();
		}
	}

	/**
	 * Receive ServiceRequest and add it to the list of requestsOnFloor for this arrival sensor.
	 */
	public void addRequest(ServiceRequest request) {
		requestsOnFloor.add(request);
	}

	/**
	 * Remove ServiceRequest from requestsOnFloor list
	 */
	public void removeRequest(ServiceRequest request) {
		// Iterate over each ServiceRequest in requestsOnFloor
		// Remove the request from the list if it's found in the list
		requestsOnFloor.removeIf(request::equals);
	}

	/**
	 * Compare the ApproachEvent with the ServiceRequests in requestsOnFloor.
	 *
	 * @param approachEvent indicates that an elevator is approaching this floor
	 */
	public void compareToListOfRequests(ApproachEvent approachEvent) {
		for (ServiceRequest request: requestsOnFloor) {
			if (approachEvent.getElevatorNumber() == request.getElevatorNumber() &&
					approachEvent.getDirection() == request.getDirection() &&
					approachEvent.getFloorNumber() == approachEvent.getFloorToVisit() &&
					approachEvent.getFloorNumber() == request.getFloorNumber()) {
				// Set boolean in approachEvent that will allow elevator to stop
				approachEvent.allowElevatorStop();
			}
		}
	}

	/**
	 * Gets the size of the requestsOnFloor list
	 *
	 * @return the number of ServiceRequests on this floor
	 */
	public int getRequestsOnFloorSize() {
		return requestsOnFloor.size();
	}
}
