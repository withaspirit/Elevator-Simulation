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

	// Requests for this arrival sensors floor
	ArrayList<ServiceRequest> activeRequests;

	private final int floorNumber;

	/**
	 * The constructor for ArrivalSensor
	 *
	 * @param floorNumber floorNumber from Floor class
	 */
	ArrivalSensor(int floorNumber) {
		this.floorNumber = floorNumber;
		activeRequests = new ArrayList<>();
	}

	/**
	 * Check ApproachEvent's floorNumber with Floor's floorNumber and then allow the elevator to stop
	 *
	 * @param approachEvent the ApproachEvent to be passed to the subsystem
	 */
	public void checkFloorNumber(ApproachEvent approachEvent) {
		if (approachEvent.getFloorNumber() == floorNumber) {
			approachEvent.allowElevatorStop();
		}
	}

	/**
	 * Receive ServiceRequest and add it to the list of activeRequests for this arrival sensor
	 */
	public void addRequest(ServiceRequest request) {
		activeRequests.add(request);
	}

	/**
	 * Compare the ApproachEvent with the ServiceRequests in activeRequests
	 *
	 * @param approachEvent indicates that an elevator is approaching this floor
	 *
	 * @return true if the elevator approaching should stop, false if not
	 */
	public void shouldStop(ApproachEvent approachEvent) {
		for (ServiceRequest request: activeRequests) {
			if (approachEvent.getElevatorNumber() == request.getElevatorNumber() && approachEvent.getDirection() == request.getDirection()
					&& approachEvent.getFloorNumber() == approachEvent.getFloorToVisit() && approachEvent.getFloorNumber() == request.getFloorNumber()){
				// Set boolean in approachEvent that will allow elevator to stop
				approachEvent.allowElevatorStop();
			}
		}
	}
}
