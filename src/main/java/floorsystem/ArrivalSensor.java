package floorsystem;
import requests.*;

/**
 * Arrival Sensor checks the floorNumber in ApproachEvent is equal to the Floor's floorNumber
 * if Both the values are equal allowElevatorStop method from ApproachEvent is called 
 * 
 * @author Ramit Mahajan
 */
public class ArrivalSensor {
	
	private int floorNumber;
	
	/**
	* The constructor for ArrivalSensor
	* 
	* @param floorNumber floorNumber from Floor class 
	*/		
	ArrivalSensor(int floorNumber) {
		this.floorNumber = floorNumber;		
	}
	
	/**
	*Check ApproachEvent's floorNumber with Floor's floorNumber and then allow the elevator to stop
	*
	*@param approachEvent the ApproachEvent to be passed to the subsystem
	*/
	public void checkFloorNumber(ApproachEvent approachEvent) {
		if(approachEvent.getFloorNumber() == floorNumber) {
			approachEvent.allowElevatorStop();		
		}
	}
}
