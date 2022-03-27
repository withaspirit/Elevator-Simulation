package floorsystem;

import requests.ApproachEvent;
import requests.ServiceRequest;

import java.util.ArrayList;

/**
 * Arrival Sensor checks the floorNumber in ApproachEvent is equal to the Floor's floorNumber
 * if Both the values are equal allowElevatorStop method from ApproachEvent is called
 *
 * @author Ramit Mahajan
 */
public class ArrivalSensor{

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
	public void addRequest(ServiceRequest request){
		activeRequests.add(request);
	}

	/**
	 * Compare the ApproachEvent with the ServiceRequests in activeRequests
	 *
	 * @param approachEvent indicates that an elevator is approaching this floor
	 *
	 * @return true if the elevator approaching should stop, false if not
	 */
	public boolean shouldStop(ApproachEvent approachEvent){
		if(!activeRequests.isEmpty()){
			for(int i=0; i<activeRequests.size(); i++){
				if(approachEvent.getElevatorNumber() == activeRequests.get(i).getElevatorNumber()){
					if(approachEvent.getDirection() == activeRequests.get(i).getDirection()){
						if(approachEvent.getFloorNumber() == approachEvent.getFloorToVisit() && approachEvent.getFloorNumber() == activeRequests.get(i).getFloorNumber()){
							// Should this be called? or just return true/false?
							approachEvent.allowElevatorStop();
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
